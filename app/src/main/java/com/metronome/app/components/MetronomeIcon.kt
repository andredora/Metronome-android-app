package com.metronome.app.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate

@Composable
fun MetronomeIcon(
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    pendulumAngle: Float = 0f
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        
        val strokeWidth = w * 0.018f
        
        // Body (Static)
        val outerPath = Path().apply {
            moveTo(w * 0.32f, h * 0.88f)
            lineTo(w * 0.68f, h * 0.88f)
            quadraticBezierTo(w * 0.72f, h * 0.88f, w * 0.71f, h * 0.84f)
            lineTo(w * 0.56f, h * 0.14f)
            quadraticBezierTo(w * 0.5f, h * 0.08f, w * 0.44f, h * 0.14f)
            lineTo(w * 0.29f, h * 0.84f)
            quadraticBezierTo(w * 0.28f, h * 0.88f, w * 0.32f, h * 0.88f)
            close()
        }
        
        drawPath(
            path = outerPath,
            color = color,
            style = Stroke(width = strokeWidth)
        )
        
        val innerPath = Path().apply {
            moveTo(w * 0.41f, h * 0.8f)
            lineTo(w * 0.59f, h * 0.8f)
            lineTo(w * 0.53f, h * 0.25f)
            quadraticBezierTo(w * 0.5f, h * 0.2f, w * 0.47f, h * 0.25f)
            close()
        }
        
        drawPath(
            path = innerPath,
            color = color,
            style = Stroke(width = strokeWidth)
        )

        // Pendulum (Animated)
        // Pivot point at the bottom of the track: w * 0.5f, h * 0.8f
        rotate(
            degrees = pendulumAngle,
            pivot = Offset(w * 0.5f, h * 0.8f)
        ) {
            drawLine(
                color = color,
                start = Offset(w * 0.5f, h * 0.25f),
                end = Offset(w * 0.5f, h * 0.8f),
                strokeWidth = strokeWidth * 0.6f
            )
            
            val weightW = w * 0.06f
            val weightH = h * 0.045f
            drawRoundRect(
                color = color,
                topLeft = Offset(w * 0.5f - weightW / 2, h * 0.62f - weightH / 2),
                size = Size(weightW, weightH),
                cornerRadius = CornerRadius(weightW / 2, weightW / 2)
            )
        }
    }
}
