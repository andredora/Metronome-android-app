package com.metronome.app

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.metronome.app.components.BeatIndicatorRow
import com.metronome.app.components.BpmAndTempoRow
import com.metronome.app.components.BpmSlider
import com.metronome.app.components.PendulumView
import com.metronome.app.components.TimeSignatureRow
import com.metronome.app.components.TopBar
import com.metronome.app.models.timeSignatures
import com.metronome.app.ui.theme.LocalMetronomeColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Composable
fun MetronomeScreen(
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit
) {
    val colors = LocalMetronomeColors.current

    var bpm by remember { mutableIntStateOf(120) }
    var selectedSignature by remember { mutableIntStateOf(0) }
    var accentedBeats by remember {
        mutableStateOf(List(timeSignatures[selectedSignature].numerator) { it == 0 })
    }
    var isPlaying by remember { mutableStateOf(false) }
    var activeBeatIndex by remember { mutableIntStateOf(-1) }

    val context = LocalContext.current
    val angle = remember { Animatable(0f) }
    val engine = remember { MetronomeEngine(context) }

    LaunchedEffect(selectedSignature) {
        accentedBeats = List(timeSignatures[selectedSignature].numerator) { it == 0 }
    }

    val currentBpm by rememberUpdatedState(bpm)
    val currentSignature by rememberUpdatedState(selectedSignature)
    val currentAccents by rememberUpdatedState(accentedBeats)

    // Self-correcting high-precision master clock
    LaunchedEffect(isPlaying) {
        if (!isPlaying) {
            activeBeatIndex = -1
            angle.animateTo(0f, tween(180, easing = FastOutSlowInEasing))
            return@LaunchedEffect
        }
        
        var beatIndex = 0
        var direction = if (angle.value <= 0f) 1f else -1f
        var nextBeatNano = System.nanoTime()
        
        while (isActive) {
            val bpmVal = currentBpm
            val sigIdx = currentSignature
            val beatsPerBar = timeSignatures[sigIdx].numerator
            
            // Calculate exact interval in nanoseconds to avoid rounding errors
            val intervalNano = 60_000_000_000L / bpmVal.coerceAtLeast(1)
            
            if (beatIndex == 0) nextBeatNano = System.nanoTime()

            val barIndex = beatIndex % beatsPerBar
            activeBeatIndex = barIndex
            
            val type = when {
                barIndex == 0 -> MetronomeEngine.ClickType.PRIMARY
                currentAccents.getOrElse(barIndex) { false } -> MetronomeEngine.ClickType.INTERMEDIATE
                else -> MetronomeEngine.ClickType.NORMAL
            }
            
            launch { engine.click(type) }
            
            // 2. Start animation
            val beatMs = (intervalNano / 1_000_000L).toInt()
            launch {
                angle.animateTo(
                    targetValue = MAX_SWING_DEGREES * direction,
                    animationSpec = tween(durationMillis = beatMs, easing = FastOutSlowInEasing)
                )
            }
            
            direction *= -1f
            beatIndex++
            
            // 3. Precision Wait (Self-Correction)
            nextBeatNano += intervalNano
            var sleepTimeNano = nextBeatNano - System.nanoTime()
            
            if (sleepTimeNano > 0) {
                // Sleep until the exact moment of the next beat
                delay(sleepTimeNano / 1_000_000L)
            } else {
                // System is lagging, don't sleep, try to catch up on the next one
                if (sleepTimeNano < -intervalNano) {
                    // Too far behind (ex: app minimized), reset clock
                    nextBeatNano = System.nanoTime()
                }
            }
        }
    }

    androidx.compose.runtime.DisposableEffect(Unit) {
        onDispose { engine.release() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
    ) {
        TopBar(
            isDarkMode = isDarkMode,
            onSettingsClick = onToggleDarkMode
        )

        Spacer(modifier = Modifier.height(28.dp)) // Distance between TopBar and BPM Section

        BpmAndTempoRow(
            bpm = bpm,
            tempoName = Tempo.nameFor(bpm)
        )

        Spacer(modifier = Modifier.height(8.dp)) // Distance inside BPM Section (keeps inside component smaller)

        BpmSlider(
            bpm = bpm,
            onBpmChange = { bpm = it }
        )

        Spacer(modifier = Modifier.height(20.dp)) // Distance between BPM Section and Time Signatures

        TimeSignatureRow(
            signatures = timeSignatures,
            selectedIndex = selectedSignature,
            onSelect = { selectedSignature = it }
        )

        Spacer(modifier = Modifier.height(24.dp)) // Distance between Signatures and Beat Indicators

        BeatIndicatorRow(
            beats = accentedBeats,
            activeBeatIndex = activeBeatIndex,
            onToggleAccent = { index ->
                if (index == 0) return@BeatIndicatorRow
                val newList = accentedBeats.toMutableList()
                newList[index] = !newList[index]
                accentedBeats = newList
            }
        )

        Spacer(modifier = Modifier.height(24.dp)) // Distance between Indicators and Pendulum

        Box(
            modifier = Modifier
                .weight(1f),
            contentAlignment = Alignment.TopCenter
        ) {
            PendulumView(
                bpm = bpm,
                isPlaying = isPlaying,
                angleDegrees = angle.value,
                activeBeatIndex = activeBeatIndex,
                onTogglePlay = { isPlaying = !isPlaying },
                onBpmChange = { bpm = it }
            )
        }
    }
}
