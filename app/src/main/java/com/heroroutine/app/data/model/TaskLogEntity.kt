package com.heroroutine.app.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "task_logs",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["taskId"]),
        Index(value = ["dateEpochDay"])
    ]
)
data class TaskLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val taskId: Long,
    val dateEpochDay: Long, // LocalDate.now().toEpochDay()
    val timestamp: Long = System.currentTimeMillis(),
    val completed: Boolean = true,
    val repsAchieved: Int = 0,
    val durationSecondsAchieved: Int = 0
)
