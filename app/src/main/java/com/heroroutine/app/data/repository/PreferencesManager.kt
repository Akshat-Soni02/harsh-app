package com.heroroutine.app.data.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("shabaash_prefs", Context.MODE_PRIVATE)

    private val _isHinglish = MutableStateFlow(prefs.getBoolean("is_hinglish", true))
    val isHinglish: StateFlow<Boolean> = _isHinglish.asStateFlow()

    private val _hasSeenAppTour = MutableStateFlow(prefs.getBoolean("has_seen_app_tour", false))
    val hasSeenAppTour: StateFlow<Boolean> = _hasSeenAppTour.asStateFlow()

    private val _isDailyKnowledgeEnabled = MutableStateFlow(prefs.getBoolean("daily_knowledge_enabled", true))
    val isDailyKnowledgeEnabled: StateFlow<Boolean> = _isDailyKnowledgeEnabled.asStateFlow()

    private val _customBodyParts = MutableStateFlow<Set<String>>(
        prefs.getStringSet("custom_body_parts", emptySet())?.toSet() ?: emptySet()
    )
    val customBodyParts: StateFlow<Set<String>> = _customBodyParts.asStateFlow()

    fun setHinglish(value: Boolean) {
        prefs.edit().putBoolean("is_hinglish", value).apply()
        _isHinglish.value = value
    }

    fun setHasSeenAppTour(value: Boolean) {
        prefs.edit().putBoolean("has_seen_app_tour", value).apply()
        _hasSeenAppTour.value = value
    }

    fun setDailyKnowledgeEnabled(value: Boolean) {
        prefs.edit().putBoolean("daily_knowledge_enabled", value).apply()
        _isDailyKnowledgeEnabled.value = value
    }

    fun addCustomBodyPart(name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        val current = _customBodyParts.value.toMutableSet()
        current.add(trimmed)
        prefs.edit().putStringSet("custom_body_parts", current).apply()
        _customBodyParts.value = current
    }

    fun removeCustomBodyPart(name: String) {
        val trimmed = name.trim()
        val current = _customBodyParts.value.toMutableSet()
        current.remove(trimmed)
        prefs.edit().putStringSet("custom_body_parts", current).apply()
        _customBodyParts.value = current
    }
}
