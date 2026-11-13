package com.heroroutine.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heroroutine.app.ui.theme.PastelPurple
import com.heroroutine.app.ui.theme.PastelOrange
import com.heroroutine.app.ui.theme.PastelBlue
import com.heroroutine.app.ui.theme.PastelGreen
import com.heroroutine.app.ui.theme.PastelYellow
import com.heroroutine.app.ui.theme.PastelRed
import kotlin.random.Random

private data class Particle(
    val x: Float,
    val y: Float,
    val color: Color,
    val size: Float,
    val speedX: Float,
    val speedY: Float
)

@Composable
fun CelebrationOverlay(
    visible: Boolean,
    isHinglish: Boolean,
    onDismiss: () -> Unit
) {
    if (!visible) return

    val particles = remember {
        val colors = listOf(PastelYellow, PastelOrange, PastelGreen, PastelBlue, PastelRed, PastelPurple)
        List(80) {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                color = colors.random(),
                size = Random.nextFloat() * 20f + 10f,
                speedX = (Random.nextFloat() - 0.5f) * 20f,
                speedY = Random.nextFloat() * 40f + 10f
            )
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + scaleIn(initialScale = 0.8f),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.9f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                ),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                particles.forEach { p ->
                    drawCircle(
                        color = p.color,
                        radius = p.size,
                        center = Offset(p.x * width, p.y * height)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "🎉",
                    fontSize = 100.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = if (isHinglish) "Bahut Badhiya!" else "Awesome Job!",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black,
                    color = PastelPurple,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = PastelPurple),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.height(64.dp).padding(horizontal = 32.dp)
                ) {
                    Text(if (isHinglish) "Chalo!" else "Done", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
