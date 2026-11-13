package com.heroroutine.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = PastelPurple,
    onPrimary = Color.White,
    primaryContainer = PastelPurpleLight,
    onPrimaryContainer = HeroGreenDark,
    secondary = PastelOrange,
    onSecondary = Color.White,
    secondaryContainer = PastelOrangeLight,
    onSecondaryContainer = Color.Black,
    tertiary = PastelBlue,
    background = BackgroundLight,
    surface = CardWhite,
    onBackground = TextDark,
    onSurface = TextDark
)

@Composable
fun HeroRoutineTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.statusBarColor = BackgroundLight.toArgb()
            window?.navigationBarColor = BackgroundLight.toArgb()
            if (window != null) {
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
            }
        }
    }

    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
