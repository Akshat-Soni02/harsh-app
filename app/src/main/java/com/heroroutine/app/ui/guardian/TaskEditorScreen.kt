package com.heroroutine.app.ui.guardian

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.heroroutine.app.ui.components.PhotoSourceDialog
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TaskEditorScreen(
    sessionId: Long,
    initialTask: TaskEntity? = null,
    isHinglish: Boolean = false,
    userBodyParts: Set<String> = emptySet(),
    onAddUserBodyPart: (String) -> Unit = {},
    onRemoveUserBodyPart: (String) -> Unit = {},
    onBack: () -> Unit,
    onSave: (
        title: String,
        type: TaskType,
        reps: Int,
        sets: Int,
        durationSeconds: Int,
        audioPath: String?,
        photoPath: String?,
        notes: String?,
        bodyParts: String,
        isDraft: Boolean
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

    var showAddBodyPartDialog by remember { mutableStateOf(false) }
    var newBodyPartInput by remember { mutableStateOf("") }
    var bodyPartPendingDelete by remember { mutableStateOf<String?>(null) }

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
            recorderHelper.startRecording()
            isRecording = recorderHelper.isRecording
        } else {
            Toast.makeText(context, "Microphone permission needed", Toast.LENGTH_SHORT).show()
        }
    }

    var showPhotoSourceDialog by remember { mutableStateOf(false) }
    var pendingCameraFile by remember { mutableStateOf<File?>(null) }

    val takePhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && pendingCameraFile != null) {
            photoPath = pendingCameraFile!!.absolutePath
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

    fun handleSave(isDraft: Boolean) {
        val saveTitle = title.trim().ifEmpty {
            if (isDraft) "Draft Exercise" else ""
        }
        if (!isDraft && saveTitle.isBlank()) {
            Toast.makeText(context, "Please enter a task name", Toast.LENGTH_SHORT).show()
            return
        }
        val finalSets = if (taskType == TaskType.EXERCISE && isSetsAndRepsMode) targetSets else 0
        val finalBodyParts = selectedParts.joinToString(", ")
        onSave(
            saveTitle,
            taskType,
            targetReps,
            finalSets,
            0,
            audioPath,
            photoPath,
            notes.trim().ifEmpty { null },
            finalBodyParts,
            isDraft
        )
    }

    fun handleBackAttempt() {
        val hasChanges = title.isNotBlank() || audioPath != null || photoPath != null ||
                notes.isNotBlank() || selectedParts.isNotEmpty()
        if (hasChanges && (initialTask == null || initialTask.isDraft)) {
            // Silently save as draft so user can resume later without losing anything
            handleSave(isDraft = true)
        } else {
            onBack()
        }
    }

    BackHandler { handleBackAttempt() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when {
                            initialTask?.isDraft == true -> if (isHinglish) "Draft Kasrat" else "Draft Exercise"
                            initialTask == null -> if (isHinglish) "Nayi Kasrat Jodein" else "Add Exercise"
                            else -> if (isHinglish) "Kasrat Badalna" else "Edit Exercise"
                        },
                        fontWeight = FontWeight.Black,
                        fontSize = 19.sp,
                        color = TextDark
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { handleBackAttempt() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextDark)
                    }
                },
                actions = {
                    Button(
                        onClick = { handleSave(isDraft = false) },
                        colors = ButtonDefaults.buttonColors(containerColor = WarmOrange),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Text(
                            text = if (isHinglish) "Save" else "Save",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WarmWhite),
                modifier = Modifier.border(1.dp, WarmCardBorder)
            )
        },
        containerColor = CreamBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Card 1: Basic Information
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = WarmWhite),
                border = BorderStroke(1.dp, WarmCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = if (isHinglish) "Kasrat ki Jaankari" else "Basic Information",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = TextDark
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = if (isHinglish) "Kasrat / Task ka Naam" else "Task Name",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            placeholder = {
                                Text(
                                    "e.g. Arm Stretch, Ball Catch",
                                    fontSize = 13.sp,
                                    color = Color(0xFFA0AEC0),
                                    fontWeight = FontWeight.Normal
                                )
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = WarmOrange,
                                unfocusedBorderColor = WarmCardBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Column {
                        Text(
                            text = if (isHinglish) "Task Type" else "Task Type",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
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
                                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FitnessCenter,
                                        contentDescription = null,
                                        tint = if (taskType == TaskType.EXERCISE) WarmOrange else TextMuted,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isHinglish) "Exercise (Kasrat)" else "Exercise",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
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
                                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.TaskAlt,
                                        contentDescription = null,
                                        tint = if (taskType == TaskType.GENERAL) SkyBlue else TextMuted,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isHinglish) "General (Anye Kaam)" else "General",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (taskType == TaskType.GENERAL) SkyBlue else TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Card 2: Targets Configuration (Only for Exercise)
            if (taskType == TaskType.EXERCISE) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = WarmWhite),
                    border = BorderStroke(1.dp, WarmCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = if (isHinglish) "Target / Ginti" else "Target Configuration",
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = TextDark
                        )

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
                                Text("Reps Only", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
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
                                Text("Sets + Reps", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                            }
                        }

                        if (isSetsAndRepsMode) {
                            // Sets Counter
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFFBF8F5), RoundedCornerShape(14.dp))
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = if (isHinglish) "Kitne Sets" else "Number of Sets",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextDark
                                    )
                                    Text(
                                        text = if (isHinglish) "Kul milakar kitne sets karne hain" else "How many sets in this routine",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFFF1F5F9),
                                        modifier = Modifier.size(40.dp).clickable { if (targetSets > 1) targetSets-- }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("-", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                        }
                                    }
                                    Text(
                                        text = "$targetSets",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Black,
                                        color = WarmOrange,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFFF1F5F9),
                                        modifier = Modifier.size(40.dp).clickable { targetSets++ }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("+", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                        }
                                    }
                                }
                            }

                            // Reps Counter
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFFBF8F5), RoundedCornerShape(14.dp))
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = if (isHinglish) "Har Set mein Reps" else "Reps per Set",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextDark
                                    )
                                    Text(
                                        text = if (isHinglish) "Ek baar mein kitni baar ginti" else "Repetitions per each set",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFFF1F5F9),
                                        modifier = Modifier.size(40.dp).clickable { if (targetReps > 1) targetReps-- }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("-", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                        }
                                    }
                                    Text(
                                        text = "$targetReps",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Black,
                                        color = WarmOrange,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFFF1F5F9),
                                        modifier = Modifier.size(40.dp).clickable { targetReps++ }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("+", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                        }
                                    }
                                }
                            }
                        } else {
                            // Reps Only Counter
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFFBF8F5), RoundedCornerShape(14.dp))
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = if (isHinglish) "Ginti (Reps)" else "Target Repetitions",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextDark
                                    )
                                    Text(
                                        text = if (isHinglish) "Kitni baar karna hai" else "Single set rep count",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFFF1F5F9),
                                        modifier = Modifier.size(40.dp).clickable { if (targetReps > 1) targetReps-- }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("-", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                        }
                                    }
                                    Text(
                                        text = "$targetReps",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Black,
                                        color = WarmOrange,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFFF1F5F9),
                                        modifier = Modifier.size(40.dp).clickable { targetReps++ }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("+", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Card 3: Target Body Parts
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = WarmWhite),
                    border = BorderStroke(1.dp, WarmCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (isHinglish) "Sharir ke Hisse (Body Parts)" else "Target Body Parts",
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = TextDark
                        )

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Premade body parts
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
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (isSelected) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
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

                            // User-created body parts
                            userBodyParts.forEach { part ->
                                val isSelected = selectedParts.contains(part)
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) WarmOrange else Color(0xFFF8FAFC),
                                    border = BorderStroke(1.dp, if (isSelected) WarmOrange else WarmCardBorder),
                                    modifier = Modifier.clickable {
                                        if (isSelected) selectedParts.remove(part) else selectedParts.add(part)
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(start = 12.dp, top = 8.dp, bottom = 8.dp, end = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (isSelected) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                        }
                                        Text(
                                            text = part,
                                            fontSize = 13.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else TextDark
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove $part",
                                            tint = if (isSelected) Color.White.copy(alpha = 0.85f) else TextMuted,
                                            modifier = Modifier
                                                .size(16.dp)
                                                .clickable { bodyPartPendingDelete = part }
                                        )
                                    }
                                }
                            }

                            // Distinct "+" chip
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MintGreenSoft,
                                border = BorderStroke(1.2.dp, MintGreen),
                                modifier = Modifier.clickable {
                                    newBodyPartInput = ""
                                    showAddBodyPartDialog = true
                                }
                            ) {
                                Box(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add Body Part",
                                        tint = MintGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Card 4: Reference Photo (Visual Cue)
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = WarmWhite),
                border = BorderStroke(1.dp, WarmCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (isHinglish) "Kasrat ki Photo (Visual Cue)" else "Reference Photo (Visual Cue)",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = TextDark
                    )
                    Text(
                        text = if (isHinglish) {
                            "Kasrat karte waqt ki photo lagane se champion ko dekh kar samajhna asaan hota hai ki kaise karna hai."
                        } else {
                            "A photo showing how to do the exercise helps champion recognize and understand tasks visually."
                        },
                        fontSize = 13.sp,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )

                    if (!photoPath.isNullOrEmpty() && File(photoPath!!).exists()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(1.dp, WarmOrangeBorder, RoundedCornerShape(16.dp))
                        ) {
                            AsyncImage(
                                model = File(photoPath!!),
                                contentDescription = "Selected Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showPhotoSourceDialog = true },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isHinglish) "Photo Badlein" else "Change Photo")
                            }

                            Button(
                                onClick = { photoPath = null },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove", modifier = Modifier.size(18.dp))
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(WarmOrangeSoft)
                                .border(1.dp, WarmOrangeBorder, RoundedCornerShape(16.dp))
                                .clickable { showPhotoSourceDialog = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.CameraAlt, contentDescription = "Add Photo", tint = WarmOrange, modifier = Modifier.size(34.dp))
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (isHinglish) "Photo Jodein" else "Add Photo",
                                    color = WarmOrange,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (isHinglish) "Camera ya gallery se photo chunein" else "Tap to take photo or choose from gallery",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    if (showPhotoSourceDialog) {
                        PhotoSourceDialog(
                            onDismiss = { showPhotoSourceDialog = false },
                            onSelectCamera = {
                                val file = MediaFileManager.createImageFile(context, "TASK_REF")
                                pendingCameraFile = file
                                val uri = MediaFileManager.getUriForFile(context, file)
                                takePhotoLauncher.launch(uri)
                            },
                            onSelectGallery = {
                                imagePickerLauncher.launch("image/*")
                            }
                        )
                    }
                }
            }

            // Card 5: Family Voice Guidance
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = WarmWhite),
                border = BorderStroke(1.dp, WarmCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (isHinglish) "Aapki Apni Aawaz (Voice Guidance)" else "Family Voice Guidance",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = TextDark
                    )
                    Text(
                        text = if (isHinglish) {
                            "Apni aawaz mein instructions record karein. Isse champion ko hausla milta hai aur kamzor nazar (low vision) hone par bhi aawaz sunkar kasrat asaani se samajh aati hai. Jaise: 'Haatho ko seedha karne ki exercise 20 baar, 3 set'."
                        } else {
                            "Record encouragement in a familiar voice. Helps champion stay motivated and listen to instructions clearly, especially helpful for low vision."
                        },
                        fontSize = 13.sp,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (!isRecording) {
                            Button(
                                onClick = { micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO) },
                                colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Mic, contentDescription = "Record")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (audioPath != null) {
                                        if (isHinglish) "Dobara Record Karein" else "Record New Voice"
                                    } else {
                                        if (isHinglish) "Aawaz Record Karein" else "Record Voice"
                                    },
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Button(
                                onClick = {
                                    val savedPath = recorderHelper.stopRecording()
                                    isRecording = false
                                    if (savedPath != null) audioPath = savedPath
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Stop, contentDescription = "Stop")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (isHinglish) "Recording Rokein" else "Stop Recording", fontWeight = FontWeight.Bold)
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
                                Icon(
                                    imageVector = if (isPlayingPreview) Icons.Default.Stop else Icons.Default.PlayArrow,
                                    contentDescription = "Play",
                                    tint = SkyBlue,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            IconButton(onClick = {
                                playerHelper.stopAudio()
                                audioPath?.let { File(it).delete() }
                                audioPath = null
                                isPlayingPreview = false
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(24.dp))
                            }
                        }
                    }
                }
            }

            // Card 6: Caregiver Notes & Instructions
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = WarmWhite),
                border = BorderStroke(1.dp, WarmCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = if (isHinglish) "Mata-Pita ke Notes & Salah" else "Caregiver Notes & Tips",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = TextDark
                    )
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text(if (isHinglish) "Notes / Physiotherapist ki salah" else "Notes / Physiotherapist instructions") },
                        placeholder = {
                            Text(
                                "e.g. Ensure elbow is supported on cushion",
                                fontSize = 13.sp,
                                color = Color(0xFF94A3B8),
                                fontWeight = FontWeight.Normal
                            )
                        },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WarmOrange,
                            unfocusedBorderColor = WarmCardBorder
                        )
                    )
                }
            }

            // Big Save Buttons at Bottom
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (initialTask == null || initialTask.isDraft) {
                    OutlinedButton(
                        onClick = { handleSave(isDraft = true) },
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.5.dp, WarmOrange),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                    ) {
                        Text(
                            text = if (isHinglish) "Draft Save Karein" else "Save as Draft",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = WarmOrange
                        )
                    }
                }

                Button(
                    onClick = { handleSave(isDraft = false) },
                    colors = ButtonDefaults.buttonColors(containerColor = WarmOrange),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                ) {
                    Text(
                        text = when {
                            initialTask?.isDraft == true -> if (isHinglish) "Kasrat Publish Karein" else "Publish Exercise"
                            initialTask == null -> if (isHinglish) "Kasrat Save Karein" else "Save Exercise"
                            else -> if (isHinglish) "Update Karein" else "Update Exercise"
                        },
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Modal: Add New Body Part (strictly NO word "custom")
    if (showAddBodyPartDialog) {
        AlertDialog(
            onDismissRequest = { showAddBodyPartDialog = false },
            title = {
                Text(
                    text = if (isHinglish) "Naya Hissa Jodein" else "Add New Body Part",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextDark
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = if (isHinglish) "Hisse ka Naam" else "Body Part Name",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextDark
                    )
                    OutlinedTextField(
                        value = newBodyPartInput,
                        onValueChange = { newBodyPartInput = it },
                        placeholder = {
                            Text(
                                "e.g. Left Knee, Ankles",
                                fontSize = 13.sp,
                                color = Color(0xFFA0AEC0),
                                fontWeight = FontWeight.Normal
                            )
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WarmOrange,
                            unfocusedBorderColor = WarmCardBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val trimmed = newBodyPartInput.trim()
                        if (trimmed.isNotEmpty()) {
                            onAddUserBodyPart(trimmed)
                            if (!selectedParts.contains(trimmed)) {
                                selectedParts.add(trimmed)
                            }
                            showAddBodyPartDialog = false
                            newBodyPartInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WarmOrange),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Add", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddBodyPartDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = WarmWhite,
            shape = RoundedCornerShape(22.dp)
        )
    }

    // Dialog: Confirm delete user body part
    if (bodyPartPendingDelete != null) {
        val part = bodyPartPendingDelete!!
        AlertDialog(
            onDismissRequest = { bodyPartPendingDelete = null },
            title = {
                Text(
                    text = if (isHinglish) "Hissa Hatayein?" else "Remove Body Part?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextDark
                )
            },
            text = {
                Text(
                    text = if (isHinglish) {
                        "'$part' ko aapki list se hataya jayega. Yeh un sabhi kasraton se bhi hat jayega jahan use ho raha hai."
                    } else {
                        "Remove '$part' from your list? It will also be removed from any exercises using it."
                    },
                    fontSize = 14.sp,
                    color = TextDark
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onRemoveUserBodyPart(part)
                        selectedParts.remove(part)
                        bodyPartPendingDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Remove", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { bodyPartPendingDelete = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = WarmWhite,
            shape = RoundedCornerShape(22.dp)
        )
    }
}
