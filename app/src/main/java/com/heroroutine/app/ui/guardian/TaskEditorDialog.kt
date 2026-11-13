package com.heroroutine.app.ui.guardian

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import com.heroroutine.app.ui.theme.CreamBackground
import com.heroroutine.app.ui.theme.MintGreen
import com.heroroutine.app.ui.theme.MintGreenDark
import com.heroroutine.app.ui.theme.MintGreenSoft
import com.heroroutine.app.ui.theme.SkyBlue
import com.heroroutine.app.ui.theme.SkyBlueSoft
import com.heroroutine.app.ui.theme.TextDark
import com.heroroutine.app.ui.theme.TextMuted
import com.heroroutine.app.ui.theme.TextSecondary
import com.heroroutine.app.ui.theme.WarmCardBorder
import com.heroroutine.app.ui.theme.WarmOrange
import com.heroroutine.app.ui.theme.WarmOrangeBorder
import com.heroroutine.app.ui.theme.WarmOrangeSoft
import com.heroroutine.app.ui.theme.WarmWhite
import java.io.File

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TaskEditorDialog(
    sessionId: Long,
    initialTask: TaskEntity? = null,
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        type: TaskType,
        reps: Int,
        sets: Int,
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

    // Target format: reps only vs sets + reps
    var isSetsAndRepsMode by remember { mutableStateOf(initialTask?.let { it.targetSets > 0 } ?: false) }
    var targetSets by remember { mutableIntStateOf(if (initialTask != null && initialTask.targetSets > 0) initialTask.targetSets else 3) }
    var targetReps by remember { mutableIntStateOf(initialTask?.targetReps ?: 10) }

    var audioPath by remember { mutableStateOf(initialTask?.audioFilePath) }
    var photoPath by remember { mutableStateOf(initialTask?.referencePhotoPath) }
    var notes by remember { mutableStateOf(initialTask?.notes ?: "") }

    // Body parts list
    val premadeParts = remember {
        listOf("Arms", "Legs", "Shoulders", "Neck", "Back", "Chest", "Core", "Hips", "Hands", "Feet")
    }
    val selectedParts = remember {
        mutableStateListOf<String>().apply {
            if (!initialTask?.bodyParts.isNullOrBlank()) {
                addAll(initialTask!!.bodyParts.split(",").map { it.trim() }.filter { it.isNotEmpty() })
            }
        }
    }
    var customPartInput by remember { mutableStateOf("") }

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
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                color = TextDark
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Name
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task / Exercise Name") },
                    placeholder = { Text("e.g. Arm Stretch, Ball Catch") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WarmOrange,
                        unfocusedBorderColor = WarmCardBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Task Type Selector: Exercise vs General
                Column {
                    Text(text = "Task Type", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { taskType = TaskType.EXERCISE },
                            shape = RoundedCornerShape(14.dp),
                            color = if (taskType == TaskType.EXERCISE) WarmOrangeSoft else Color(0xFFF8FAFC),
                            border = BorderStroke(1.5.dp, if (taskType == TaskType.EXERCISE) WarmOrange else WarmCardBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FitnessCenter,
                                    contentDescription = null,
                                    tint = if (taskType == TaskType.EXERCISE) WarmOrange else TextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Exercise",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (taskType == TaskType.EXERCISE) WarmOrange else TextSecondary
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { taskType = TaskType.GENERAL },
                            shape = RoundedCornerShape(14.dp),
                            color = if (taskType == TaskType.GENERAL) SkyBlueSoft else Color(0xFFF8FAFC),
                            border = BorderStroke(1.5.dp, if (taskType == TaskType.GENERAL) SkyBlue else WarmCardBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TaskAlt,
                                    contentDescription = null,
                                    tint = if (taskType == TaskType.GENERAL) SkyBlue else TextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "General",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (taskType == TaskType.GENERAL) SkyBlue else TextSecondary
                                )
                            }
                        }
                    }
                }

                // If Exercise: Reps / Sets Configuration
                if (taskType == TaskType.EXERCISE) {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = WarmWhite),
                        border = BorderStroke(1.dp, WarmCardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(text = "Target Format", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                            Spacer(modifier = Modifier.height(8.dp))

                            // Radio toggle between Reps Only vs Sets + Reps
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { isSetsAndRepsMode = false }
                                ) {
                                    RadioButton(
                                        selected = !isSetsAndRepsMode,
                                        onClick = { isSetsAndRepsMode = false },
                                        colors = RadioButtonDefaults.colors(selectedColor = WarmOrange)
                                    )
                                    Text("Reps Only", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { isSetsAndRepsMode = true }
                                ) {
                                    RadioButton(
                                        selected = isSetsAndRepsMode,
                                        onClick = { isSetsAndRepsMode = true },
                                        colors = RadioButtonDefaults.colors(selectedColor = WarmOrange)
                                    )
                                    Text("Sets + Reps", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            if (isSetsAndRepsMode) {
                                // Sets counter
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Number of Sets", fontSize = 14.sp, color = TextSecondary)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = CircleShape,
                                            color = Color(0xFFF1F5F9),
                                            modifier = Modifier.size(36.dp).clickable { if (targetSets > 1) targetSets-- }
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text("-", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                        Text(
                                            text = "$targetSets",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Black,
                                            color = WarmOrange,
                                            modifier = Modifier.padding(horizontal = 14.dp)
                                        )
                                        Surface(
                                            shape = CircleShape,
                                            color = Color(0xFFF1F5F9),
                                            modifier = Modifier.size(36.dp).clickable { targetSets++ }
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text("+", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Reps per set counter
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Reps per Set", fontSize = 14.sp, color = TextSecondary)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = CircleShape,
                                            color = Color(0xFFF1F5F9),
                                            modifier = Modifier.size(36.dp).clickable { if (targetReps > 1) targetReps-- }
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text("-", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                        Text(
                                            text = "$targetReps",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Black,
                                            color = WarmOrange,
                                            modifier = Modifier.padding(horizontal = 14.dp)
                                        )
                                        Surface(
                                            shape = CircleShape,
                                            color = Color(0xFFF1F5F9),
                                            modifier = Modifier.size(36.dp).clickable { targetReps++ }
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text("+", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Format Preview Pill
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = WarmOrangeSoft,
                                    border = BorderStroke(1.dp, WarmOrangeBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Preview on Champion card: ", fontSize = 12.sp, color = TextSecondary)
                                        Text("$targetSets sets [$targetReps]", fontWeight = FontWeight.Black, color = WarmOrange, fontSize = 14.sp)
                                    }
                                }
                            } else {
                                // Reps Only counter
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Target Reps", fontSize = 14.sp, color = TextSecondary)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = CircleShape,
                                            color = Color(0xFFF1F5F9),
                                            modifier = Modifier.size(36.dp).clickable { if (targetReps > 1) targetReps-- }
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text("-", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                        Text(
                                            text = "$targetReps",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Black,
                                            color = WarmOrange,
                                            modifier = Modifier.padding(horizontal = 16.dp)
                                        )
                                        Surface(
                                            shape = CircleShape,
                                            color = Color(0xFFF1F5F9),
                                            modifier = Modifier.size(36.dp).clickable { targetReps++ }
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text("+", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Format Preview Pill
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = WarmOrangeSoft,
                                    border = BorderStroke(1.dp, WarmOrangeBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Preview on Champion card: ", fontSize = 12.sp, color = TextSecondary)
                                        Text("$targetReps Reps", fontWeight = FontWeight.Black, color = WarmOrange, fontSize = 14.sp)
                                    }
                                }
                            }
                        }
                    }

                    // Body Parts Section
                    Column {
                        Text(text = "Target Body Parts", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                        Spacer(modifier = Modifier.height(6.dp))

                        // Premade selectable body parts chips
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            premadeParts.forEach { part ->
                                val isSelected = selectedParts.contains(part)
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) WarmOrange else Color(0xFFF1F5F9),
                                    border = BorderStroke(1.dp, if (isSelected) WarmOrange else WarmCardBorder),
                                    modifier = Modifier.clickable {
                                        if (isSelected) selectedParts.remove(part) else selectedParts.add(part)
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (isSelected) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                        }
                                        Text(
                                            text = part,
                                            fontSize = 13.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else TextDark
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Custom body part text field with "+ Add" button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = customPartInput,
                                onValueChange = { customPartInput = it },
                                placeholder = { Text("Add custom part (e.g. Left Knee)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = WarmOrange,
                                    unfocusedBorderColor = WarmCardBorder
                                )
                            )

                            Button(
                                onClick = {
                                    val trimmed = customPartInput.trim()
                                    if (trimmed.isNotEmpty() && !selectedParts.contains(trimmed)) {
                                        selectedParts.add(trimmed)
                                        customPartInput = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = WarmOrange),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.height(52.dp)
                            ) {
                                Text("+ Add", fontWeight = FontWeight.Bold)
                            }
                        }

                        // Selected list chips
                        if (selectedParts.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                selectedParts.forEach { part ->
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = WarmOrangeSoft,
                                        border = BorderStroke(1.dp, WarmOrangeBorder)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(part, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WarmOrange)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Remove",
                                                tint = WarmOrange,
                                                modifier = Modifier.size(14.dp).clickable { selectedParts.remove(part) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Reference Photo
                Column {
                    Text(text = "Reference Photo (Visual Cue)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(WarmOrangeSoft)
                            .border(1.dp, WarmOrangeBorder, RoundedCornerShape(16.dp))
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (!photoPath.isNullOrEmpty() && File(photoPath!!).exists()) {
                            AsyncImage(
                                model = File(photoPath!!),
                                contentDescription = "Selected Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.AddAPhoto, contentDescription = "Add Photo", tint = WarmOrange, modifier = Modifier.size(32.dp))
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = "Tap to choose a photo", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }

                // Voice Recording
                Column {
                    Text(text = "Family Voice Guidance", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (!isRecording) {
                            Button(
                                onClick = { micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO) },
                                colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
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
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
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
                                Icon(if (isPlayingPreview) Icons.Default.Stop else Icons.Default.PlayArrow, contentDescription = "Play", tint = SkyBlue)
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
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val finalSets = if (taskType == TaskType.EXERCISE && isSetsAndRepsMode) targetSets else 0
                        val finalBodyParts = selectedParts.joinToString(", ")
                        onSave(
                            title.trim(),
                            taskType,
                            targetReps,
                            finalSets,
                            0, // duration
                            audioPath,
                            photoPath,
                            notes.trim(),
                            finalBodyParts
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = WarmOrange),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Save Exercise", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = WarmWhite,
        shape = RoundedCornerShape(26.dp)
    )
}
