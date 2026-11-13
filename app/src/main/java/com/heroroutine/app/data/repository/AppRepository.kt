package com.heroroutine.app.data.repository

import com.heroroutine.app.data.db.AppDatabase
import com.heroroutine.app.data.model.ProgressPhotoEntity
import com.heroroutine.app.data.model.SessionEntity
import com.heroroutine.app.data.model.TaskEntity
import com.heroroutine.app.data.model.TaskLogEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import java.time.LocalDate

data class StreakInfo(
    val currentStreak: Int,
    val bestStreak: Int,
    val completedToday: Boolean,
    val todayCompletionPercentage: Float
)

class AppRepository(private val database: AppDatabase) {

    val allSessions: Flow<List<SessionEntity>> = database.sessionDao().getAllSessions()
    val allTasks: Flow<List<TaskEntity>> = database.taskDao().getAllTasks()
    val allPhotos: Flow<List<ProgressPhotoEntity>> = database.progressPhotoDao().getAllPhotos()
    val allLogs: Flow<List<TaskLogEntity>> = database.taskLogDao().getAllLogs()

    fun getTasksForSession(sessionId: Long): Flow<List<TaskEntity>> =
        database.taskDao().getTasksForSession(sessionId)

    fun getLogsForDay(epochDay: Long): Flow<List<TaskLogEntity>> =
        database.taskLogDao().getLogsForDay(epochDay)

    fun getLogsForTask(taskId: Long): Flow<List<TaskLogEntity>> =
        database.taskLogDao().getLogsForTask(taskId)

    fun getPhotosForTask(taskId: Long): Flow<List<ProgressPhotoEntity>> =
        database.progressPhotoDao().getPhotosForTask(taskId)

    suspend fun insertSession(session: SessionEntity): Long =
        database.sessionDao().insertSession(session)

    suspend fun updateSession(session: SessionEntity) =
        database.sessionDao().updateSession(session)

    suspend fun deleteSession(session: SessionEntity) =
        database.sessionDao().deleteSession(session)

    suspend fun insertTask(task: TaskEntity): Long =
        database.taskDao().insertTask(task)

    suspend fun updateTask(task: TaskEntity) =
        database.taskDao().updateTask(task)

    suspend fun deleteTask(task: TaskEntity) =
        database.taskDao().deleteTask(task)

    suspend fun toggleTaskCompletion(task: TaskEntity, epochDay: Long, isCompleted: Boolean) {
        val existing = database.taskLogDao().getLogForTaskOnDay(task.id, epochDay)
        if (isCompleted) {
            val log = TaskLogEntity(
                taskId = task.id,
                dateEpochDay = epochDay,
                timestamp = System.currentTimeMillis(),
                completed = true,
                repsAchieved = task.targetReps,
                durationSecondsAchieved = task.targetDurationSeconds
            )
            database.taskLogDao().insertLog(log)
        } else {
            database.taskLogDao().deleteLogForTaskOnDay(task.id, epochDay)
        }
    }

    suspend fun insertProgressPhoto(photo: ProgressPhotoEntity): Long =
        database.progressPhotoDao().insertPhoto(photo)

    suspend fun deleteProgressPhoto(photo: ProgressPhotoEntity) =
        database.progressPhotoDao().deletePhoto(photo)

    fun calculateStreakFlow(): Flow<StreakInfo> {
        val todayEpoch = LocalDate.now().toEpochDay()

        return combine(allTasks, database.taskLogDao().getAllCompletedEpochDays()) { tasks, _ ->
            if (tasks.isEmpty()) {
                return@combine StreakInfo(0, 0, false, 0f)
            }

            val totalTaskCount = tasks.size
            val threshold = (totalTaskCount * 0.6f).coerceAtLeast(1f)

            var streak = 0
            var checkDay = todayEpoch
            var completedToday = false
            var todayPercent = 0f

            val todayCount = database.taskLogDao().getCompletedCountForDay(todayEpoch)
            todayPercent = (todayCount.toFloat() / totalTaskCount.toFloat()).coerceIn(0f, 1f)
            if (todayCount >= threshold) {
                completedToday = true
                streak++
                checkDay--
            } else {
                checkDay--
            }

            while (true) {
                val dayCount = database.taskLogDao().getCompletedCountForDay(checkDay)
                if (dayCount >= threshold) {
                    streak++
                    checkDay--
                } else {
                    break
                }
            }

            StreakInfo(
                currentStreak = streak,
                bestStreak = streak.coerceAtLeast(1),
                completedToday = completedToday,
                todayCompletionPercentage = todayPercent
            )
        }
    }
}
