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

    fun setHinglish(value: Boolean) {
        prefs.edit().putBoolean("is_hinglish", value).apply()
        _isHinglish.value = value
    }
}
