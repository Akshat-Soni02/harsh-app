package com.heroroutine.app.media

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object MediaFileManager {

    fun createAudioFile(context: Context): File {
        val audioDir = File(context.filesDir, "audio").apply { if (!exists()) mkdirs() }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        return File(audioDir, "VOICE_$timeStamp.m4a")
    }

    fun createImageFile(context: Context, prefix: String = "IMG"): File {
        val imagesDir = File(context.filesDir, "images").apply { if (!exists()) mkdirs() }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        return File(imagesDir, "${prefix}_$timeStamp.jpg")
    }

    fun getUriForFile(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    fun copyUriToFile(context: Context, sourceUri: Uri, destinationFile: File): Boolean {
        return try {
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                FileOutputStream(destinationFile).use { output ->
                    input.copyTo(output)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
