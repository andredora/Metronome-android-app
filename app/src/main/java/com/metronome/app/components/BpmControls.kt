package com.metronome.app.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.metronome.app.MAX_BPM
import com.metronome.app.MIN_BPM
import com.metronome.app.Tempo
import com.metronome.app.ui.theme.LocalMetronomeColors
import com.metronome.app.ui.theme.SerifFamily

@Composable
fun BpmAndTempoRow(bpm: Int, tempoName: String) {
    val colors = LocalMetronomeColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = bpm.toString(),
            color = colors.textPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "BPM",
            color = colors.textSecondary,
            fontSize = 15.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = tempoName,
            color = colors.textPrimary,
            fontSize = 24.sp,
            fontFamily = SerifFamily
        )
    }
}

@Composable
fun BpmSlider(bpm: Int, onBpmChange: (Int) -> Unit) {
    val colors = LocalMetronomeColors.current
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
        Slider(
            value = bpm.toFloat(),
            onValueChange = { newValue ->
                onBpmChange(newValue.toInt())
            },
            valueRange = MIN_BPM.toFloat()..MAX_BPM.toFloat(),
            colors = SliderDefaults.colors(
                thumbColor = colors.accent,
                activeTrackColor = colors.accent,
                inactiveTrackColor = colors.surface
            )
        )
    }
}
