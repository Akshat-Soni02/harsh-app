package com.heroroutine.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.heroroutine.app.HeroRoutineApp
import com.heroroutine.app.alarm.AlarmScheduler
import com.heroroutine.app.data.backup.BackupManager
import com.heroroutine.app.data.backup.BackupStats
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
import java.io.InputStream
import java.io.OutputStream
import java.time.LocalDate

enum class AppMode {
    CHAMPION,
    PARENT
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as HeroRoutineApp).repository
    val prefsManager = (application as HeroRoutineApp).preferencesManager
    val audioPlayer = AudioPlayerHelper(application)
    val backupManager = BackupManager(application)

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
    val hasSeenAppTour: StateFlow<Boolean> = prefsManager.hasSeenAppTour
    val isDailyKnowledgeEnabled: StateFlow<Boolean> = prefsManager.isDailyKnowledgeEnabled
    val customBodyParts: StateFlow<Set<String>> = prefsManager.customBodyParts

    fun setHinglish(enabled: Boolean) {
        prefsManager.setHinglish(enabled)
    }

    fun setHasSeenAppTour(seen: Boolean) {
        prefsManager.setHasSeenAppTour(seen)
    }

    fun setDailyKnowledgeEnabled(enabled: Boolean) {
        prefsManager.setDailyKnowledgeEnabled(enabled)
    }

    fun addCustomBodyPart(name: String) {
        prefsManager.addCustomBodyPart(name)
    }

    fun removeCustomBodyPart(name: String) {
        prefsManager.removeCustomBodyPart(name)
        viewModelScope.launch {
            val targetTrimmed = name.trim().lowercase()
            allTasks.value.forEach { task ->
                val currentParts = task.bodyParts.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                val filtered = currentParts.filter { !it.equals(targetTrimmed, ignoreCase = true) }
                if (filtered.size != currentParts.size) {
                    repository.updateTask(task.copy(bodyParts = filtered.joinToString(",")))
                }
            }
        }
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
                delay(300)
                val currentLogs = repository.getLogsForDay(todayEpoch).stateIn(viewModelScope).value
                val sessionTasks = allTasks.value.filter { it.sessionId == task.sessionId }
                val sessionTaskIds = sessionTasks.map { it.id }.toSet()
                val completedTaskIds = currentLogs.filter { it.completed }.map { it.taskId }.toSet()
                
                // If this task completed all tasks in this session
                if (sessionTaskIds.isNotEmpty() && completedTaskIds.containsAll(sessionTaskIds)) {
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

    private fun parseTimeToHourMinute(timeStr: String?): Pair<Int, Int>? {
        if (timeStr.isNullOrBlank()) return null
        return try {
            val trimmed = timeStr.trim().uppercase()
            if (trimmed.contains("AM") || trimmed.contains("PM")) {
                val isPm = trimmed.contains("PM")
                val cleanTime = trimmed.replace("AM", "").replace("PM", "").trim()
                val parts = cleanTime.split(":")
                var hour = parts[0].trim().toInt()
                val min = if (parts.size > 1) parts[1].trim().toInt() else 0
                if (isPm && hour < 12) hour += 12
                if (!isPm && hour == 12) hour = 0
                Pair(hour, min)
            } else if (trimmed.contains(":")) {
                val parts = trimmed.split(":")
                val hour = parts[0].trim().toInt()
                val min = if (parts.size > 1) parts[1].trim().toInt() else 0
                Pair(hour, min)
            } else null
        } catch (e: Exception) {
            null
        }
    }

    // Parent Actions
    fun createSession(title: String, iconEmoji: String, targetTime: String?, alarmEnabled: Boolean = false) {
        viewModelScope.launch {
            val count = sessions.value.size
            val newId = repository.insertSession(
                SessionEntity(
                    title = title,
                    iconEmoji = iconEmoji,
                    orderIndex = count,
                    targetTime = targetTime,
                    alarmEnabled = alarmEnabled
                )
            )
            if (alarmEnabled && !targetTime.isNullOrBlank()) {
                val hm = parseTimeToHourMinute(targetTime)
                if (hm != null) {
                    AlarmScheduler.scheduleDailyAlarm(
                        context = getApplication(),
                        requestCode = newId.toInt(),
                        hour = hm.first,
                        minute = hm.second,
                        title = "Routine: $title",
                        message = "Time for your $title exercises! 🌟"
                    )
                }
            }
        }
    }

    fun updateSession(session: SessionEntity) {
        viewModelScope.launch {
            repository.updateSession(session)
            if (session.alarmEnabled && !session.targetTime.isNullOrBlank()) {
                val hm = parseTimeToHourMinute(session.targetTime)
                if (hm != null) {
                    AlarmScheduler.scheduleDailyAlarm(
                        context = getApplication(),
                        requestCode = session.id.toInt(),
                        hour = hm.first,
                        minute = hm.second,
                        title = "Routine: ${session.title}",
                        message = "Time for your ${session.title} exercises! 🌟"
                    )
                } else {
                    AlarmScheduler.cancelAlarm(getApplication(), session.id.toInt())
                }
            } else {
                AlarmScheduler.cancelAlarm(getApplication(), session.id.toInt())
            }
        }
    }

    fun deleteSession(session: SessionEntity) {
        viewModelScope.launch {
            AlarmScheduler.cancelAlarm(getApplication(), session.id.toInt())
            repository.deleteSession(session)
        }
    }

    fun createTask(
        sessionId: Long,
        title: String,
        taskType: TaskType,
        targetReps: Int,
        targetSets: Int = 0,
        targetDurationSeconds: Int = 0,
        audioFilePath: String?,
        referencePhotoPath: String?,
        notes: String?,
        bodyParts: String,
        isDraft: Boolean = false
    ) {
        viewModelScope.launch {
            val order = allTasks.value.count { it.sessionId == sessionId }
            repository.insertTask(
                TaskEntity(
                    sessionId = sessionId,
                    title = title,
                    taskType = taskType,
                    targetReps = targetReps,
                    targetSets = targetSets,
                    targetDurationSeconds = targetDurationSeconds,
                    audioFilePath = audioFilePath,
                    referencePhotoPath = referencePhotoPath,
                    orderIndex = order,
                    notes = notes,
                    bodyParts = bodyParts,
                    isDraft = isDraft
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

    suspend fun exportBackup(outputStream: OutputStream): Result<BackupStats> {
        return backupManager.exportBackup(outputStream)
    }

    suspend fun importBackup(inputStream: InputStream): Result<BackupStats> {
        return backupManager.importBackup(inputStream)
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.stopAudio()
    }
}
