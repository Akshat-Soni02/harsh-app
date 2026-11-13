package com.heroroutine.app.ui.guardian

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.heroroutine.app.data.model.TaskEntity
import com.heroroutine.app.data.model.TaskType
import com.heroroutine.app.media.AudioPlayerHelper
import com.heroroutine.app.media.AudioRecorderHelper
import com.heroroutine.app.media.MediaFileManager
import com.heroroutine.app.ui.theme.PastelPurple
import com.heroroutine.app.ui.theme.PastelRed
import java.io.File

@Composable
fun TaskEditorDialog(
    sessionId: Long,
    initialTask: TaskEntity? = null,
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        type: TaskType,
        reps: Int,
        durationSeconds: Int,
        audioPath: String?,
        photoPath: String?,
        notes: String?,
        bodyParts: String
    ) -> Unit
) {
    val context = LocalContext.current
    val recorderHelper = remember { AudioRecorderHelper(context) }
    val playerHelper = remember { AudioPlayerHelper(context) }

    var title by remember { mutableStateOf(initialTask?.title ?: "") }
    var taskType by remember { mutableStateOf(initialTask?.taskType ?: TaskType.EXERCISE) }
    var reps by remember { mutableIntStateOf(initialTask?.targetReps ?: 10) }
    var durationSeconds by remember { mutableIntStateOf(initialTask?.targetDurationSeconds ?: 0) }
    var audioPath by remember { mutableStateOf(initialTask?.audioFilePath) }
    var photoPath by remember { mutableStateOf(initialTask?.referencePhotoPath) }
    var notes by remember { mutableStateOf(initialTask?.notes ?: "") }
    var bodyParts by remember { mutableStateOf(initialTask?.bodyParts ?: "") }

    var isRecording by remember { mutableStateOf(false) }
    var isPlayingPreview by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            playerHelper.stopAudio()
            recorderHelper.cancelRecording()
        }
    }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val file = recorderHelper.startRecording()
            isRecording = recorderHelper.isRecording
        } else {
            Toast.makeText(context, "Microphone permission needed", Toast.LENGTH_SHORT).show()
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val destFile = MediaFileManager.createImageFile(context, "TASK_REF")
            if (MediaFileManager.copyUriToFile(context, it, destFile)) {
                photoPath = destFile.absolutePath
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialTask == null) "Add Exercise / Task" else "Edit Exercise / Task",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task / Exercise Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(text = "Task Type:", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { taskType = TaskType.EXERCISE }) {
                        RadioButton(selected = taskType == TaskType.EXERCISE, onClick = { taskType = TaskType.EXERCISE })
                        Text(text = "Exercise (Reps)")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { taskType = TaskType.GENERAL }) {
                        RadioButton(selected = taskType == TaskType.GENERAL, onClick = { taskType = TaskType.GENERAL })
                        Text(text = "General")
                    }
                }

                if (taskType == TaskType.EXERCISE) {
                    Text(text = "Target Repeats (Reps):", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { if (reps > 1) reps -= 1 },
                            shape = CircleShape,
                            modifier = Modifier.size(44.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEEEEE))
                        ) { Text("-", fontSize = 20.sp, color = Color.Black, fontWeight = FontWeight.Bold) }

                        Text(text = "$reps reps", fontSize = 22.sp, fontWeight = FontWeight.Black, color = PastelPurple)

                        Button(
                            onClick = { reps += 1 },
                            shape = CircleShape,
                            modifier = Modifier.size(44.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEEEEE))
                        ) { Text("+", fontSize = 20.sp, color = Color.Black, fontWeight = FontWeight.Bold) }
                    }

                    OutlinedTextField(
                        value = bodyParts,
                        onValueChange = { bodyParts = it },
                        label = { Text("Body Parts (e.g. Left Arm, Back)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Text(text = "Reference Photo (Visual Cue):", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF3F4F6))
                        .border(1.dp, Color.LightGray, RoundedCornerShape(16.dp))
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (!photoPath.isNullOrEmpty() && File(photoPath!!).exists()) {
                        AsyncImage(
                            model = File(photoPath!!),
                            contentDescription = "Selected Photo",
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.AddAPhoto, contentDescription = "Add Photo", tint = PastelPurple)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Tap to choose a photo", color = Color.Gray, fontSize = 14.sp)
                        }
                    }
                }

                Text(text = "Voice Guidance (Your Audio Note):", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (!isRecording) {
                        Button(
                            onClick = { micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO) },
                            colors = ButtonDefaults.buttonColors(containerColor = PastelPurple),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.Mic, contentDescription = "Record")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Record Voice")
                        }
                    } else {
                        Button(
                            onClick = {
                                val savedPath = recorderHelper.stopRecording()
                                isRecording = false
                                if (savedPath != null) audioPath = savedPath
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PastelRed),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.Stop, contentDescription = "Stop")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Stop Recording")
                        }
                    }

                    if (!audioPath.isNullOrEmpty() && File(audioPath!!).exists()) {
                        IconButton(onClick = {
                            if (isPlayingPreview) {
                                playerHelper.stopAudio()
                                isPlayingPreview = false
                            } else {
                                playerHelper.playAudio(audioPath!!) { isPlayingPreview = false }
                                isPlayingPreview = true
                            }
                        }) {
                            Icon(if (isPlayingPreview) Icons.Default.Stop else Icons.Default.PlayArrow, contentDescription = "Play", tint = PastelPurple)
                        }

                        IconButton(onClick = {
                            playerHelper.stopAudio()
                            audioPath?.let { File(it).delete() }
                            audioPath = null
                            isPlayingPreview = false
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(title.trim(), taskType, reps, durationSeconds, audioPath, photoPath, notes.trim(), bodyParts.trim())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PastelPurple)
            ) {
                Text("Save Exercise")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
