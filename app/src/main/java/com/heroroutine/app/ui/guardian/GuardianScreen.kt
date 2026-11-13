package com.heroroutine.app.ui.guardian

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.heroroutine.app.data.model.SessionEntity
import com.heroroutine.app.data.model.TaskEntity
import com.heroroutine.app.data.model.TaskType
import com.heroroutine.app.ui.theme.CreamBackground
import com.heroroutine.app.ui.theme.MintGreen
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
import com.heroroutine.app.viewmodel.AppMode
import com.heroroutine.app.viewmodel.MainViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuardianScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedTaskToDetail by remember { mutableStateOf<TaskEntity?>(null) }
    var taskEditorSessionId by remember { mutableStateOf<Long?>(null) }

    if (selectedTaskToDetail != null) {
        TaskDetailScreen(
            taskId = selectedTaskToDetail!!.id,
            viewModel = viewModel,
            onBack = { selectedTaskToDetail = null }
        )
        return
    }

    if (taskEditorSessionId != null) {
        TaskEditorScreen(
            sessionId = taskEditorSessionId!!,
            initialTask = null,
            onBack = { taskEditorSessionId = null },
            onSave = { title, type, reps, sets, duration, audioPath, photoPath, notes, bodyParts ->
                viewModel.createTask(
                    sessionId = taskEditorSessionId!!,
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
                taskEditorSessionId = null
            }
        )
        return
    }

    Scaffold(
        topBar = {
            Surface(
                color = WarmWhite,
                border = BorderStroke(1.dp, WarmCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Guardian",
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = TextDark,
                            lineHeight = 22.sp
                        )
                        Text(
                            text = "Dashboard",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TextSecondary,
                            lineHeight = 15.sp
                        )
                    }

                    Button(
                        onClick = { viewModel.switchMode(AppMode.CHAMPION) },
                        colors = ButtonDefaults.buttonColors(containerColor = WarmOrange),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Champion Mode",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }
            }
        },
        bottomBar = {
            // Strictly 2 tabs: Routines and Progress & Analytics
            NavigationBar(
                containerColor = WarmWhite,
                tonalElevation = 4.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.FitnessCenter, contentDescription = "Routines") },
                    label = { Text("Routines", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = WarmOrange,
                        selectedTextColor = WarmOrange,
                        indicatorColor = WarmOrangeSoft,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextSecondary
                    )
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.ShowChart, contentDescription = "Progress") },
                    label = { Text("Progress & Analytics", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = WarmOrange,
                        selectedTextColor = WarmOrange,
                        indicatorColor = WarmOrangeSoft,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextSecondary
                    )
                )
            }
        },
        containerColor = CreamBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> GuardianRoutineManager(
                    viewModel = viewModel,
                    onTaskClicked = { selectedTaskToDetail = it },
                    onAddTask = { sessionId -> taskEditorSessionId = sessionId }
                )
                1 -> ProgressAnalyticsView(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun GuardianRoutineManager(
    viewModel: MainViewModel,
    onTaskClicked: (TaskEntity) -> Unit,
    onAddTask: (sessionId: Long) -> Unit
) {
    val sessions by viewModel.sessions.collectAsState()
    val allTasks by viewModel.allTasks.collectAsState()

    var showSessionDialog by remember { mutableStateOf(false) }
    var sessionToEdit by remember { mutableStateOf<SessionEntity?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    sessionToEdit = null
                    showSessionDialog = true
                },
                containerColor = WarmOrange,
                contentColor = Color.White,
                shape = RoundedCornerShape(18.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = "New Session")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("New Session", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            if (sessions.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, WarmCardBorder, RoundedCornerShape(24.dp)),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = WarmWhite)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(36.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No Routine Sessions Yet",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = TextDark
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Create morning or evening sessions to organize exercises for your champion.",
                                fontSize = 14.sp,
                                color = TextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(sessions, key = { it.id }) { session ->
                    val tasks = allTasks.filter { it.sessionId == session.id }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, WarmCardBorder, RoundedCornerShape(24.dp)),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = WarmWhite),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            // Session Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    if (session.iconEmoji.isNotBlank()) {
                                        Text(text = session.iconEmoji, fontSize = 24.sp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                    }
                                    Column {
                                        Text(
                                            text = session.title,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Black,
                                            color = TextDark
                                        )
                                        if (!session.targetTime.isNullOrBlank()) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(top = 2.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Schedule,
                                                    contentDescription = null,
                                                    tint = TextSecondary,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = session.targetTime!!,
                                                    fontSize = 12.sp,
                                                    color = TextSecondary,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                    }
                                }

                                Row {
                                    IconButton(onClick = {
                                        sessionToEdit = session
                                        showSessionDialog = true
                                    }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TextSecondary)
                                    }
                                    IconButton(onClick = { viewModel.deleteSession(session) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Tasks List
                            if (tasks.isEmpty()) {
                                Text(
                                    text = "No exercises added to this session yet.",
                                    fontSize = 14.sp,
                                    color = TextMuted,
                                    modifier = Modifier.padding(vertical = 10.dp)
                                )
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    tasks.forEach { task ->
                                        GuardianTaskItem(
                                            task = task,
                                            onClick = { onTaskClicked(task) }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // "Add Task" button
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable {
                                        onAddTask(session.id)
                                    },
                                shape = RoundedCornerShape(16.dp),
                                color = WarmOrangeSoft,
                                border = BorderStroke(1.dp, WarmOrangeBorder)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Add", tint = WarmOrange, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Add Task", color = WarmOrange, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showSessionDialog) {
        SessionEditorDialog(
            initialSession = sessionToEdit,
            onDismiss = { showSessionDialog = false },
            onSave = { title, emoji, time ->
                if (sessionToEdit == null) {
                    viewModel.createSession(title, emoji, time)
                } else {
                    viewModel.updateSession(sessionToEdit!!.copy(title = title, iconEmoji = emoji, targetTime = time))
                }
                showSessionDialog = false
            }
        )
    }
}

@Composable
fun GuardianTaskItem(
    task: TaskEntity,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFFFAF7F2),
        border = BorderStroke(1.dp, WarmCardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                // Large Thumbnail Image
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(14.dp))
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
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = task.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (task.taskType == TaskType.EXERCISE) {
                            val targetLabel = if (task.targetSets > 0) "${task.targetSets} sets [${task.targetReps}]" else "${task.targetReps} Reps"
                            Text(
                                text = targetLabel,
                                fontSize = 13.sp,
                                color = WarmOrange,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Text(text = "General", fontSize = 13.sp, color = TextSecondary)
                        }

                        if (task.bodyParts.isNotBlank()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "• ${task.bodyParts}",
                                fontSize = 12.sp,
                                color = TextMuted,
                                maxLines = 1
                            )
                        }

                        if (!task.audioFilePath.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Mic, contentDescription = "Voice", tint = SkyBlue, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("Voice", fontSize = 11.sp, color = SkyBlue, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Icon(Icons.Default.ChevronRight, contentDescription = "Details", tint = TextMuted)
        }
    }
}
