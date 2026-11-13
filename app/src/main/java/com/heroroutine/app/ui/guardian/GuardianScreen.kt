package com.heroroutine.app.ui.guardian

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.material3.TopAppBarDefaults
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
import com.heroroutine.app.ui.theme.BackgroundLight
import com.heroroutine.app.ui.theme.HeroGreenDark
import com.heroroutine.app.ui.theme.PastelBlue
import com.heroroutine.app.ui.theme.PastelPurple
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
    val isHinglish by viewModel.isHinglish.collectAsState()

    if (selectedTaskToDetail != null) {
        TaskDetailScreen(
            taskId = selectedTaskToDetail!!.id,
            viewModel = viewModel,
            onBack = { selectedTaskToDetail = null }
        )
        return
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Guardian Setup 🛡️",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.setHinglish(!isHinglish) }) {
                        Icon(Icons.Default.Translate, contentDescription = "Toggle Language", tint = Color.White)
                    }
                },
                actions = {
                    Button(
                        onClick = { viewModel.switchMode(AppMode.CHAMPION) },
                        colors = ButtonDefaults.buttonColors(containerColor = PastelBlue),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Text(
                            text = "Start Routine 🌟",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = HeroGreenDark
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.FitnessCenter, contentDescription = "Routines") },
                    label = { Text("Routines") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PastelPurple,
                        selectedTextColor = PastelPurple,
                        indicatorColor = PastelPurple.copy(alpha = 0.15f)
                    )
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.ShowChart, contentDescription = "Progress") },
                    label = { Text("Overall Progress") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PastelPurple,
                        selectedTextColor = PastelPurple,
                        indicatorColor = PastelPurple.copy(alpha = 0.15f)
                    )
                )
            }
        },
        containerColor = BackgroundLight
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> GuardianRoutineManager(
                    viewModel = viewModel, 
                    onTaskClicked = { selectedTaskToDetail = it }
                )
                1 -> ProgressAnalyticsView(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun GuardianRoutineManager(
    viewModel: MainViewModel,
    onTaskClicked: (TaskEntity) -> Unit
) {
    val sessions by viewModel.sessions.collectAsState()
    val allTasks by viewModel.allTasks.collectAsState()

    var showSessionDialog by remember { mutableStateOf(false) }
    var sessionToEdit by remember { mutableStateOf<SessionEntity?>(null) }

    var showTaskCreateDialog by remember { mutableStateOf(false) }
    var targetSessionIdForNewTask by remember { mutableStateOf<Long?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    sessionToEdit = null
                    showSessionDialog = true
                },
                containerColor = PastelPurple,
                contentColor = Color.White
            ) {
                Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, contentDescription = "Add Session")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("New Session", fontWeight = FontWeight.Bold)
                }
            }
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            contentPadding = PaddingValues(bottom = 72.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(sessions, key = { it.id }) { session ->
                val tasks = allTasks.filter { it.sessionId == session.id }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = session.iconEmoji, fontSize = 28.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = session.title,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Row {
                                IconButton(onClick = {
                                    sessionToEdit = session
                                    showSessionDialog = true
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Gray)
                                }
                                IconButton(onClick = { viewModel.deleteSession(session) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        if (tasks.isEmpty()) {
                            Text(
                                text = "No exercises added to this session yet.",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        } else {
                            tasks.forEach { task ->
                                GuardianTaskItem(
                                    task = task,
                                    onClick = { onTaskClicked(task) }
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                targetSessionIdForNewTask = session.id
                                showTaskCreateDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PastelPurple.copy(alpha = 0.1f)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add", tint = PastelPurple)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add Exercise", color = PastelPurple, fontWeight = FontWeight.Bold)
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

    if (showTaskCreateDialog && targetSessionIdForNewTask != null) {
        TaskEditorDialog(
            sessionId = targetSessionIdForNewTask!!,
            initialTask = null,
            onDismiss = { showTaskCreateDialog = false },
            onSave = { title, type, reps, duration, audioPath, photoPath, notes, bodyParts ->
                viewModel.createTask(
                    sessionId = targetSessionIdForNewTask!!,
                    title = title,
                    taskType = type,
                    targetReps = reps,
                    targetDurationSeconds = duration,
                    audioFilePath = audioPath,
                    referencePhotoPath = photoPath,
                    notes = notes,
                    bodyParts = bodyParts
                )
                showTaskCreateDialog = false
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
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF8FAFC),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(12.dp))
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
                        Text(text = if (task.taskType == TaskType.EXERCISE) "🤸" else "✨", fontSize = 24.sp)
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(text = task.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1E293B))
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (task.taskType == TaskType.EXERCISE) {
                            Text(text = "${task.targetReps} reps", fontSize = 13.sp, color = PastelPurple, fontWeight = FontWeight.SemiBold)
                        } else {
                            Text(text = "General", fontSize = 13.sp, color = Color.Gray)
                        }
                        if (!task.audioFilePath.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "🎙️ Voice", fontSize = 12.sp, color = Color.DarkGray)
                        }
                    }
                }
            }

            Icon(Icons.Default.ChevronRight, contentDescription = "Details", tint = Color.Gray)
        }
    }
}
