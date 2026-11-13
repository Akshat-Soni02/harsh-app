package com.heroroutine.app.data.backup

import android.content.Context
import com.heroroutine.app.data.db.AppDatabase
import com.heroroutine.app.data.model.ProgressPhotoEntity
import com.heroroutine.app.data.model.SessionEntity
import com.heroroutine.app.data.model.TaskEntity
import com.heroroutine.app.data.model.TaskLogEntity
import com.heroroutine.app.data.model.TaskType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.StandardCharsets
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

data class BackupStats(
    val sessionCount: Int,
    val taskCount: Int,
    val logCount: Int,
    val photoCount: Int
)

class BackupManager(private val context: Context) {

    private val db = AppDatabase.getDatabase(context)

    suspend fun exportBackup(outputStream: OutputStream): Result<BackupStats> = withContext(Dispatchers.IO) {
        try {
            val sessions = db.sessionDao().getAllSessionsList()
            val tasks = db.taskDao().getAllTasksList()
            val logs = db.taskLogDao().getAllLogsList()
            val photos = db.progressPhotoDao().getAllPhotosList()

            val rootJson = JSONObject().apply {
                put("version", 1)
                put("timestamp", System.currentTimeMillis())

                // Sessions
                val sessionsArray = JSONArray()
                sessions.forEach { s ->
                    val obj = JSONObject().apply {
                        put("id", s.id)
                        put("title", s.title)
                        put("iconEmoji", s.iconEmoji)
                        put("orderIndex", s.orderIndex)
                        put("targetTime", s.targetTime ?: JSONObject.NULL)
                    }
                    sessionsArray.put(obj)
                }
                put("sessions", sessionsArray)

                // Tasks
                val tasksArray = JSONArray()
                tasks.forEach { t ->
                    val obj = JSONObject().apply {
                        put("id", t.id)
                        put("sessionId", t.sessionId)
                        put("title", t.title)
                        put("taskType", t.taskType.name)
                        put("targetReps", t.targetReps)
                        put("targetSets", t.targetSets)
                        put("targetDurationSeconds", t.targetDurationSeconds)
                        put("audioFilePath", t.audioFilePath?.let { File(it).name } ?: JSONObject.NULL)
                        put("referencePhotoPath", t.referencePhotoPath?.let { File(it).name } ?: JSONObject.NULL)
                        put("orderIndex", t.orderIndex)
                        put("notes", t.notes ?: JSONObject.NULL)
                        put("bodyParts", t.bodyParts)
                    }
                    tasksArray.put(obj)
                }
                put("tasks", tasksArray)

                // Task Logs (History & Charts)
                val logsArray = JSONArray()
                logs.forEach { l ->
                    val obj = JSONObject().apply {
                        put("id", l.id)
                        put("taskId", l.taskId)
                        put("dateEpochDay", l.dateEpochDay)
                        put("timestamp", l.timestamp)
                        put("completed", l.completed)
                        put("repsAchieved", l.repsAchieved)
                        put("durationSecondsAchieved", l.durationSecondsAchieved)
                    }
                    logsArray.put(obj)
                }
                put("task_logs", logsArray)

                // Progress Photos
                val photosArray = JSONArray()
                photos.forEach { p ->
                    val obj = JSONObject().apply {
                        put("id", p.id)
                        put("taskId", p.taskId)
                        put("photoPath", File(p.photoPath).name)
                        put("timestamp", p.timestamp)
                        put("notes", p.notes ?: JSONObject.NULL)
                    }
                    photosArray.put(obj)
                }
                put("progress_photos", photosArray)
            }

            ZipOutputStream(BufferedOutputStream(outputStream)).use { zipOut ->
                // 1. Write backup_data.json
                val jsonEntry = ZipEntry("backup_data.json")
                zipOut.putNextEntry(jsonEntry)
                zipOut.write(rootJson.toString(2).toByteArray(StandardCharsets.UTF_8))
                zipOut.closeEntry()

                // 2. Package audio files
                val audioDir = File(context.filesDir, "audio")
                if (audioDir.exists() && audioDir.isDirectory) {
                    audioDir.listFiles()?.forEach { file ->
                        if (file.isFile) {
                            val entry = ZipEntry("audio/${file.name}")
                            zipOut.putNextEntry(entry)
                            FileInputStream(file).use { it.copyTo(zipOut) }
                            zipOut.closeEntry()
                        }
                    }
                }

                // 3. Package images
                val imagesDir = File(context.filesDir, "images")
                if (imagesDir.exists() && imagesDir.isDirectory) {
                    imagesDir.listFiles()?.forEach { file ->
                        if (file.isFile) {
                            val entry = ZipEntry("images/${file.name}")
                            zipOut.putNextEntry(entry)
                            FileInputStream(file).use { it.copyTo(zipOut) }
                            zipOut.closeEntry()
                        }
                    }
                }
            }

            Result.success(
                BackupStats(
                    sessionCount = sessions.size,
                    taskCount = tasks.size,
                    logCount = logs.size,
                    photoCount = photos.size
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun importBackup(inputStream: InputStream): Result<BackupStats> = withContext(Dispatchers.IO) {
        try {
            var jsonContent: String? = null
            val audioDir = File(context.filesDir, "audio").apply { if (!exists()) mkdirs() }
            val imagesDir = File(context.filesDir, "images").apply { if (!exists()) mkdirs() }

            ZipInputStream(BufferedInputStream(inputStream)).use { zipIn ->
                var entry = zipIn.nextEntry
                while (entry != null) {
                    when {
                        entry.name == "backup_data.json" -> {
                            jsonContent = zipIn.bufferedReader(StandardCharsets.UTF_8).readText()
                        }
                        entry.name.startsWith("audio/") -> {
                            val fileName = entry.name.removePrefix("audio/")
                            if (fileName.isNotEmpty()) {
                                val dest = File(audioDir, fileName)
                                FileOutputStream(dest).use { zipIn.copyTo(it) }
                            }
                        }
                        entry.name.startsWith("images/") -> {
                            val fileName = entry.name.removePrefix("images/")
                            if (fileName.isNotEmpty()) {
                                val dest = File(imagesDir, fileName)
                                FileOutputStream(dest).use { zipIn.copyTo(it) }
                            }
                        }
                    }
                    zipIn.closeEntry()
                    entry = zipIn.nextEntry
                }
            }

            if (jsonContent.isNullOrBlank()) {
                return@withContext Result.failure(IllegalStateException("Invalid backup file: backup_data.json missing"))
            }

            val rootJson = JSONObject(jsonContent!!)

            // Parse Sessions
            val sessionsArray = rootJson.optJSONArray("sessions") ?: JSONArray()
            val restoredSessions = mutableListOf<SessionEntity>()
            for (i in 0 until sessionsArray.length()) {
                val obj = sessionsArray.getJSONObject(i)
                restoredSessions.add(
                    SessionEntity(
                        id = obj.optLong("id", 0L),
                        title = obj.getString("title"),
                        iconEmoji = obj.optString("iconEmoji", "⚡"),
                        orderIndex = obj.optInt("orderIndex", 0),
                        targetTime = if (obj.isNull("targetTime")) null else obj.getString("targetTime")
                    )
                )
            }

            // Parse Tasks
            val tasksArray = rootJson.optJSONArray("tasks") ?: JSONArray()
            val restoredTasks = mutableListOf<TaskEntity>()
            for (i in 0 until tasksArray.length()) {
                val obj = tasksArray.getJSONObject(i)
                val audioFileName = if (obj.isNull("audioFilePath")) null else obj.getString("audioFilePath")
                val photoFileName = if (obj.isNull("referencePhotoPath")) null else obj.getString("referencePhotoPath")

                val fullAudioPath = audioFileName?.let { File(audioDir, it).absolutePath }
                val fullPhotoPath = photoFileName?.let { File(imagesDir, it).absolutePath }

                restoredTasks.add(
                    TaskEntity(
                        id = obj.optLong("id", 0L),
                        sessionId = obj.getLong("sessionId"),
                        title = obj.getString("title"),
                        taskType = try { TaskType.valueOf(obj.getString("taskType")) } catch (e: Exception) { TaskType.EXERCISE },
                        targetReps = obj.optInt("targetReps", 10),
                        targetSets = obj.optInt("targetSets", 0),
                        targetDurationSeconds = obj.optInt("targetDurationSeconds", 0),
                        audioFilePath = fullAudioPath,
                        referencePhotoPath = fullPhotoPath,
                        orderIndex = obj.optInt("orderIndex", 0),
                        notes = if (obj.isNull("notes")) null else obj.getString("notes"),
                        bodyParts = obj.optString("bodyParts", "")
                    )
                )
            }

            // Parse Logs (Chart history!)
            val logsArray = rootJson.optJSONArray("task_logs") ?: JSONArray()
            val restoredLogs = mutableListOf<TaskLogEntity>()
            for (i in 0 until logsArray.length()) {
                val obj = logsArray.getJSONObject(i)
                restoredLogs.add(
                    TaskLogEntity(
                        id = obj.optLong("id", 0L),
                        taskId = obj.getLong("taskId"),
                        dateEpochDay = obj.getLong("dateEpochDay"),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                        completed = obj.optBoolean("completed", true),
                        repsAchieved = obj.optInt("repsAchieved", 0),
                        durationSecondsAchieved = obj.optInt("durationSecondsAchieved", 0)
                    )
                )
            }

            // Parse Photos
            val photosArray = rootJson.optJSONArray("progress_photos") ?: JSONArray()
            val restoredPhotos = mutableListOf<ProgressPhotoEntity>()
            for (i in 0 until photosArray.length()) {
                val obj = photosArray.getJSONObject(i)
                val photoFileName = obj.getString("photoPath")
                val fullPhotoPath = File(imagesDir, photoFileName).absolutePath
                restoredPhotos.add(
                    ProgressPhotoEntity(
                        id = obj.optLong("id", 0L),
                        taskId = obj.getLong("taskId"),
                        photoPath = fullPhotoPath,
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                        notes = if (obj.isNull("notes")) null else obj.getString("notes")
                    )
                )
            }

            // Insert into Room
            if (restoredSessions.isNotEmpty()) db.sessionDao().insertSessions(restoredSessions)
            if (restoredTasks.isNotEmpty()) db.taskDao().insertTasks(restoredTasks)
            if (restoredLogs.isNotEmpty()) db.taskLogDao().insertLogs(restoredLogs)
            if (restoredPhotos.isNotEmpty()) db.progressPhotoDao().insertPhotos(restoredPhotos)

            Result.success(
                BackupStats(
                    sessionCount = restoredSessions.size,
                    taskCount = restoredTasks.size,
                    logCount = restoredLogs.size,
                    photoCount = restoredPhotos.size
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
