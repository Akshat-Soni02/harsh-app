package com.heroroutine.app.ui.guardian

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.TaskAlt
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

    val task = allTasks.find { it.id == taskId }

    var showEditDialog by remember { mutableStateOf(false) }
    var showAddPhotoDialog by remember { mutableStateOf(false) }
    var photoNoteInput by remember { mutableStateOf("") }
    var tempPhotoPath by remember { mutableStateOf<String?>(null) }

    val isPlayingVoice = viewModel.audioPlayer.isPathPlaying(task?.audioFilePath ?: "")

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
                        color = TextDark
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextDark)
                    }
                },
                actions = {
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TextSecondary)
                    }
                    IconButton(onClick = {
                        viewModel.deleteTask(task)
                        onBack()
                    }) {
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
                                color = TextDark
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            if (task.taskType == TaskType.EXERCISE) {
                                val targetFormat = if (task.targetSets > 0) "${task.targetSets} sets [${task.targetReps}]" else "${task.targetReps} Reps"
                                Text(
                                    text = targetFormat,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 20.sp,
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
                    currentTarget = task.targetReps
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
                    modifier = Modifier.clickable { photoPickerLauncher.launch("image/*") }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AddAPhoto, contentDescription = "Add Photo", tint = WarmOrange, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ Add Photo", color = WarmOrange, fontWeight = FontWeight.Bold, fontSize = 13.sp)
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

    // Edit Task Dialog
    if (showEditDialog) {
        TaskEditorDialog(
            sessionId = task.sessionId,
            initialTask = task,
            onDismiss = { showEditDialog = false },
            onSave = { title, type, reps, sets, duration, audioPath, photoPath, notes, bodyParts ->
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
                        bodyParts = bodyParts
                    )
                )
                showEditDialog = false
            }
        )
    }
}

/**
 * Repetition Progression Chart with visible X-axis dates and Y-axis reps (0, 5, 10, 15, 20...)
 */
@Composable
fun RepetitionChartCard(
    taskLogs: List<TaskLogEntity>,
    currentTarget: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, WarmCardBorder, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = WarmWhite)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            val validLogs = taskLogs.filter { it.completed && it.repsAchieved > 0 }
                .sortedBy { it.dateEpochDay }
                .takeLast(7)

            val formatter = remember { DateTimeFormatter.ofPattern("MMM d") }

            // If empty, fabricate 2 sample points so chart renders cleanly
            val points = if (validLogs.isEmpty()) {
                val today = LocalDate.now()
                listOf(
                    Pair(today.minusDays(1).format(formatter), (currentTarget * 0.8f).toInt()),
                    Pair(today.format(formatter), currentTarget)
                )
            } else {
                validLogs.map { log ->
                    val date = LocalDate.ofEpochDay(log.dateEpochDay)
                    Pair(date.format(formatter), log.repsAchieved)
                }
            }

            val maxVal = max(20, (points.maxOfOrNull { it.second } ?: currentTarget) + 5)
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

                    // Margins for axes
                    val leftMargin = 38.dp.toPx()
                    val bottomMargin = 26.dp.toPx()
                    val topMargin = 12.dp.toPx()
                    val rightMargin = 16.dp.toPx()

                    val chartWidth = w - leftMargin - rightMargin
                    val chartHeight = h - topMargin - bottomMargin

                    val gridColor = Color(0xFFE5DDD0)
                    val textColor = android.graphics.Color.parseColor("#7E7265")

                    // Draw Y-axis gridlines and labels
                    val textPaint = android.graphics.Paint().apply {
                        color = textColor
                        textSize = 28f
                        isAntiAlias = true
                        textAlign = android.graphics.Paint.Align.RIGHT
                    }

                    ySteps.forEach { stepVal ->
                        val yRatio = (stepVal.toFloat() / maxVal.toFloat()).coerceIn(0f, 1f)
                        val yPos = topMargin + chartHeight * (1f - yRatio)

                        // Draw dashed horizontal line
                        drawLine(
                            color = gridColor,
                            start = Offset(leftMargin, yPos),
                            end = Offset(w - rightMargin, yPos),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        )

                        // Draw Y-axis text
                        drawContext.canvas.nativeCanvas.drawText(
                            "$stepVal",
                            leftMargin - 10f,
                            yPos + 10f,
                            textPaint
                        )
                    }

                    // Draw Line and Points
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

                    // Draw connecting orange line
                    drawPath(
                        path = path,
                        color = WarmOrange,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Draw Points and X-axis labels
                    val xTextPaint = android.graphics.Paint().apply {
                        color = textColor
                        textSize = 26f
                        isAntiAlias = true
                        textAlign = android.graphics.Paint.Align.CENTER
                    }

                    val valTextPaint = android.graphics.Paint().apply {
                        color = android.graphics.Color.parseColor("#FF8A3D")
                        textSize = 26f
                        isFakeBoldText = true
                        isAntiAlias = true
                        textAlign = android.graphics.Paint.Align.CENTER
                    }

                    coords.forEachIndexed { idx, offset ->
                        // White circle border + orange dot
                        drawCircle(color = Color.White, radius = 7.dp.toPx(), center = offset)
                        drawCircle(color = WarmOrange, radius = 5.dp.toPx(), center = offset)

                        // Draw value above point
                        drawContext.canvas.nativeCanvas.drawText(
                            "${points[idx].second}",
                            offset.x,
                            offset.y - 14f,
                            valTextPaint
                        )

                        // Draw X-axis date label at bottom
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

@Composable
fun TaskCalendarCard(taskLogs: List<TaskLogEntity>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, WarmCardBorder, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = WarmWhite)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            val today = LocalDate.now()
            val startDay = today.minusDays(6)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (i in 0..6) {
                    val day = startDay.plusDays(i.toLong())
                    val epoch = day.toEpochDay()
                    val isDone = taskLogs.any { it.dateEpochDay == epoch && it.completed }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = day.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (day == today) WarmOrange else TextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(if (isDone) MintGreen else Color(0xFFF1F5F9))
                                .border(1.dp, if (isDone) MintGreen else WarmCardBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isDone) {
                                Text("✓", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black)
                            } else {
                                Text(
                                    text = "${day.dayOfMonth}",
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
