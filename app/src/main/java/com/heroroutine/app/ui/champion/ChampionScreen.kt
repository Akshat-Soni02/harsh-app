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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heroroutine.app.data.model.SessionEntity
import com.heroroutine.app.data.model.TaskEntity
import com.heroroutine.app.ui.components.CelebrationOverlay
import com.heroroutine.app.ui.components.HoldToSwitchButton
import com.heroroutine.app.ui.theme.HeroGreen
import com.heroroutine.app.ui.theme.HeroGreenDark
import com.heroroutine.app.viewmodel.AppMode
import com.heroroutine.app.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
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

    val currentSession = sessions.find { it.id == selectedSessionId } ?: sessions.firstOrNull()
    val currentTasks = allTasks.filter { it.sessionId == currentSession?.id }

    val completedTaskIds = todayLogs.filter { it.completed }.map { it.taskId }.toSet()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Champion Mode 🌟",
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp,
                            color = Color.White
                        )
                    }
                },
                actions = {
                    HoldToSwitchButton(
                        modifier = Modifier.padding(end = 12.dp),
                        onUnlocked = { viewModel.switchMode(AppMode.GUARDIAN) }
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = HeroGreenDark
                )
            )
        },
        containerColor = Color(0xFFF6F8FA)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Streak & Progress Header
                item {
                    StreakSummaryCard(streakInfo = streakInfo)
                }

                // Session Tabs (e.g. Morning, Evening)
                if (sessions.size > 1) {
                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(sessions) { session ->
                                val isSelected = session.id == currentSession?.id
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (isSelected) HeroGreen else Color.White,
                                    shadowElevation = if (isSelected) 4.dp else 1.dp,
                                    modifier = Modifier.clickable {
                                        viewModel.selectSession(session.id)
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = session.iconEmoji, fontSize = 20.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = session.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = if (isSelected) Color.White else Color.Black
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Task Cards
                if (currentTasks.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(
                                modifier = Modifier.padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "🎉", fontSize = 48.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "All Done or No Tasks Yet!",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                } else {
                    items(currentTasks, key = { it.id }) { task ->
                        val isDone = completedTaskIds.contains(task.id)
                        val isThisPlaying = viewModel.audioPlayer.isPathPlaying(task.audioFilePath ?: "")

                        ChampionTaskCard(
                            task = task,
                            isCompleted = isDone,
                            isPlayingAudio = isThisPlaying,
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

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }

            // Celebration Modal when goals are reached!
            CelebrationOverlay(
                visible = showCelebration,
                streakDays = streakInfo.currentStreak,
                onDismiss = { viewModel.dismissCelebration() }
            )
        }
    }
}
