package com.heroroutine.app.ui.guardian

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.heroroutine.app.ui.components.PhotoSourceDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.heroroutine.app.data.model.TaskEntity
import com.heroroutine.app.data.model.TaskLogEntity
import com.heroroutine.app.data.model.TaskType
import com.heroroutine.app.media.MediaFileManager
import com.heroroutine.app.ui.theme.CreamBackground
import com.heroroutine.app.ui.theme.MintGreen
import com.heroroutine.app.ui.theme.MintGreenDark
import com.heroroutine.app.ui.theme.MintGreenSoft
import com.heroroutine.app.ui.theme.SkyBlue
import com.heroroutine.app.ui.theme.SkyBlueBorder
import com.heroroutine.app.ui.theme.SkyBlueSoft
import com.heroroutine.app.ui.theme.TextDark
import com.heroroutine.app.ui.theme.TextMuted
import com.heroroutine.app.ui.theme.TextSecondary
import com.heroroutine.app.ui.theme.WarmCardBorder
import com.heroroutine.app.ui.theme.WarmOrange
import com.heroroutine.app.ui.theme.WarmOrangeBorder
import com.heroroutine.app.ui.theme.WarmOrangeSoft
import com.heroroutine.app.ui.theme.WarmWhite
import com.heroroutine.app.viewmodel.MainViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TaskDetailScreen(
    taskId: Long,
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val allTasks by viewModel.allTasks.collectAsState()
    val allLogs by viewModel.allLogs.collectAsState()
    val allPhotos by viewModel.allPhotos.collectAsState()
    val isHinglish by viewModel.isHinglish.collectAsState()
    val customBodyParts by viewModel.customBodyParts.collectAsState()

    BackHandler { onBack() }

    val task = allTasks.find { it.id == taskId }

    var isEditing by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showAddPhotoDialog by remember { mutableStateOf(false) }
    var showPhotoSourceDialog by remember { mutableStateOf(false) }
    var photoNoteInput by remember { mutableStateOf("") }
    var tempPhotoPath by remember { mutableStateOf<String?>(null) }
    var pendingCameraFile by remember { mutableStateOf<File?>(null) }

    val isPlayingVoice = viewModel.audioPlayer.isPathPlaying(task?.audioFilePath ?: "")

    val takePhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && pendingCameraFile != null) {
            tempPhotoPath = pendingCameraFile!!.absolutePath
            showAddPhotoDialog = true
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val destFile = MediaFileManager.createImageFile(context, "PROGRESS")
            if (MediaFileManager.copyUriToFile(context, it, destFile)) {
                tempPhotoPath = destFile.absolutePath
                showAddPhotoDialog = true
            }
        }
    }

    if (task == null) {
        onBack()
        return
    }

    if (isEditing) {
        TaskEditorScreen(
            sessionId = task.sessionId,
            initialTask = task,
            isHinglish = isHinglish,
            userBodyParts = customBodyParts,
            onAddUserBodyPart = { viewModel.addCustomBodyPart(it) },
            onRemoveUserBodyPart = { viewModel.removeCustomBodyPart(it) },
            onBack = { isEditing = false },
            onDiscardDraft = {
                viewModel.deleteTask(task)
                isEditing = false
                onBack()
            },
            onSave = { title, type, reps, sets, duration, audioPath, photoPath, notes, bodyParts, isDraft ->
                viewModel.updateTask(
                    task.copy(
                        title = title,
                        taskType = type,
                        targetReps = reps,
                        targetSets = sets,
                        targetDurationSeconds = duration,
                        audioFilePath = audioPath,
                        referencePhotoPath = photoPath,
                        notes = notes,
                        bodyParts = bodyParts,
                        isDraft = isDraft
                    )
                )
                isEditing = false
            }
        )
        return
    }

    val taskLogs = allLogs.filter { it.taskId == taskId }
    val taskPhotos = allPhotos.filter { it.taskId == taskId }.sortedByDescending { it.timestamp }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = task.title,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = TextDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextDark)
                    }
                },
                actions = {
                    IconButton(onClick = { isEditing = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TextSecondary)
                    }
                    IconButton(onClick = { showDeleteConfirmDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444))
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Task Header Overview Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, WarmCardBorder, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = WarmWhite)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(if (task.taskType == TaskType.EXERCISE) WarmOrangeSoft else SkyBlueSoft),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!task.referencePhotoPath.isNullOrEmpty() && File(task.referencePhotoPath).exists()) {
                                AsyncImage(
                                    model = File(task.referencePhotoPath),
                                    contentDescription = task.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = if (task.taskType == TaskType.EXERCISE) Icons.Default.FitnessCenter else Icons.Default.TaskAlt,
                                    contentDescription = null,
                                    tint = if (task.taskType == TaskType.EXERCISE) WarmOrange else SkyBlue,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = task.title,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = TextDark,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            if (task.taskType == TaskType.EXERCISE) {
                                val targetFormat = if (task.targetSets > 0) "${task.targetSets} sets of ${task.targetReps} reps" else "${task.targetReps} Reps"
                                Text(
                                    text = targetFormat,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    color = WarmOrange
                                )
                            } else {
                                Text(
                                    text = "General Task",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = SkyBlue
                                )
                            }
                        }
                    }

                    // Body Parts Chips
                    if (task.bodyParts.isNotBlank()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Target Body Parts",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            task.bodyParts.split(",").map { it.trim() }.filter { it.isNotEmpty() }.forEach { part ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = WarmOrangeSoft,
                                    border = BorderStroke(1.dp, WarmOrangeBorder)
                                ) {
                                    Text(
                                        text = part,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = WarmOrange,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Voice Note Player (if available)
                    if (!task.audioFilePath.isNullOrEmpty() && File(task.audioFilePath).exists()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    if (isPlayingVoice) {
                                        viewModel.audioPlayer.stopAudio()
                                    } else {
                                        viewModel.audioPlayer.playAudio(task.audioFilePath)
                                    }
                                },
                            shape = RoundedCornerShape(14.dp),
                            color = SkyBlueSoft,
                            border = BorderStroke(1.dp, SkyBlueBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isPlayingVoice) Icons.Default.Stop else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = SkyBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isPlayingVoice) "Playing Voice Note..." else "Play Recorded Voice Guidance",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SkyBlue
                                )
                            }
                        }
                    }
                }
            }

            // Repetition Progress Chart with X & Y Labels
            if (task.taskType == TaskType.EXERCISE) {
                Text(
                    text = "Repetition Progression",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = TextDark,
                    modifier = Modifier.padding(start = 4.dp)
                )

                RepetitionChartCard(
                    taskLogs = taskLogs,
                    task = task
                )
            }

            // 7-Day Consistency Row
            Text(
                text = "Recent Consistency",
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                color = TextDark,
                modifier = Modifier.padding(start = 4.dp)
            )

            TaskCalendarCard(taskLogs = taskLogs)

            // Dated Progress Photos Section
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Progress Photos",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = TextDark
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = WarmOrangeSoft,
                    border = BorderStroke(1.dp, WarmOrangeBorder),
                    modifier = Modifier.clickable { showPhotoSourceDialog = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = "Add Photo", tint = WarmOrange, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add Photo", color = WarmOrange, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            if (taskPhotos.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, WarmCardBorder, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = WarmWhite)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No dated progress photos yet.",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Capture photos of posture or mobility over time to see improvement.",
                            color = TextMuted,
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                taskPhotos.forEach { photo ->
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = WarmWhite),
                        border = BorderStroke(1.dp, WarmCardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1.4f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFFF1F5F9))
                            ) {
                                AsyncImage(
                                    model = File(photo.photoPath),
                                    contentDescription = "Progress Photo",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val dateStr = SimpleDateFormat("MMM d, yyyy • hh:mm a", Locale.getDefault())
                                    .format(Date(photo.timestamp))

                                Text(
                                    text = dateStr,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary
                                )

                                IconButton(
                                    onClick = { viewModel.deleteProgressPhoto(photo) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(18.dp))
                                }
                            }

                            if (!photo.notes.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = photo.notes,
                                    fontSize = 14.sp,
                                    color = TextDark
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Progress Photo Dialog
    if (showAddPhotoDialog && tempPhotoPath != null) {
        Dialog(onDismissRequest = { showAddPhotoDialog = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = WarmWhite),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Add Progress Note",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = TextDark
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(14.dp))
                    ) {
                        AsyncImage(
                            model = File(tempPhotoPath!!),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    OutlinedTextField(
                        value = photoNoteInput,
                        onValueChange = { photoNoteInput = it },
                        label = { Text("Note (e.g. Arm raised 10 degrees higher!)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WarmOrange,
                            unfocusedBorderColor = WarmCardBorder
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = {
                            showAddPhotoDialog = false
                            tempPhotoPath = null
                            photoNoteInput = ""
                        }) {
                            Text("Cancel", color = TextSecondary)
                        }

                        Button(
                            onClick = {
                                viewModel.addProgressPhoto(task.id, tempPhotoPath!!, photoNoteInput.trim())
                                showAddPhotoDialog = false
                                tempPhotoPath = null
                                photoNoteInput = ""
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = WarmOrange)
                        ) {
                            Text("Save Photo")
                        }
                    }
                }
            }
        }
    }

    if (showPhotoSourceDialog) {
        PhotoSourceDialog(
            onDismiss = { showPhotoSourceDialog = false },
            onSelectCamera = {
                val file = MediaFileManager.createImageFile(context, "PROGRESS")
                pendingCameraFile = file
                val uri = MediaFileManager.getUriForFile(context, file)
                takePhotoLauncher.launch(uri)
            },
            onSelectGallery = {
                photoPickerLauncher.launch("image/*")
            }
        )
    }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = {
                Text(
                    text = if (isHinglish) "Kasrat Hatayein?" else "Delete Task?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextDark
                )
            },
            text = {
                Text(
                    text = if (isHinglish) {
                        "'${task.title}' ko hatane ke baad iska saara data hat jayega. Kya aap hatana chahte hain?"
                    } else {
                        "Delete '${task.title}'? This action cannot be undone."
                    },
                    fontSize = 14.sp,
                    color = TextDark
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        viewModel.deleteTask(task)
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Delete", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = WarmWhite,
            shape = RoundedCornerShape(22.dp)
        )
    }
}

/**
 * Repetition Progression Chart with visible X-axis dates and Y-axis reps (0, 5, 10, 15, 20...)
 * For exercises with sets, shows total reps (sets × reps) on Y-axis.
 */
enum class ChartTimeRange {
    WEEKLY,
    MONTHLY,
    YEARLY
}

/**
 * Repetition Progression Chart with visible X-axis dates and Y-axis reps.
 * Supports Weekly, Monthly, and Yearly view modes with unlimited historical navigation (< and > arrows).
 * For exercises with sets, shows total reps (sets × reps) on Y-axis.
 */
@Composable
fun RepetitionChartCard(
    taskLogs: List<TaskLogEntity>,
    task: TaskEntity
) {
    val effectiveTarget = if (task.targetSets > 0) task.targetSets * task.targetReps else task.targetReps
    var selectedRange by remember { mutableStateOf(ChartTimeRange.WEEKLY) }
    var timeOffset by remember { mutableStateOf(0) } // 0 = current, -1 = previous, etc.

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, WarmCardBorder, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = WarmWhite)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header / Subtitle
            if (task.targetSets > 0) {
                Text(
                    text = "Total Reps (${task.targetSets} sets × ${task.targetReps} reps = $effectiveTarget target)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            } else {
                Text(
                    text = "Reps achieved (Target: $effectiveTarget reps)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Time Range Switcher (Weekly / Monthly / Yearly)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF1EDE6), RoundedCornerShape(12.dp))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf(
                    ChartTimeRange.WEEKLY to "Weekly",
                    ChartTimeRange.MONTHLY to "Monthly",
                    ChartTimeRange.YEARLY to "Yearly"
                ).forEach { (range, title) ->
                    val isSelected = selectedRange == range
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if (selectedRange != range) {
                                    selectedRange = range
                                    timeOffset = 0
                                }
                            },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) WarmOrange else Color.Transparent
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else TextDark
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Navigation Controls (< Label >)
            val rangeLabel = when (selectedRange) {
                ChartTimeRange.WEEKLY -> {
                    val endDay = LocalDate.now().plusWeeks(timeOffset.toLong())
                    val startDay = endDay.minusDays(6)
                    val fmt = DateTimeFormatter.ofPattern("MMM d")
                    "${startDay.format(fmt)} - ${endDay.format(fmt)}"
                }
                ChartTimeRange.MONTHLY -> {
                    val month = YearMonth.now().plusMonths(timeOffset.toLong())
                    month.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
                }
                ChartTimeRange.YEARLY -> {
                    val year = Year.now().plusYears(timeOffset.toLong())
                    year.format(DateTimeFormatter.ofPattern("yyyy"))
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { timeOffset-- },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Previous", tint = TextDark)
                }

                Text(
                    text = rangeLabel,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                IconButton(
                    onClick = { if (timeOffset < 0) timeOffset++ },
                    enabled = timeOffset < 0,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = "Next",
                        tint = if (timeOffset < 0) TextDark else TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Compute Points based on Range
            val points: List<Pair<String, Int>> = when (selectedRange) {
                ChartTimeRange.WEEKLY -> {
                    val endDay = LocalDate.now().plusWeeks(timeOffset.toLong())
                    val startDay = endDay.minusDays(6)
                    val dayFmt = DateTimeFormatter.ofPattern("EEE")
                    (0..6).map { i ->
                        val currentDay = startDay.plusDays(i.toLong())
                        val epoch = currentDay.toEpochDay()
                        val log = taskLogs.firstOrNull { it.dateEpochDay == epoch && it.completed }
                        val reps = if (log != null) {
                            if (task.targetSets > 0 && log.repsAchieved == task.targetReps) {
                                task.targetSets * task.targetReps
                            } else {
                                log.repsAchieved
                            }
                        } else 0
                        Pair(currentDay.format(dayFmt), reps)
                    }
                }
                ChartTimeRange.MONTHLY -> {
                    val currentMonth = YearMonth.now().plusMonths(timeOffset.toLong())
                    val startEpoch = currentMonth.atDay(1).toEpochDay()
                    val endEpoch = currentMonth.atEndOfMonth().toEpochDay()

                    val monthLogs = taskLogs.filter { it.completed && it.dateEpochDay in startEpoch..endEpoch }
                        .sortedBy { it.dateEpochDay }

                    if (monthLogs.isEmpty()) {
                        listOf(Pair("1st", 0), Pair("15th", 0), Pair("End", 0))
                    } else {
                        monthLogs.map { log ->
                            val date = LocalDate.ofEpochDay(log.dateEpochDay)
                            val reps = if (task.targetSets > 0 && log.repsAchieved == task.targetReps) {
                                task.targetSets * task.targetReps
                            } else {
                                log.repsAchieved
                            }
                            Pair("${date.dayOfMonth}", reps)
                        }
                    }
                }
                ChartTimeRange.YEARLY -> {
                    val currentYear = Year.now().plusYears(timeOffset.toLong())
                    val monthFmt = DateTimeFormatter.ofPattern("MMM")
                    (1..12).map { monthNum ->
                        val ym = currentYear.atMonth(monthNum)
                        val startEpoch = ym.atDay(1).toEpochDay()
                        val endEpoch = ym.atEndOfMonth().toEpochDay()
                        val logsInMonth = taskLogs.filter { it.completed && it.dateEpochDay in startEpoch..endEpoch }
                        val avgReps = if (logsInMonth.isNotEmpty()) {
                            logsInMonth.map { log ->
                                if (task.targetSets > 0 && log.repsAchieved == task.targetReps) {
                                    task.targetSets * task.targetReps
                                } else {
                                    log.repsAchieved
                                }
                            }.average().toInt()
                        } else 0
                        Pair(ym.format(monthFmt), avgReps)
                    }
                }
            }

            val maxVal = max(20, (points.maxOfOrNull { it.second } ?: effectiveTarget) + 5)
            val ySteps = listOf(maxVal, (maxVal * 0.75f).toInt(), (maxVal * 0.5f).toInt(), (maxVal * 0.25f).toInt(), 0)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color(0xFFFAF7F2), RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    val leftMargin = 38.dp.toPx()
                    val bottomMargin = 26.dp.toPx()
                    val topMargin = 12.dp.toPx()
                    val rightMargin = 16.dp.toPx()

                    val chartWidth = w - leftMargin - rightMargin
                    val chartHeight = h - topMargin - bottomMargin

                    val gridColor = Color(0xFFE5DDD0)
                    val textColor = android.graphics.Color.parseColor("#7E7265")

                    val textPaint = android.graphics.Paint().apply {
                        color = textColor
                        textSize = 28f
                        isAntiAlias = true
                        textAlign = android.graphics.Paint.Align.RIGHT
                    }

                    ySteps.forEach { stepVal ->
                        val yRatio = (stepVal.toFloat() / maxVal.toFloat()).coerceIn(0f, 1f)
                        val yPos = topMargin + chartHeight * (1f - yRatio)

                        drawLine(
                            color = gridColor,
                            start = Offset(leftMargin, yPos),
                            end = Offset(w - rightMargin, yPos),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        )

                        drawContext.canvas.nativeCanvas.drawText(
                            "$stepVal",
                            leftMargin - 10f,
                            yPos + 10f,
                            textPaint
                        )
                    }

                    val path = Path()
                    val stepX = if (points.size > 1) chartWidth / (points.size - 1) else chartWidth

                    val coords = points.mapIndexed { idx, pair ->
                        val x = leftMargin + idx * stepX
                        val yRatio = (pair.second.toFloat() / maxVal.toFloat()).coerceIn(0f, 1f)
                        val y = topMargin + chartHeight * (1f - yRatio)
                        Offset(x, y)
                    }

                    coords.forEachIndexed { idx, offset ->
                        if (idx == 0) path.moveTo(offset.x, offset.y) else path.lineTo(offset.x, offset.y)
                    }

                    drawPath(
                        path = path,
                        color = WarmOrange,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    val xTextPaint = android.graphics.Paint().apply {
                        color = textColor
                        textSize = 24f
                        isAntiAlias = true
                        textAlign = android.graphics.Paint.Align.CENTER
                    }

                    val valTextPaint = android.graphics.Paint().apply {
                        color = android.graphics.Color.parseColor("#FF8A3D")
                        textSize = 24f
                        isFakeBoldText = true
                        isAntiAlias = true
                        textAlign = android.graphics.Paint.Align.CENTER
                    }

                    coords.forEachIndexed { idx, offset ->
                        drawCircle(color = Color.White, radius = 6.dp.toPx(), center = offset)
                        drawCircle(color = WarmOrange, radius = 4.dp.toPx(), center = offset)

                        if (points[idx].second > 0) {
                            drawContext.canvas.nativeCanvas.drawText(
                                "${points[idx].second}",
                                offset.x,
                                offset.y - 12f,
                                valTextPaint
                            )
                        }

                        drawContext.canvas.nativeCanvas.drawText(
                            points[idx].first,
                            offset.x,
                            h - 4f,
                            xTextPaint
                        )
                    }
                }
            }
        }
    }
}

/**
 * Monthly Consistency Calendar View for a specific exercise.
 * Supports < and > arrow navigation to any month in history (e.g. March 2025).
 */
@Composable
fun TaskCalendarCard(taskLogs: List<TaskLogEntity>) {
    var monthOffset by remember { mutableStateOf(0) }
    val currentMonth = remember(monthOffset) {
        YearMonth.now().plusMonths(monthOffset.toLong())
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, WarmCardBorder, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = WarmWhite)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Month Navigation Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { monthOffset-- }) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Month", tint = TextDark)
                }

                Text(
                    text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = TextDark
                )

                IconButton(
                    onClick = { if (monthOffset < 0) monthOffset++ },
                    enabled = monthOffset < 0
                ) {
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = "Next Month",
                        tint = if (monthOffset < 0) TextDark else TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Day of Week Header
            val dayHeaders = listOf("M", "T", "W", "T", "F", "S", "S")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                dayHeaders.forEach { d ->
                    Text(
                        text = d,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        modifier = Modifier.width(36.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Calendar Grid
            val daysInMonth = currentMonth.lengthOfMonth()
            val firstDayOfWeek = currentMonth.atDay(1).dayOfWeek.value // 1 = Mon ... 7 = Sun
            val paddingBefore = firstDayOfWeek - 1
            val totalCells = paddingBefore + daysInMonth
            val rows = (totalCells + 6) / 7

            val today = LocalDate.now()
            var completedCount = 0

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                for (r in 0 until rows) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        for (c in 0 until 7) {
                            val cellIndex = r * 7 + c
                            val dayNumber = cellIndex - paddingBefore + 1

                            if (dayNumber in 1..daysInMonth) {
                                val date = currentMonth.atDay(dayNumber)
                                val epoch = date.toEpochDay()
                                val isDone = taskLogs.any { it.dateEpochDay == epoch && it.completed }
                                if (isDone) completedCount++
                                val isToday = date == today

                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (isDone) MintGreenSoft else Color(0xFFF1F5F9))
                                        .border(
                                            width = if (isToday) 1.5.dp else 1.dp,
                                            color = if (isToday) WarmOrange else if (isDone) MintGreen else Color(0xFFE2E8F0),
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isDone) {
                                        Text(
                                            text = "✓",
                                            color = MintGreenDark,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    } else {
                                        Text(
                                            text = "$dayNumber",
                                            fontSize = 12.sp,
                                            fontWeight = if (isToday) FontWeight.Black else FontWeight.Medium,
                                            color = if (isToday) WarmOrange else TextMuted
                                        )
                                    }
                                }
                            } else {
                                Box(modifier = Modifier.size(36.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Monthly Summary Footer
            val consistencyPercent = if (daysInMonth > 0) ((completedCount.toFloat() / daysInMonth.toFloat()) * 100).toInt() else 0
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (completedCount > 0) MintGreenSoft else Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, if (completedCount > 0) MintGreen else WarmCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Completed $completedCount of $daysInMonth days",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        text = "$consistencyPercent% consistency",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = if (completedCount > 0) MintGreenDark else TextSecondary
                    )
                }
            }
        }
    }
}
