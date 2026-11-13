package com.heroroutine.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.heroroutine.app.data.model.TaskLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskLogDao {
    @Query("SELECT * FROM task_logs WHERE dateEpochDay = :epochDay")
    fun getLogsForDay(epochDay: Long): Flow<List<TaskLogEntity>>

    @Query("SELECT * FROM task_logs WHERE taskId = :taskId ORDER BY timestamp ASC")
    fun getLogsForTask(taskId: Long): Flow<List<TaskLogEntity>>

    @Query("SELECT * FROM task_logs ORDER BY timestamp ASC")
    fun getAllLogs(): Flow<List<TaskLogEntity>>

    @Query("SELECT * FROM task_logs ORDER BY timestamp ASC")
    suspend fun getAllLogsList(): List<TaskLogEntity>

    @Query("SELECT * FROM task_logs WHERE taskId = :taskId AND dateEpochDay = :epochDay LIMIT 1")
    suspend fun getLogForTaskOnDay(taskId: Long, epochDay: Long): TaskLogEntity?

    @Query("SELECT DISTINCT dateEpochDay FROM task_logs WHERE completed = 1")
    fun getAllCompletedEpochDays(): Flow<List<Long>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: TaskLogEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogs(logs: List<TaskLogEntity>)

    @Query("DELETE FROM task_logs WHERE taskId = :taskId AND dateEpochDay = :epochDay")
    suspend fun deleteLogForTaskOnDay(taskId: Long, epochDay: Long)

    @Query("SELECT COUNT(*) FROM task_logs WHERE completed = 1 AND dateEpochDay = :epochDay")
    suspend fun getCompletedCountForDay(epochDay: Long): Int

    @Query("DELETE FROM task_logs")
    suspend fun clearAllLogs()
}
