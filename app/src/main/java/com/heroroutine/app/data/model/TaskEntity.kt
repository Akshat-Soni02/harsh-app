package com.heroroutine.app.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = SessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["sessionId"])]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sessionId: Long,
    val title: String,
    val taskType: TaskType = TaskType.EXERCISE,
    val targetReps: Int = 10,
    val targetDurationSeconds: Int = 0,
    val audioFilePath: String? = null,
    val referencePhotoPath: String? = null,
    val orderIndex: Int = 0,
    val notes: String? = null,
    val bodyParts: String = "" // Comma separated list of body parts
)
