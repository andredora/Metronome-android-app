package com.metronome.app.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.metronome.app.models.TimeSignature
import com.metronome.app.ui.theme.LocalMetronomeColors
import com.metronome.app.ui.theme.SerifFamily

@Composable
fun TimeSignatureRow(
    signatures: List<TimeSignature>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    val colors = LocalMetronomeColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        signatures.forEachIndexed { index, sig ->
            val selected = index == selectedIndex
            val bg = if (selected) colors.pillSelected else colors.pillUnselected
            val textColor = colors.pillTextUnselected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(bg)
                    .clickable { onSelect(index) },
                contentAlignment = Alignment.Center
            ) {
                FractionText(sig.numerator, sig.denominator, textColor)
            }
        }
    }
}

@Composable
fun BeatIndicatorRow(
    beats: List<Boolean>,
    activeBeatIndex: Int,
    onToggleAccent: (Int) -> Unit
) {
    val colors = LocalMetronomeColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        beats.forEachIndexed { index, isAccented ->
            val isFirst = index == 0
            val isActive = index == activeBeatIndex
            
            // Animation for size increase using scale to avoid layout shifts
            val scale by animateFloatAsState(
                targetValue = if (isActive) 1.3f else 1f,
                animationSpec = tween(durationMillis = 100, easing = FastOutSlowInEasing),
                label = "DotScale"
            )

            Box(
                modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .size(20.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .clip(CircleShape)
                    .background(if (isFirst) colors.accent else Color.Transparent)
                    .border(
                        width = if (isFirst) 3.dp else 2.dp,
                        color = colors.accent,
                        shape = CircleShape
                    )
                    .then(
                        if (isFirst) Modifier else Modifier.clickable { onToggleAccent(index) }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isAccented && !isFirst) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(colors.accent)
                    )
                }
            }
        }
    }
}

@Composable
private fun FractionText(numerator: Int, denominator: Int, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = numerator.toString(),
            color = color,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = SerifFamily,
            modifier = Modifier.padding(bottom = 7.dp)
        )
        Text(
            text = "/",
            color = color,
            fontSize = 19.sp,
            fontWeight = FontWeight.Light,
        )
        Text(
            text = denominator.toString(),
            color = color,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = SerifFamily,
            modifier = Modifier.padding(top = 7.dp)
        )
    }
}
