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

    fun setHinglish(value: Boolean) {
        prefs.edit().putBoolean("is_hinglish", value).apply()
        _isHinglish.value = value
    }

    fun setHasSeenAppTour(value: Boolean) {
        prefs.edit().putBoolean("has_seen_app_tour", value).apply()
        _hasSeenAppTour.value = value
    }
}
