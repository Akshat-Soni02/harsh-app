package com.heroroutine.app.media

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.File

class AudioRecorderHelper(private val context: Context) {

    private var recorder: MediaRecorder? = null
    private var currentOutputFile: File? = null
    var isRecording: Boolean = false
        private set

    fun startRecording(): File? {
        if (isRecording) return currentOutputFile

        val outputFile = MediaFileManager.createAudioFile(context)
        currentOutputFile = outputFile

        try {
            recorder = (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }).apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128000)
                setAudioSamplingRate(44100)
                setOutputFile(outputFile.absolutePath)
                prepare()
                start()
            }
            isRecording = true
            return outputFile
        } catch (e: Exception) {
            e.printStackTrace()
            recorder?.release()
            recorder = null
            isRecording = false
            currentOutputFile?.delete()
            currentOutputFile = null
            return null
        }
    }

    fun stopRecording(): String? {
        if (!isRecording) return null

        return try {
            recorder?.apply {
                stop()
                reset()
                release()
            }
            isRecording = false
            val path = currentOutputFile?.absolutePath
            currentOutputFile = null
            path
        } catch (e: Exception) {
            e.printStackTrace()
            isRecording = false
            currentOutputFile?.delete()
            currentOutputFile = null
            null
        } finally {
            recorder = null
        }
    }

    fun cancelRecording() {
        try {
            if (isRecording) {
                recorder?.apply {
                    stop()
                    reset()
                    release()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            recorder = null
            isRecording = false
            currentOutputFile?.delete()
            currentOutputFile = null
        }
    }
}
