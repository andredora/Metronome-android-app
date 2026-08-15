package com.metronome.app.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * A small custom palette, since the app's look (cream/dark card, olive accent pills,
 * dark trapezoid pendulum body) doesn't map cleanly onto Material3's default roles.
 */
data class MetronomeColors(
    val background: Color,
    val surface: Color,
    val pendulumBody: Color,
    val pendulumTrack: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val accent: Color,
    val pillUnselected: Color,
    val pillSelected: Color,
    val pillTextUnselected: Color,
    val weight: Color,
    val isDark: Boolean
)

val LightMetronomeColors = MetronomeColors(
    background = LightBackground,
    surface = LightSurface,
    pendulumBody = LightPendulumBody,
    pendulumTrack = LightPendulumTrack,
    textPrimary = LightTextPrimary,
    textSecondary = LightTextSecondary,
    accent = LightAccent,
    pillUnselected = LightPillUnselected,
    pillSelected = LightPillSelected,
    pillTextUnselected = LightPillTextUnselected,
    weight = WeightColorLight,
    isDark = false
)

val DarkMetronomeColors = MetronomeColors(
    background = DarkBackground,
    surface = DarkSurface,
    pendulumBody = DarkPendulumBody,
    pendulumTrack = DarkPendulumTrack,
    textPrimary = DarkTextPrimary,
    textSecondary = DarkTextSecondary,
    accent = DarkAccent,
    pillUnselected = DarkPillUnselected,
    pillSelected = DarkPillSelected,
    pillTextUnselected = DarkPillTextUnselected,
    weight = WeightColorDark,
    isDark = true
)

val LocalMetronomeColors = staticCompositionLocalOf { LightMetronomeColors }
