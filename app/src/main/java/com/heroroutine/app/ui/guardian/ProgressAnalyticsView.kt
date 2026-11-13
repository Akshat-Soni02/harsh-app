package com.heroroutine.app.ui.guardian

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heroroutine.app.ui.theme.CompletedGreen
import com.heroroutine.app.ui.theme.PastelPurple
import com.heroroutine.app.viewmodel.MainViewModel
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun ProgressAnalyticsView(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val allLogs by viewModel.allLogs.collectAsState()
    val allTasks by viewModel.allTasks.collectAsState()
    val streakInfo by viewModel.streakInfo.collectAsState()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Key Metrics
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = PastelPurple),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(20.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total Streak", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                    Text("${streakInfo.currentStreak} Days", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                }
                Text("🔥", fontSize = 48.sp)
            }
        }
        
        Text("Weekly Consistency", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(start = 8.dp))
        
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
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
                        val logsForDay = allLogs.filter { it.dateEpochDay == epoch && it.completed }
                        val isDone = logsForDay.isNotEmpty()
                        
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
                                    Text("${logsForDay.size}", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
        
        Text("Body Parts Worked", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(start = 8.dp))
        
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                val partsCount = mutableMapOf<String, Int>()
                
                allLogs.filter { it.completed }.forEach { log ->
                    val task = allTasks.find { it.id == log.taskId }
                    if (task != null && task.bodyParts.isNotBlank()) {
                        task.bodyParts.split(",").map { it.trim() }.forEach { part ->
                            if (part.isNotBlank()) {
                                partsCount[part] = (partsCount[part] ?: 0) + 1
                            }
                        }
                    }
                }
                
                if (partsCount.isEmpty()) {
                    Text("No body parts tracked yet. Add them in exercise details.", color = Color.Gray)
                } else {
                    partsCount.entries.sortedByDescending { it.value }.forEach { (part, count) ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(part, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                            Text("$count exercises", color = PastelPurple, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
