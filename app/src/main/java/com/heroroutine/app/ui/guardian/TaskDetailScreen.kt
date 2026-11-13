package com.heroroutine.app.ui.guardian

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.heroroutine.app.data.model.TaskType
import com.heroroutine.app.ui.theme.BackgroundLight
import com.heroroutine.app.ui.theme.CompletedGreen
import com.heroroutine.app.ui.theme.PastelPurple
import com.heroroutine.app.ui.theme.PastelRed
import com.heroroutine.app.viewmodel.MainViewModel
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    taskId: Long,
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val allTasks by viewModel.allTasks.collectAsState()
    val allLogs by viewModel.allLogs.collectAsState()
    val allPhotos by viewModel.allPhotos.collectAsState()

    val task = allTasks.find { it.id == taskId }

    var showEditDialog by remember { mutableStateOf(false) }

    if (task == null) {
        onBack()
        return
    }

    val taskLogs = allLogs.filter { it.taskId == taskId }
    val taskPhotos = allPhotos.filter { it.taskId == taskId }.sortedByDescending { it.timestamp }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(task.title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = {
                        viewModel.deleteTask(task)
                        onBack()
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = PastelRed)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
            )
        },
        containerColor = BackgroundLight
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Task Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFE2E8F0)),
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
                            Text(text = if (task.taskType == TaskType.EXERCISE) "🤸" else "✨", fontSize = 36.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        if (task.taskType == TaskType.EXERCISE) {
                            Text("Target Reps: ${task.targetReps}", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = PastelPurple)
                        }
                        if (task.bodyParts.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Body Parts: ${task.bodyParts}", color = Color.Gray, fontSize = 14.sp)
                        }
                    }
                }
            }

            if (task.taskType == TaskType.EXERCISE) {
                Text("Repetition Progress", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(start = 8.dp))
                RepetitionChartCard(taskLogs = taskLogs, currentTarget = task.targetReps)
            }

            Text("Consistency Calendar", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(start = 8.dp))
            TaskCalendarCard(taskLogs = taskLogs)

            Text("Progress Photos", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(start = 8.dp))
            if (taskPhotos.isEmpty()) {
                Text("No photos added yet.", color = Color.Gray, modifier = Modifier.padding(start = 8.dp))
            } else {
                taskPhotos.forEach { photo ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1.5f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF1F5F9))
                            ) {
                                AsyncImage(
                                    model = File(photo.photoPath),
                                    contentDescription = "Progress",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            if (!photo.notes.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = photo.notes, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showEditDialog) {
        TaskEditorDialog(
            sessionId = task.sessionId,
            initialTask = task,
            onDismiss = { showEditDialog = false },
            onSave = { title, type, reps, duration, audioPath, photoPath, notes, bodyParts ->
                viewModel.updateTask(
                    task.copy(
                        title = title,
                        taskType = type,
                        targetReps = reps,
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

@Composable
fun RepetitionChartCard(taskLogs: List<com.heroroutine.app.data.model.TaskLogEntity>, currentTarget: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            val dataPoints = taskLogs.filter { it.completed && it.repsAchieved > 0 }.takeLast(10).map { it.repsAchieved.toFloat() }
            val pointsToDraw = if (dataPoints.isEmpty()) listOf(0f, currentTarget.toFloat()) else dataPoints

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    val maxVal = (pointsToDraw.maxOrNull() ?: 10f) * 1.2f
                    val minVal = 0f

                    val path = Path()
                    val stepX = if (pointsToDraw.size > 1) width / (pointsToDraw.size - 1) else width

                    pointsToDraw.forEachIndexed { index, value ->
                        val x = index * stepX
                        val y = height - ((value - minVal) / (maxVal - minVal) * height)

                        if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)

                        drawCircle(color = PastelPurple, radius = 6.dp.toPx(), center = Offset(x, y))
                    }

                    drawPath(path = path, color = PastelPurple, style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round))
                }
            }
        }
    }
}

@Composable
fun TaskCalendarCard(taskLogs: List<com.heroroutine.app.data.model.TaskLogEntity>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
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
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isDone) CompletedGreen else Color(0xFFF1F5F9)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isDone) {
                                Text("✓", color = Color.White, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
