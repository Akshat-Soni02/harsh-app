package com.heroroutine.app.ui.champion

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.heroroutine.app.data.model.TaskEntity
import com.heroroutine.app.data.model.TaskType
import com.heroroutine.app.ui.theme.CardWhite
import com.heroroutine.app.ui.theme.MintGreen
import com.heroroutine.app.ui.theme.MintGreenDark
import com.heroroutine.app.ui.theme.SkyBlue
import com.heroroutine.app.ui.theme.SkyBlueBorder
import com.heroroutine.app.ui.theme.SkyBlueSoft
import com.heroroutine.app.ui.theme.TextDark
import com.heroroutine.app.ui.theme.TextSecondary
import com.heroroutine.app.ui.theme.WarmCardBorder
import com.heroroutine.app.ui.theme.WarmOrange
import com.heroroutine.app.ui.theme.WarmOrangeSoft
import java.io.File

@Composable
fun ChampionTaskCard(
    task: TaskEntity,
    isPlayingAudio: Boolean,
    onMarkCompleted: () -> Unit,
    onPlayAudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val vibrator = remember { context.getSystemService(Vibrator::class.java) }

    fun triggerVibration() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(100)
            }
        } catch (e: Exception) {
            // Ignore
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, WarmCardBorder, RoundedCornerShape(26.dp)),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            // Large Clear Visual Image Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.35f)
                    .clip(RoundedCornerShape(20.dp))
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
                        modifier = Modifier.size(76.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Task Name (smaller) and Clear Big Reps Display
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = task.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                )

                // Reps / Sets Display
                if (task.taskType == TaskType.EXERCISE) {
                    if (task.targetSets > 0) {
                        // "x sets of y reps" format
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${task.targetSets}",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black,
                                color = WarmOrange
                            )
                            Text(
                                text = " sets of ",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                            Text(
                                text = "${task.targetReps}",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black,
                                color = WarmOrange
                            )
                            Text(
                                text = " reps",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                    } else if (task.targetReps > 0) {
                        // "Reps only" format
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${task.targetReps}",
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Black,
                                color = WarmOrange
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Reps",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = WarmOrange,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Two Big Simple Action Buttons: Left Listen, Right Done
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Listen Button
                Surface(
                    modifier = Modifier
                        .height(58.dp)
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onPlayAudio() },
                    shape = RoundedCornerShape(18.dp),
                    color = if (isPlayingAudio) Color(0xFFFEF3C7) else SkyBlueSoft,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isPlayingAudio) Color(0xFFFDE68A) else SkyBlueBorder)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (isPlayingAudio) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = "Play Audio",
                            tint = if (isPlayingAudio) Color(0xFFB45309) else SkyBlue,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isPlayingAudio) "Playing..." else "Listen",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isPlayingAudio) Color(0xFFB45309) else SkyBlue
                        )
                    }
                }

                // Done Button
                Surface(
                    modifier = Modifier
                        .height(58.dp)
                        .weight(1.1f)
                        .clip(RoundedCornerShape(18.dp))
                        .clickable {
                            triggerVibration()
                            onMarkCompleted()
                        },
                    shape = RoundedCornerShape(18.dp),
                    color = MintGreen,
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Done",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Done",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
