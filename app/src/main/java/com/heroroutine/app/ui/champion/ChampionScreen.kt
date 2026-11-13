package com.heroroutine.app.ui.champion

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heroroutine.app.ui.components.CelebrationOverlay
import com.heroroutine.app.ui.theme.BackgroundLight
import com.heroroutine.app.ui.theme.CompletedGreen
import com.heroroutine.app.ui.theme.PastelBlue
import com.heroroutine.app.ui.theme.PastelPurple
import com.heroroutine.app.ui.theme.PastelPurpleLight
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
    val selectedSessionId by viewModel.selectedSessionId.collectAsState()
    val showCelebration by viewModel.showCelebration.collectAsState()
    val isPlayingAudio by viewModel.audioPlayer.isPlaying.collectAsState()
    val pendingUndoTasks by viewModel.pendingUndoTasks.collectAsState()
    val isHinglish by viewModel.isHinglish.collectAsState()

    var showUnlockConfirm by remember { mutableStateOf(false) }

    val currentSession = sessions.find { it.id == selectedSessionId } ?: sessions.firstOrNull()
    val currentTasks = allTasks.filter { it.sessionId == currentSession?.id }

    val completedTaskIds = todayLogs.filter { it.completed }.map { it.taskId }.toSet()

    // Filter tasks to show: those NOT completed OR those in pending Undo state
    val visibleTasks = currentTasks.filter { 
        !completedTaskIds.contains(it.id) || pendingUndoTasks.containsKey(it.id) 
    }

    val quotes = listOf(
        "Rukna nahi hai, chalte rehna hai!",
        "You are a champion!",
        "Shabaash! Thoda aur!",
        "Ek dum badhiya!",
        "You're doing great!"
    )
    val randomQuote = remember { quotes.random() }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Streak
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🔥", fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${streakInfo.currentStreak}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = PastelPurple
                    )
                }

                // Progress Bar
                LinearProgressIndicator(
                    progress = { streakInfo.todayCompletionPercentage },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 20.dp)
                        .height(14.dp)
                        .clip(RoundedCornerShape(7.dp)),
                    color = CompletedGreen,
                    trackColor = Color(0xFFE2E8F0)
                )

                // Settings Lock
                if (!showUnlockConfirm) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 2.dp,
                        modifier = Modifier.clickable { showUnlockConfirm = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.Gray,
                            modifier = Modifier.padding(12.dp).size(24.dp)
                        )
                    }
                } else {
                    var clickCount by remember { mutableStateOf(0) }
                    Button(
                        onClick = {
                            clickCount++
                            if (clickCount >= 2) {
                                showUnlockConfirm = false
                                viewModel.switchMode(AppMode.GUARDIAN)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PastelPurple)
                    ) {
                        Text(if (isHinglish) "Double tap karein" else "Double Tap Setup")
                    }
                }
            }
        },
        containerColor = BackgroundLight
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                // Session Indicator
                item {
                    if (currentSession != null) {
                        Text(
                            text = currentSession.title,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1E293B)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isHinglish) randomQuote else "Keep it up!",
                            fontSize = 18.sp,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                if (sessions.size > 1) {
                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(sessions) { session ->
                                val isSelected = session.id == currentSession?.id
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = if (isSelected) PastelBlue else Color.White,
                                    shadowElevation = if (isSelected) 4.dp else 1.dp,
                                    modifier = Modifier.clickable {
                                        viewModel.selectSession(session.id)
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = session.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = if (isSelected) Color.White else Color.DarkGray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (visibleTasks.isEmpty() && currentTasks.isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(32.dp),
                            colors = CardDefaults.cardColors(containerColor = PastelPurpleLight)
                        ) {
                            Column(
                                modifier = Modifier.padding(40.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "🎉", fontSize = 64.sp)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = if (isHinglish) "Sab ho gaya!" else "All Done!",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 28.sp,
                                    color = PastelPurple
                                )
                            }
                        }
                    }
                } else {
                    items(visibleTasks, key = { it.id }) { task ->
                        val isUndo = pendingUndoTasks.containsKey(task.id)
                        val isThisPlaying = viewModel.audioPlayer.isPathPlaying(task.audioFilePath ?: "")

                        ChampionTaskCard(
                            task = task,
                            isUndoState = isUndo,
                            isPlayingAudio = isThisPlaying,
                            isHinglish = isHinglish,
                            onToggleCompleted = { completed ->
                                viewModel.toggleTask(task, completed)
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

            CelebrationOverlay(
                visible = showCelebration,
                isHinglish = isHinglish,
                onDismiss = { viewModel.dismissCelebration() }
            )
        }
    }
}
