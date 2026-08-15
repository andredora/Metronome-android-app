package com.metronome.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun MetronomeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val customColors = if (darkTheme) DarkMetronomeColors else LightMetronomeColors
    val materialScheme = if (darkTheme) {
        darkColorScheme(background = DarkBackground, surface = DarkSurface)
    } else {
        lightColorScheme(background = LightBackground, surface = LightSurface)
    }

    CompositionLocalProvider(LocalMetronomeColors provides customColors) {
        MaterialTheme(
            colorScheme = materialScheme,
            typography = AppTypography,
            content = content
        )
    }
}
