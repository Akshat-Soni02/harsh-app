package com.heroroutine.app.ui.champion

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.heroroutine.app.ui.theme.PastelBlue
import com.heroroutine.app.ui.theme.PastelPurple
import com.heroroutine.app.ui.theme.PastelPurpleLight
import com.heroroutine.app.ui.theme.PastelYellow
import kotlinx.coroutines.delay
import java.io.File

@Composable
fun ChampionTaskCard(
    task: TaskEntity,
    isUndoState: Boolean,
    isPlayingAudio: Boolean,
    isHinglish: Boolean,
    onToggleCompleted: (Boolean) -> Unit,
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

    if (isUndoState) {
        // Slim Undo Card
        var progress by remember { mutableFloatStateOf(1f) }
        LaunchedEffect(Unit) {
            while (progress > 0f) {
                delay(50)
                progress -= 0.01f
            }
        }
        
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = PastelPurpleLight),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isHinglish) "Shabaash! Ho Gaya 🌟" else "Great Job! Done 🌟",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PastelPurple
                    )
                    
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        modifier = Modifier.clickable { 
                            triggerVibration()
                            onToggleCompleted(false) 
                        }
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Undo, contentDescription = "Undo", tint = PastelPurple, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isHinglish) "Undo" else "Undo", color = PastelPurple, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(4.dp),
                    color = PastelPurple,
                    trackColor = Color.Transparent
                )
            }
        }
        return
    }

    val scaleAnim by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(scaleAnim),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Massive Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.3f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFFF1F5F9)),
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
                    Text(
                        text = if (task.taskType == TaskType.EXERCISE) "🤸‍♂️" else "✨",
                        fontSize = 80.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Title & Reps Count
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = task.title,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF1E293B),
                    modifier = Modifier.weight(1f)
                )

                if (task.taskType == TaskType.EXERCISE && task.targetReps > 0) {
                    Text(
                        text = "${task.targetReps}",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Black,
                        color = PastelPurple
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Listen Button
                if (!task.audioFilePath.isNullOrEmpty() && File(task.audioFilePath).exists()) {
                    Surface(
                        modifier = Modifier
                            .height(76.dp)
                            .weight(1f)
                            .clickable { onPlayAudio() },
                        shape = RoundedCornerShape(24.dp),
                        color = if (isPlayingAudio) PastelYellow else Color(0xFFF8FAFC),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (isPlayingAudio) Icons.Default.Stop else Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = if (isPlayingAudio) Color.Black else PastelBlue,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHinglish) "Suno" else "Listen",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isPlayingAudio) Color.Black else PastelBlue
                            )
                        }
                    }
                }

                // DONE Button
                Surface(
                    modifier = Modifier
                        .height(76.dp)
                        .weight(1.3f)
                        .clickable {
                            triggerVibration()
                            onToggleCompleted(true)
                        },
                    shape = RoundedCornerShape(24.dp),
                    color = CompletedGreen,
                    shadowElevation = 4.dp
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
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (isHinglish) "Ho Gaya" else "DONE",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
