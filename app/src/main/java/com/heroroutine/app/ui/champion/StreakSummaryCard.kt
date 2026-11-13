package com.heroroutine.app.ui.champion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heroroutine.app.data.repository.StreakInfo
import com.heroroutine.app.ui.theme.HeroGold
import com.heroroutine.app.ui.theme.HeroGreen

@Composable
fun StreakSummaryCard(
    streakInfo: StreakInfo,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🔥",
                        fontSize = 36.sp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "${streakInfo.currentStreak} Day Streak!",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = HeroGreen
                        )
                        Text(
                            text = if (streakInfo.completedToday) "Today's goal achieved! 🌟" else "Keep your momentum going! 💪",
                            fontSize = 15.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Forgiving streak progress bar
            LinearProgressIndicator(
                progress = { streakInfo.todayCompletionPercentage },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp)),
                color = if (streakInfo.todayCompletionPercentage >= 0.6f) HeroGreen else HeroGold,
                trackColor = Color(0xFFEEEEEE)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${(streakInfo.todayCompletionPercentage * 100).toInt()}% Done",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = HeroGreen
                )
                Text(
                    text = "Goal: 60% protects streak",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
