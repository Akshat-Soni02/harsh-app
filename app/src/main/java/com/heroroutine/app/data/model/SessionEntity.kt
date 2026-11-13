package com.heroroutine.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val iconEmoji: String = "⚡",
    val orderIndex: Int = 0,
    val targetTime: String? = null // e.g. "09:00"
)
