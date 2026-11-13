package com.heroroutine.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = HeroGreen,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = HeroGreenSoft,
    onPrimaryContainer = HeroGreenDark,
    secondary = HeroGold,
    onSecondary = androidx.compose.ui.graphics.Color.Black,
    secondaryContainer = HeroGoldLight,
    onSecondaryContainer = androidx.compose.ui.graphics.Color.Black,
    tertiary = HeroBlue,
    background = SurfaceBackground,
    surface = CardBackground,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun HeroRoutineTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.statusBarColor = HeroGreenDark.toArgb()
            window?.navigationBarColor = HeroGreenDark.toArgb()
            if (window != null) {
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
