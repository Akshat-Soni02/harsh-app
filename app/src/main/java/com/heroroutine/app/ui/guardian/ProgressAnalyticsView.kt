package com.heroroutine.app.ui.guardian

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

data class BodyPartMonthlyStat(
    val partName: String,
    val completionsThisMonth: Int,
    val totalOccurrencesThisMonth: Int,
    val completionRate: Float
)

@Composable
fun ProgressAnalyticsView(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val allLogs by viewModel.allLogs.collectAsState()
    val allTasks by viewModel.allTasks.collectAsState()
    val streakInfo by viewModel.streakInfo.collectAsState()

    var monthOffset by remember { mutableIntStateOf(0) }
    val selectedMonth = remember(monthOffset) {
        YearMonth.now().plusMonths(monthOffset.toLong())
    }

    // Export & Import Launchers
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/zip")
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                try {
                    context.contentResolver.openOutputStream(it)?.use { outStream ->
                        val result = viewModel.exportBackup(outStream)
                        result.onSuccess { stats ->
                            Toast.makeText(
                                context,
                                "Backup saved! (${stats.sessionCount} sessions, ${stats.taskCount} tasks, ${stats.logCount} chart logs)",
                                Toast.LENGTH_LONG
                            ).show()
                        }.onFailure { err ->
                            Toast.makeText(context, "Backup failed: ${err.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Error saving backup: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                try {
                    context.contentResolver.openInputStream(it)?.use { inStream ->
                        val result = viewModel.importBackup(inStream)
                        result.onSuccess { stats ->
                            Toast.makeText(
                                context,
                                "Restored! (${stats.taskCount} tasks, ${stats.logCount} chart records)",
                                Toast.LENGTH_LONG
                            ).show()
                        }.onFailure { err ->
                            Toast.makeText(context, "Restore failed: ${err.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Error reading backup: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

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

        // Monthly Consistency Matrix with < and > Navigation
        Text(
            text = "Monthly Consistency",
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
                // Month Navigation Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { monthOffset-- }) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Month", tint = TextDark)
                    }

                    Text(
                        text = selectedMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = TextDark
                    )

                    IconButton(
                        onClick = { if (monthOffset < 0) monthOffset++ },
                        enabled = monthOffset < 0
                    ) {
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = "Next Month",
                            tint = if (monthOffset < 0) TextDark else TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Day of Week Header
                val dayHeaders = listOf("M", "T", "W", "T", "F", "S", "S")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    dayHeaders.forEach { d ->
                        Text(
                            text = d,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            modifier = Modifier.width(36.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Calendar Grid
                val daysInMonth = selectedMonth.lengthOfMonth()
                val firstDayOfWeek = selectedMonth.atDay(1).dayOfWeek.value // 1 = Mon ... 7 = Sun
                val paddingBefore = firstDayOfWeek - 1
                val totalCells = paddingBefore + daysInMonth
                val rows = (totalCells + 6) / 7

                val today = LocalDate.now()
                var activeDaysCount = 0
                var totalCompletedInMonth = 0

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    for (r in 0 until rows) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            for (c in 0 until 7) {
                                val cellIndex = r * 7 + c
                                val dayNumber = cellIndex - paddingBefore + 1

                                if (dayNumber in 1..daysInMonth) {
                                    val date = selectedMonth.atDay(dayNumber)
                                    val epoch = date.toEpochDay()
                                    val logsOnDay = allLogs.filter { it.dateEpochDay == epoch && it.completed }
                                    val count = logsOnDay.size
                                    if (count > 0) {
                                        activeDaysCount++
                                        totalCompletedInMonth += count
                                    }
                                    val isToday = date == today

                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(if (count > 0) MintGreenSoft else Color(0xFFF1F5F9))
                                            .border(
                                                width = if (isToday) 1.5.dp else 1.dp,
                                                color = if (isToday) WarmOrange else if (count > 0) MintGreen else Color(0xFFE2E8F0),
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (count > 0) {
                                            Text(
                                                text = "$count",
                                                color = MintGreenDark,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                        } else {
                                            Text(
                                                text = "$dayNumber",
                                                fontSize = 12.sp,
                                                fontWeight = if (isToday) FontWeight.Black else FontWeight.Medium,
                                                color = if (isToday) WarmOrange else TextMuted
                                            )
                                        }
                                    }
                                } else {
                                    Box(modifier = Modifier.size(36.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Monthly Summary
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (activeDaysCount > 0) MintGreenSoft else Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, if (activeDaysCount > 0) MintGreen else WarmCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$activeDaysCount active days in ${selectedMonth.format(DateTimeFormatter.ofPattern("MMM"))}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Text(
                            text = "$totalCompletedInMonth exercises done",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = if (activeDaysCount > 0) MintGreenDark else TextSecondary
                        )
                    }
                }
            }
        }

        // Targeted Body Areas Breakdown
        // Evaluates training health: (Completions this month / Total occurrences this month)
        // Strictly ordered from highest rate to lowest
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
                val currentYearMonth = YearMonth.now()
                val daysInCurrentMonth = currentYearMonth.lengthOfMonth()
                val startOfMonthEpoch = currentYearMonth.atDay(1).toEpochDay()
                val endOfMonthEpoch = currentYearMonth.atEndOfMonth().toEpochDay()

                val allDefinedParts = allTasks
                    .flatMap { it.bodyParts.split(",").map { p -> p.trim() } }
                    .filter { it.isNotBlank() }
                    .distinct()

                val bodyPartStats = allDefinedParts.map { part ->
                    val tasksWithPart = allTasks.filter { task ->
                        task.bodyParts.split(",").map { it.trim() }.contains(part)
                    }
                    val totalExpected = tasksWithPart.size * daysInCurrentMonth
                    val completions = allLogs.count { log ->
                        log.completed &&
                        log.dateEpochDay in startOfMonthEpoch..endOfMonthEpoch &&
                        tasksWithPart.any { it.id == log.taskId }
                    }
                    val rate = if (totalExpected > 0) completions.toFloat() / totalExpected.toFloat() else 0f
                    BodyPartMonthlyStat(
                        partName = part,
                        completionsThisMonth = completions,
                        totalOccurrencesThisMonth = totalExpected,
                        completionRate = rate
                    )
                }.sortedWith(
                    compareByDescending<BodyPartMonthlyStat> { it.completionRate }
                        .thenByDescending { it.completionsThisMonth }
                )

                if (bodyPartStats.isEmpty()) {
                    Text(
                        text = "No body parts tracked yet. Tag body parts when editing exercises in the Guardian dashboard.",
                        color = TextMuted,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    Text(
                        text = "Monthly Training Health (Sorted highest to lowest rate):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    bodyPartStats.forEachIndexed { index, stat ->
                        if (index > 0) {
                            Spacer(modifier = Modifier.height(14.dp))
                        }

                        val percent = (stat.completionRate * 100).toInt()
                        val (statusText, statusBg, statusColor) = when {
                            percent >= 70 -> Triple("Well Trained", MintGreenSoft, MintGreenDark)
                            percent >= 40 -> Triple("On Track", WarmOrangeSoft, WarmOrange)
                            else -> Triple("Needs Attention", Color(0xFFFEE2E2), Color(0xFFDC2626))
                        }

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = CircleShape,
                                        color = WarmOrangeSoft,
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.FitnessCenter,
                                                contentDescription = null,
                                                tint = WarmOrange,
                                                modifier = Modifier.size(15.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = stat.partName,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextDark
                                        )
                                        Text(
                                            text = "${stat.completionsThisMonth} / ${stat.totalOccurrencesThisMonth} occurrences this month",
                                            fontSize = 12.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = statusBg,
                                    border = BorderStroke(1.dp, statusColor.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = "$statusText ($percent%)",
                                        color = statusColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            LinearProgressIndicator(
                                progress = { stat.completionRate.coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = statusColor,
                                trackColor = Color(0xFFF1EDE6)
                            )
                        }
                    }
                }
            }
        }

        // Routine Data Backup & Device Transfer Section
        Text(
            text = "Data Backup & Device Transfer",
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
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Save all exercises, family voice guidance clips, reference photos, and complete chart history into a backup file. When switching to a new phone, tap 'Restore Routines' to pick your backup file and restore everything.",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmm", Locale.US).format(Date())
                            exportLauncher.launch("Shabaash_Backup_$timeStamp.zip")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WarmOrange),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Backup Routines", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            importLauncher.launch(arrayOf("application/zip", "*/*"))
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Restore Routines", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
