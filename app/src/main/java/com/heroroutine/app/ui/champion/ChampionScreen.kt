package com.heroroutine.app.ui.champion

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.heroroutine.app.data.model.TaskEntity
import com.heroroutine.app.ui.components.CelebrationOverlay
import com.heroroutine.app.ui.theme.CreamBackground
import com.heroroutine.app.ui.theme.MintGreen
import com.heroroutine.app.ui.theme.MintGreenDark
import com.heroroutine.app.ui.theme.MintGreenSoft
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

@Composable
fun ChampionScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val sessions by viewModel.sessions.collectAsState()
    val allTasks by viewModel.allTasks.collectAsState()
    val todayLogs by viewModel.todayLogs.collectAsState()
    val streakInfo by viewModel.streakInfo.collectAsState()
    val showCelebration by viewModel.showCelebration.collectAsState()

    var showGuardianModal by remember { mutableStateOf(false) }
    var undoTask by remember { mutableStateOf<TaskEntity?>(null) }
    val undoProgress = remember { Animatable(1f) }

    val completedTaskIds = todayLogs.filter { it.completed }.map { it.taskId }.toSet()

    // 5-second animated undo timer
    LaunchedEffect(undoTask) {
        if (undoTask != null) {
            undoProgress.snapTo(1f)
            undoProgress.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 5000, easing = LinearEasing)
            )
            undoTask = null
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Streak: Flame + Big Number (no extra text)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 12.dp)
                ) {
                    Text(text = "🔥", fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${streakInfo.currentStreak}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = TextDark
                    )
                }

                // Middle: Full-day progress bar (no numbers/text)
                LinearProgressIndicator(
                    progress = { streakInfo.todayCompletionPercentage },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    color = WarmOrange,
                    trackColor = Color(0xFFEFE6DA)
                )

                // Right: Settings gear icon to switch to Guardian mode
                Surface(
                    shape = CircleShape,
                    color = WarmWhite,
                    border = BorderStroke(1.dp, WarmCardBorder),
                    shadowElevation = 1.dp,
                    modifier = Modifier.clickable { showGuardianModal = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Switch to Guardian Mode",
                        tint = TextSecondary,
                        modifier = Modifier
                            .padding(10.dp)
                            .size(24.dp)
                    )
                }
            }
        },
        containerColor = CreamBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                if (sessions.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, WarmCardBorder, RoundedCornerShape(26.dp)),
                            shape = RoundedCornerShape(26.dp),
                            colors = CardDefaults.cardColors(containerColor = WarmWhite)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No routines set up yet.",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Ask your parent to set up routines in Guardian mode.",
                                    fontSize = 14.sp,
                                    color = TextMuted,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(sessions, key = { it.id }) { session ->
                        val sessionTasks = allTasks.filter { it.sessionId == session.id }
                        val remainingTasks = sessionTasks.filter { !completedTaskIds.contains(it.id) }
                        val isSessionCompleted = sessionTasks.isNotEmpty() && remainingTasks.isEmpty()

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, WarmCardBorder, RoundedCornerShape(26.dp)),
                            shape = RoundedCornerShape(26.dp),
                            colors = CardDefaults.cardColors(containerColor = WarmWhite),
                            elevation = CardDefaults.cardElevation(1.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                // Session Header inside Card: Left icon + name, right timing
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
                                            Text(
                                                text = session.iconEmoji,
                                                fontSize = 24.sp,
                                                modifier = Modifier.padding(end = 10.dp)
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.FitnessCenter,
                                                contentDescription = null,
                                                tint = WarmOrange,
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .padding(end = 6.dp)
                                            )
                                        }

                                        Text(
                                            text = session.title,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Black,
                                            color = TextDark
                                        )
                                    }

                                    if (!session.targetTime.isNullOrBlank()) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(WarmOrangeSoft)
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Schedule,
                                                contentDescription = null,
                                                tint = WarmOrange,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = session.targetTime!!,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = WarmOrange
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Session Content: Completed State OR Tasks List
                                if (sessionTasks.isEmpty()) {
                                    Text(
                                        text = "No exercises in this session.",
                                        fontSize = 14.sp,
                                        color = TextMuted,
                                        modifier = Modifier.padding(vertical = 12.dp)
                                    )
                                } else if (isSessionCompleted) {
                                    // Clean checkmark state when session is done
                                    Card(
                                        shape = RoundedCornerShape(20.dp),
                                        colors = CardDefaults.cardColors(containerColor = MintGreenSoft),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 20.dp, horizontal = 16.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Session Completed",
                                                tint = MintGreen,
                                                modifier = Modifier.size(28.dp)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = "Session Completed!",
                                                fontWeight = FontWeight.Black,
                                                color = MintGreenDark,
                                                fontSize = 18.sp
                                            )
                                        }
                                    }
                                } else {
                                    // Nested Tasks
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(14.dp)
                                    ) {
                                        remainingTasks.forEach { task ->
                                            val isThisPlaying = viewModel.audioPlayer.isPathPlaying(task.audioFilePath ?: "")

                                            ChampionTaskCard(
                                                task = task,
                                                isPlayingAudio = isThisPlaying,
                                                onMarkCompleted = {
                                                    undoTask = task
                                                    viewModel.toggleTask(task, true)
                                                },
                                                onPlayAudio = {
                                                    task.audioFilePath?.let { path ->
                                                        if (isThisPlaying) {
                                                            viewModel.audioPlayer.stopAudio()
                                                        } else {
                                                            viewModel.audioPlayer.playAudio(path)
                                                        }
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Floating Undo Notification (stays 5s with progress bar)
            AnimatedVisibility(
                visible = undoTask != null,
                enter = slideInVertically(initialOffsetY = { it * 2 }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it * 2 }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                undoTask?.let { task ->
                    Card(
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = TextDark),
                        elevation = CardDefaults.cardElevation(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 18.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = MintGreen,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "${task.title} completed",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color.White
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = WarmOrange,
                                    modifier = Modifier.clickable {
                                        viewModel.toggleTask(task, false)
                                        undoTask = null
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Undo,
                                            contentDescription = "Undo",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Undo",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = Color.White
                                        )
                                    }
                                }
                            }

                            // 5s animated countdown bar
                            LinearProgressIndicator(
                                progress = { undoProgress.value },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp),
                                color = WarmOrange,
                                trackColor = Color.White.copy(alpha = 0.2f)
                            )
                        }
                    }
                }
            }

            // Mode Switch Modal (single click inside, dismissible outside)
            if (showGuardianModal) {
                Dialog(onDismissRequest = { showGuardianModal = false }) {
                    Card(
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = WarmWhite),
                        elevation = CardDefaults.cardElevation(12.dp),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Guardian Mode",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = TextDark
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Switch to dashboard to manage routines, exercise reps, and view progress.",
                                fontSize = 14.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = {
                                    showGuardianModal = false
                                    viewModel.switchMode(AppMode.GUARDIAN)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = WarmOrange),
                                shape = RoundedCornerShape(18.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                            ) {
                                Text(
                                    text = "Enter Guardian Mode",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Celebration Modal: falling confetti, star, "Shabaash!", big streak number, dismiss on tap anywhere
            CelebrationOverlay(
                visible = showCelebration,
                currentStreak = streakInfo.currentStreak,
                onDismiss = { viewModel.dismissCelebration() }
            )
        }
    }
}
