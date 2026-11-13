package com.heroroutine.app.ui.guardian

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
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
import com.heroroutine.app.ui.theme.CreamBackground
import com.heroroutine.app.ui.theme.MintGreen
import com.heroroutine.app.ui.theme.MintGreenDark
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
            .background(CreamBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Streak Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = WarmOrangeSoft),
            border = BorderStroke(1.5.dp, WarmOrangeBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Current Streak",
                        color = WarmOrange,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${streakInfo.currentStreak} Days",
                        color = TextDark,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(WarmOrange),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🔥", fontSize = 28.sp)
                }
            }
        }

        // Today's Routine Completion Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = WarmWhite),
            border = BorderStroke(1.dp, WarmCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Today's Progress",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = TextDark
                    )
                    Text(
                        text = "${(streakInfo.todayCompletionPercentage * 100).toInt()}%",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = WarmOrange
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = { streakInfo.todayCompletionPercentage },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = WarmOrange,
                    trackColor = Color(0xFFEFE6DA)
                )
            }
        }

        // 7-Day Consistency Matrix
        Text(
            text = "Weekly Activity",
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
            color = TextDark,
            modifier = Modifier.padding(start = 4.dp)
        )

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = WarmWhite),
            border = BorderStroke(1.dp, WarmCardBorder),
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
                                fontWeight = FontWeight.Bold,
                                color = if (day == today) WarmOrange else TextSecondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (isDone) MintGreenSoft else Color(0xFFF1F5F9))
                                    .border(1.dp, if (isDone) MintGreen else WarmCardBorder, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isDone) {
                                    Text(
                                        text = "${logsForDay.size}",
                                        color = MintGreenDark,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                } else {
                                    Text(
                                        text = "0",
                                        color = TextMuted,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Body Parts Worked Breakdown
        Text(
            text = "Targeted Body Areas",
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
            color = TextDark,
            modifier = Modifier.padding(start = 4.dp)
        )

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = WarmWhite),
            border = BorderStroke(1.dp, WarmCardBorder),
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
                    Text(
                        text = "No body parts tracked yet. Tag body parts when editing exercises.",
                        color = TextMuted,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    partsCount.entries.sortedByDescending { it.value }.forEachIndexed { index, (part, count) ->
                        if (index > 0) {
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = WarmOrangeSoft,
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.FitnessCenter,
                                            contentDescription = null,
                                            tint = WarmOrange,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = part,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MintGreenSoft,
                                border = BorderStroke(1.dp, MintGreen)
                            ) {
                                Text(
                                    text = "$count completions",
                                    color = MintGreenDark,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
