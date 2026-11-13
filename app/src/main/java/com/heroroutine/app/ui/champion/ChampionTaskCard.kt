package com.heroroutine.app.ui.champion

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.heroroutine.app.data.model.TaskEntity
import com.heroroutine.app.data.model.TaskType
import com.heroroutine.app.ui.theme.CompletedGreen
import com.heroroutine.app.ui.theme.HeroGold
import com.heroroutine.app.ui.theme.HeroGreen
import com.heroroutine.app.ui.theme.HeroGreenSoft
import java.io.File

@Composable
fun ChampionTaskCard(
    task: TaskEntity,
    isCompleted: Boolean,
    isPlayingAudio: Boolean,
    onToggleCompleted: (Boolean) -> Unit,
    onPlayAudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val vibrator = context.getSystemService(Vibrator::class.java)

    val scaleAnim by animateFloatAsState(
        targetValue = if (isCompleted) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    val cardColor by animateColorAsState(
        targetValue = if (isCompleted) HeroGreenSoft else Color.White,
        label = "cardColor"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isCompleted) CompletedGreen else Color(0xFFE0E0E0),
        label = "borderColor"
    )

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
            .scale(scaleAnim),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = BorderStroke(if (isCompleted) 3.dp else 1.5.dp, borderColor),
        elevation = CardDefaults.cardElevation(if (isCompleted) 2.dp else 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            // 1. Massive Reference Photo / Visual Cue
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.6f)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFFF3F4F6)),
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
                    // Default friendly exercise visual placeholder
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (task.taskType == TaskType.EXERCISE) "🤸‍♂️" else "✨",
                            fontSize = 64.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Reference Photo",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // If completed, celebratory badge over image
                if (isCompleted) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(CompletedGreen.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = CompletedGreen,
                            shadowElevation = 6.dp
                        ) {
                            Text(
                                text = "DONE! 🌟",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Title & Target Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1C1B1F)
                    )

                    if (task.taskType == TaskType.EXERCISE && task.targetReps > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFE8F5E9)
                        ) {
                            Text(
                                text = "🔁 Repeat ${task.targetReps} times",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = HeroGreen
                            )
                        }
                    } else if (task.targetDurationSeconds > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFE3F2FD)
                        ) {
                            Text(
                                text = "⏱️ ${task.targetDurationSeconds} seconds",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1565C0)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 3. Action Buttons: Big Listen Button + Giant Done Checkmark
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Listen Button (Only if voice audio is attached)
                if (!task.audioFilePath.isNullOrEmpty() && File(task.audioFilePath).exists()) {
                    FilledIconButton(
                        onClick = onPlayAudio,
                        modifier = Modifier
                            .height(68.dp)
                            .weight(1f),
                        shape = RoundedCornerShape(20.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = if (isPlayingAudio) HeroGold else Color(0xFFF0F2F5)
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (isPlayingAudio) Icons.Default.Stop else Icons.Default.VolumeUp,
                                contentDescription = "Listen to Voice Guidance",
                                tint = if (isPlayingAudio) Color.Black else HeroGreen,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isPlayingAudio) "Playing..." else "Listen",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isPlayingAudio) Color.Black else HeroGreen
                            )
                        }
                    }
                }

                // Giant Checkmark Button
                Surface(
                    modifier = Modifier
                        .height(68.dp)
                        .weight(if (!task.audioFilePath.isNullOrEmpty()) 1.2f else 1f)
                        .clickable {
                            triggerVibration()
                            onToggleCompleted(!isCompleted)
                        },
                    shape = RoundedCornerShape(20.dp),
                    color = if (isCompleted) CompletedGreen else Color(0xFFE8F5E9),
                    border = BorderStroke(2.dp, CompletedGreen),
                    shadowElevation = if (isCompleted) 2.dp else 4.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = if (isCompleted) "Completed" else "Mark Complete",
                            tint = if (isCompleted) Color.White else CompletedGreen,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isCompleted) "DONE!" else "COMPLETE",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isCompleted) Color.White else CompletedGreen
                        )
                    }
                }
            }
        }
    }
}
