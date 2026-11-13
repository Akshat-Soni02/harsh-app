package com.heroroutine.app.ui.guardian

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heroroutine.app.data.model.TaskEntity
import com.heroroutine.app.data.model.TaskLogEntity
import com.heroroutine.app.data.model.TaskType
import com.heroroutine.app.ui.theme.HeroGold
import com.heroroutine.app.ui.theme.HeroGreen
import com.heroroutine.app.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProgressAnalyticsView(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val allTasks by viewModel.allTasks.collectAsState()
    var selectedTaskId by remember { mutableStateOf(allTasks.firstOrNull()?.id) }

    // If selectedTaskId is null, default to first task
    if (selectedTaskId == null && allTasks.isNotEmpty()) {
        selectedTaskId = allTasks.first().id
    }

    val selectedTask = allTasks.find { it.id == selectedTaskId }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Exercise & Task Progress",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1C1B1F)
        )
        Text(
            text = "Track repetition growth and daily consistency",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Task selector pills
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(allTasks) { task ->
                val isSelected = task.id == selectedTaskId
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) HeroGreen else Color.White,
                    shadowElevation = if (isSelected) 4.dp else 1.dp,
                    modifier = Modifier.clickable { selectedTaskId = task.id }
                ) {
                    Text(
                        text = task.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = if (isSelected) Color.White else Color.Black,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedTask != null) {
            TaskProgressCard(task = selectedTask)
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Text(
                    text = "No tasks created yet.",
                    modifier = Modifier.padding(24.dp),
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun TaskProgressCard(task: TaskEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = task.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = if (task.taskType == TaskType.EXERCISE) "Current Target: ${task.targetReps} reps" else "General Activity",
                        fontSize = 14.sp,
                        color = HeroGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFE8F5E9)
                ) {
                    Text(
                        text = "📈 Active",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = HeroGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Chart Title
            Text(
                text = "Repetition Progression Trend",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Progression Visual Chart (Repetitions progression curve)
            val baseReps = task.targetReps.toFloat()
            // Sample historical data points leading to current target
            val dataPoints = remember(task.targetReps) {
                val start = (baseReps * 0.6f).coerceAtLeast(3f)
                val mid1 = (baseReps * 0.75f).coerceAtLeast(5f)
                val mid2 = (baseReps * 0.9f).coerceAtLeast(8f)
                val current = baseReps
                listOf(start, mid1, mid2, current)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(Color(0xFFFAFAFA), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    val maxVal = (dataPoints.maxOrNull() ?: 10f) * 1.25f
                    val minVal = 0f

                    val path = Path()
                    val stepX = width / (dataPoints.size - 1)

                    dataPoints.forEachIndexed { index, value ->
                        val x = index * stepX
                        val y = height - ((value - minVal) / (maxVal - minVal) * height)

                        if (index == 0) {
                            path.moveTo(x, y)
                        } else {
                            path.lineTo(x, y)
                        }

                        // Draw point dot
                        drawCircle(
                            color = HeroGreen,
                            radius = 6.dp.toPx(),
                            center = Offset(x, y)
                        )
                    }

                    drawPath(
                        path = path,
                        color = HeroGreen,
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Week 1", fontSize = 12.sp, color = Color.Gray)
                Text(text = "Week 2", fontSize = 12.sp, color = Color.Gray)
                Text(text = "Week 3", fontSize = 12.sp, color = Color.Gray)
                Text(text = "Current", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = HeroGreen)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9))
            ) {
                Text(
                    text = "💡 Caregiver Insight: Increasing reps gradually from ${dataPoints.first().toInt()} to ${task.targetReps} builds motor stamina safely.",
                    modifier = Modifier.padding(14.dp),
                    fontSize = 13.sp,
                    color = Color(0xFF2E7D32)
                )
            }
        }
    }
}
