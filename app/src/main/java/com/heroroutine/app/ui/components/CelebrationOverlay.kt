package com.heroroutine.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heroroutine.app.ui.theme.CardWhite
import com.heroroutine.app.ui.theme.MintGreen
import com.heroroutine.app.ui.theme.SkyBlue
import com.heroroutine.app.ui.theme.TextDark
import com.heroroutine.app.ui.theme.WarmOrange
import com.heroroutine.app.ui.theme.WarmOrangeBorder
import com.heroroutine.app.ui.theme.WarmOrangeSoft
import kotlinx.coroutines.delay
import kotlin.random.Random

private data class Confetti(
    val initialX: Float,
    val initialY: Float,
    val color: Color,
    val size: Float,
    val speed: Float
)

@Composable
fun CelebrationOverlay(
    visible: Boolean,
    currentStreak: Int,
    onDismiss: () -> Unit
) {
    if (!visible) return

    // Auto-dismiss after 4.5 seconds if user doesn't tap; play celebration chime
    LaunchedEffect(visible) {
        if (visible) {
            com.heroroutine.app.media.CelebrationSoundPlayer.playCelebrationChime()
            delay(4500)
            onDismiss()
        }
    }

    val confetti = remember {
        val colors = listOf(WarmOrange, MintGreen, SkyBlue, Color(0xFFF59E0B), Color(0xFF8B5CF6))
        List(60) {
            Confetti(
                initialX = Random.nextFloat(),
                initialY = Random.nextFloat() * -0.5f,
                color = colors.random(),
                size = Random.nextFloat() * 12f + 8f,
                speed = Random.nextFloat() * 0.4f + 0.6f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "confetti")
    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "confettiProgress"
    )

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + scaleIn(initialScale = 0.85f),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                ),
            contentAlignment = Alignment.Center
        ) {
            // Falling confetti canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                confetti.forEach { c ->
                    val yProgress = (c.initialY + animProgress * c.speed) % 1.2f
                    val y = yProgress * height
                    val x = c.initialX * width
                    drawCircle(
                        color = c.color,
                        radius = c.size,
                        center = Offset(x, y)
                    )
                }
            }

            // Simplified celebration card: Star + Shabaash + Big streak number
            Card(
                shape = RoundedCornerShape(36.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                elevation = CardDefaults.cardElevation(16.dp),
                modifier = Modifier
                    .padding(32.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDismiss
                    )
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Big Star Graphic in Circle
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .background(WarmOrange),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star",
                            tint = Color.White,
                            modifier = Modifier.size(54.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Shabaash!",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        color = TextDark,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Big Streak Number: Fire + big number (no extra text)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(WarmOrangeSoft)
                            .padding(horizontal = 24.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "🔥", fontSize = 32.sp)
                            Text(
                                text = "$currentStreak",
                                fontSize = 40.sp,
                                fontWeight = FontWeight.Black,
                                color = WarmOrange
                            )
                        }
                    }
                }
            }
        }
    }
}
