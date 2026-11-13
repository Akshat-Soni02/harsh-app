package com.heroroutine.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.heroroutine.app.HeroRoutineApp
import com.heroroutine.app.data.model.ProgressPhotoEntity
import com.heroroutine.app.data.model.SessionEntity
import com.heroroutine.app.data.model.TaskEntity
import com.heroroutine.app.data.model.TaskLogEntity
import com.heroroutine.app.data.model.TaskType
import com.heroroutine.app.data.repository.StreakInfo
import com.heroroutine.app.media.AudioPlayerHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

enum class AppMode {
    CHAMPION,
    GUARDIAN
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as HeroRoutineApp).repository
    val prefsManager = (application as HeroRoutineApp).preferencesManager
    val audioPlayer = AudioPlayerHelper(application)

    private val _currentMode = MutableStateFlow(AppMode.CHAMPION)
    val currentMode: StateFlow<AppMode> = _currentMode.asStateFlow()

    private val _selectedSessionId = MutableStateFlow<Long?>(null)
    val selectedSessionId: StateFlow<Long?> = _selectedSessionId.asStateFlow()

    private val _showCelebration = MutableStateFlow(false)
    val showCelebration: StateFlow<Boolean> = _showCelebration.asStateFlow()

    // Key is taskId, Value is timestamp when Undo expires
    private val _pendingUndoTasks = MutableStateFlow<Map<Long, Long>>(emptyMap())
    val pendingUndoTasks: StateFlow<Map<Long, Long>> = _pendingUndoTasks.asStateFlow()

    val isHinglish: StateFlow<Boolean> = prefsManager.isHinglish

    fun setHinglish(enabled: Boolean) {
        prefsManager.setHinglish(enabled)
    }

    val sessions: StateFlow<List<SessionEntity>> = repository.allSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTasks: StateFlow<List<TaskEntity>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPhotos: StateFlow<List<ProgressPhotoEntity>> = repository.allPhotos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allLogs: StateFlow<List<TaskLogEntity>> = repository.allLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val streakInfo: StateFlow<StreakInfo> = repository.calculateStreakFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StreakInfo(0, 0, false, 0f))

    private val todayEpoch = LocalDate.now().toEpochDay()
    val todayLogs: StateFlow<List<TaskLogEntity>> = repository.getLogsForDay(todayEpoch)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun switchMode(mode: AppMode) {
        audioPlayer.stopAudio()
        _currentMode.value = mode
    }

    fun selectSession(sessionId: Long) {
        _selectedSessionId.value = sessionId
    }

    fun toggleTask(task: TaskEntity, isCompleted: Boolean) {
        viewModelScope.launch {
            if (isCompleted) {
                // Add to Undo state
                _pendingUndoTasks.update { it + (task.id to System.currentTimeMillis() + 5000) }
                repository.toggleTaskCompletion(task, todayEpoch, true)

                // Check for celebration (after small delay to let DB update flow)
                delay(500)
                val currentLogs = repository.getLogsForDay(todayEpoch).stateIn(viewModelScope).value
                val sessionTasks = allTasks.value.filter { it.sessionId == selectedSessionId.value }
                val sessionTaskIds = sessionTasks.map { it.id }.toSet()
                val completedSessionTaskIds = currentLogs.filter { it.completed }.map { it.taskId }.toSet()
                
                // If this task completed the whole session
                if (sessionTaskIds.isNotEmpty() && completedSessionTaskIds.containsAll(sessionTaskIds)) {
                    _showCelebration.value = true
                }

                // Cleanup undo state after 5 seconds
                delay(5000)
                _pendingUndoTasks.update { it - task.id }
            } else {
                // Remove from DB immediately (Undo clicked)
                _pendingUndoTasks.update { it - task.id }
                repository.toggleTaskCompletion(task, todayEpoch, false)
            }
        }
    }

    fun dismissCelebration() {
        _showCelebration.value = false
    }

    // Guardian Actions
    fun createSession(title: String, iconEmoji: String, targetTime: String?) {
        viewModelScope.launch {
            val count = sessions.value.size
            repository.insertSession(
                SessionEntity(
                    title = title,
                    iconEmoji = iconEmoji,
                    orderIndex = count,
                    targetTime = targetTime
                )
            )
        }
    }

    fun updateSession(session: SessionEntity) {
        viewModelScope.launch { repository.updateSession(session) }
    }

    fun deleteSession(session: SessionEntity) {
        viewModelScope.launch { repository.deleteSession(session) }
    }

    fun createTask(
        sessionId: Long,
        title: String,
        taskType: TaskType,
        targetReps: Int,
        targetDurationSeconds: Int,
        audioFilePath: String?,
        referencePhotoPath: String?,
        notes: String?,
        bodyParts: String
    ) {
        viewModelScope.launch {
            val order = allTasks.value.count { it.sessionId == sessionId }
            repository.insertTask(
                TaskEntity(
                    sessionId = sessionId,
                    title = title,
                    taskType = taskType,
                    targetReps = targetReps,
                    targetDurationSeconds = targetDurationSeconds,
                    audioFilePath = audioFilePath,
                    referencePhotoPath = referencePhotoPath,
                    orderIndex = order,
                    notes = notes,
                    bodyParts = bodyParts
                )
            )
        }
    }

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch { repository.updateTask(task) }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch { repository.deleteTask(task) }
    }

    fun addProgressPhoto(taskId: Long, photoPath: String, notes: String?) {
        viewModelScope.launch {
            repository.insertProgressPhoto(
                ProgressPhotoEntity(
                    taskId = taskId,
                    photoPath = photoPath,
                    notes = notes
                )
            )
        }
    }

    fun deleteProgressPhoto(photo: ProgressPhotoEntity) {
        viewModelScope.launch { repository.deleteProgressPhoto(photo) }
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.stopAudio()
    }
}
