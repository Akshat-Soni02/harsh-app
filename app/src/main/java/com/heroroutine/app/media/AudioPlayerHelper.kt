package com.heroroutine.app.media

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

class AudioPlayerHelper(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private var currentlyPlayingPath: String? = null

    fun playAudio(filePath: String, onCompletion: () -> Unit = {}) {
        val file = File(filePath)
        if (!file.exists()) return

        stopAudio()

        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(filePath)
                prepare()
                start()
                _isPlaying.value = true
                currentlyPlayingPath = filePath

                setOnCompletionListener {
                    _isPlaying.value = false
                    currentlyPlayingPath = null
                    onCompletion()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _isPlaying.value = false
            currentlyPlayingPath = null
        }
    }

    fun isPathPlaying(filePath: String): Boolean {
        return _isPlaying.value && currentlyPlayingPath == filePath
    }

    fun stopAudio() {
        try {
            mediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                reset()
                release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mediaPlayer = null
            _isPlaying.value = false
            currentlyPlayingPath = null
        }
    }
}
