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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

enum class AppMode {
    CHAMPION,
    GUARDIAN
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as HeroRoutineApp).repository
    val audioPlayer = AudioPlayerHelper(application)

    private val _currentMode = MutableStateFlow(AppMode.CHAMPION)
    val currentMode: StateFlow<AppMode> = _currentMode.asStateFlow()

    private val _selectedSessionId = MutableStateFlow<Long?>(null)
    val selectedSessionId: StateFlow<Long?> = _selectedSessionId.asStateFlow()

    private val _showCelebration = MutableStateFlow(false)
    val showCelebration: StateFlow<Boolean> = _showCelebration.asStateFlow()

    val sessions: StateFlow<List<SessionEntity>> = repository.allSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTasks: StateFlow<List<TaskEntity>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPhotos: StateFlow<List<ProgressPhotoEntity>> = repository.allPhotos
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
            repository.toggleTaskCompletion(task, todayEpoch, isCompleted)

            if (isCompleted) {
                // Check if all or >= 60% of daily tasks are now completed
                val currentLogs = repository.getLogsForDay(todayEpoch)
                // Trigger celebration
                _showCelebration.value = true
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
        viewModelScope.launch {
            repository.updateSession(session)
        }
    }

    fun deleteSession(session: SessionEntity) {
        viewModelScope.launch {
            repository.deleteSession(session)
        }
    }

    fun createTask(
        sessionId: Long,
        title: String,
        taskType: TaskType,
        targetReps: Int,
        targetDurationSeconds: Int,
        audioFilePath: String?,
        referencePhotoPath: String?,
        notes: String?
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
                    notes = notes
                )
            )
        }
    }

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
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
        viewModelScope.launch {
            repository.deleteProgressPhoto(photo)
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.stopAudio()
    }
}
