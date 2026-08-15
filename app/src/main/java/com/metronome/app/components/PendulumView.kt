package com.metronome.app.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.metronome.app.BPM_STEPS
import com.metronome.app.MAX_BPM
import com.metronome.app.MIN_BPM
import kotlin.math.roundToInt

@Composable
fun PendulumView(
    bpm: Int,
    isPlaying: Boolean,
    angleDegrees: Float,
    activeBeatIndex: Int,
    onTogglePlay: () -> Unit,
    onBpmChange: (Int) -> Unit
) {
    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(activeBeatIndex) {
        if (activeBeatIndex != -1) {
            // Quick shake effect
            shakeOffset.animateTo(2f, tween(30, easing = FastOutLinearInEasing))
            shakeOffset.animateTo(-2f, tween(30, easing = FastOutLinearInEasing))
            shakeOffset.animateTo(0f, tween(30, easing = FastOutLinearInEasing))
        }
    }

    // Button position in SVG coordinates
    val buttonCenter = Offset(152f, 560f)
    val buttonRadius = 25f

    // Constants for mapping BPM to weight Y position
    // Rod Y range in SVG coordinates: 8.4 (top) to 443.375 (bottom/pivot)
    val minWeightY = 40f  // Position for MIN_BPM (40)
    val maxWeightY = 400f // Position for MAX_BPM (208)

    fun getWeightY(currentBpm: Int): Float {
        val fraction = (currentBpm - MIN_BPM).toFloat() / (MAX_BPM - MIN_BPM)
        return minWeightY + fraction * (maxWeightY - minWeightY)
    }

    val currentWeightY = getWeightY(bpm)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .pointerInput(isPlaying) {
                detectDragGestures(
                    onDrag = { change, _ ->
                        if (!isPlaying) {
                            change.consume()

                            val w = size.width.toFloat()
                            val h = size.height.toFloat()
                            val svgW = 304f
                            val svgH = 622f
                            val scale = minOf(w / svgW, h / svgH)
                            val offsetY = (h - svgH * scale) / 2f

                            // The rod's top is offset by 'dy' in the SVG coordinates
                            val dy = 480f - 443.375f 
                            val touchYInSvg = (change.position.y - offsetY) / scale
                            val touchYRelative = touchYInSvg - dy

                            // Map touchYRelative to BPM value (linear)
                            val fraction = ((touchYRelative - minWeightY) / (maxWeightY - minWeightY)).coerceIn(0f, 1f)
                            val targetBpm = MIN_BPM + (fraction * (MAX_BPM - MIN_BPM)).roundToInt()

                            // Snap to closest BPM step
                            val closestBpm = BPM_STEPS.minByOrNull { kotlin.math.abs(it - targetBpm) } ?: targetBpm
                            
                            if (closestBpm != bpm) {
                                onBpmChange(closestBpm)
                            }
                        }
                    }
                )
            }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val w = size.width.toFloat()
                    val h = size.height.toFloat()
                    val svgW = 304f
                    val svgH = 622f
                    val scale = minOf(w / svgW, h / svgH)
                    val offsetX = (w - svgW * scale) / 2f
                    val offsetY = (h - svgH * scale) / 2f

                    val tapInSvg = Offset(
                        (offset.x - offsetX) / scale,
                        (offset.y - offsetY) / scale
                    )

                    // Check distance to button center
                    val dist = (tapInSvg - buttonCenter).getDistance()
                    if (dist <= buttonRadius * 1.5f) { // Slightly larger hit area for better UX
                        onTogglePlay()
                    }
                }
            },
        contentAlignment = Alignment.TopCenter
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            val svgW = 304f
            val svgH = 622f
            val scale = minOf(w / svgW, h / svgH)

            withTransform({
                translate((w - svgW * scale) / 2f, (h - svgH * scale) / 2f)
                scale(scale, scale, pivot = Offset.Zero)
            }) {
                // Outer body
                val bodyPath = Path().apply {
                    moveTo(288.504f, 622f)
                    lineTo(15.8731f, 622f)
                    cubicTo(7.44715f, 622f, 0.937534f, 614.55f, 1.9946f, 606.191f)
                    cubicTo(29.1156f, 391.716f, 48.2908f, 260.781f, 90.2161f, 52.6592f)
                    cubicTo(95.8548f, 24.6681f, 117.559f, 1.2499f, 146.091f, 0.142743f)
                    cubicTo(151.358f, -0.0616403f, 156.48f, -0.0433985f, 161.601f, 0.177287f)
                    cubicTo(190.578f, 1.42611f, 212.898f, 25.0966f, 218.705f, 53.5137f)
                    cubicTo(260.867f, 259.863f, 280.223f, 390.957f, 302.416f, 606.521f)
                    cubicTo(303.265f, 614.769f, 296.796f, 622f, 288.504f, 622f)
                    close()
                }
                drawPath(bodyPath, color = Color(0xFF504B4B))

                // Play/Pause Button
                drawCircle(
                    color = Color(0xFF333030),
                    radius = buttonRadius,
                    center = buttonCenter
                )

                if (isPlaying) {
                    // Pause Icon: Two rounded rectangles
                    val barW = 4.5f
                    val barH = 16f
                    val spacing = 5f
                    val corner = CornerRadius(2.25f)
                    drawRoundRect(
                        color = Color.White,
                        topLeft = Offset(buttonCenter.x - spacing / 2f - barW, buttonCenter.y - barH / 2f),
                        size = Size(barW, barH),
                        cornerRadius = corner
                    )
                    drawRoundRect(
                        color = Color.White,
                        topLeft = Offset(buttonCenter.x + spacing / 2f, buttonCenter.y - barH / 2f),
                        size = Size(barW, barH),
                        cornerRadius = corner
                    )
                } else {
                    // Play Icon: Rounded Triangle
                    // To make a triangle rounded, we use a Path with a CornerPathEffect or similar, 
                    // but in Compose DrawScope we can draw a path and use a Stroke with Round Join, 
                    // or just use a Path with rounded corners. For a small icon, a high-quality 
                    // rounded triangle path is best.
                    val triSize = 12f
                    val triPath = Path().apply {
                        val x = buttonCenter.x
                        val y = buttonCenter.y
                        // Simple rounded triangle approach: use a very thick rounded stroke or specific coordinates
                        // Here we define a slightly "softer" triangle
                        moveTo(x - triSize * 0.35f, y - triSize * 0.55f)
                        lineTo(x + triSize * 0.45f, y)
                        lineTo(x - triSize * 0.35f, y + triSize * 0.55f)
                        close()
                    }
                    // Draw with round join and round cap to simulate rounded corners
                    drawPath(
                        path = triPath,
                        color = Color.White,
                        style = Stroke(
                            width = 3f,
                            cap = androidx.compose.ui.graphics.StrokeCap.Round,
                            join = androidx.compose.ui.graphics.StrokeJoin.Round
                        )
                    )
                    drawPath(
                        path = triPath,
                        color = Color.White,
                        style = Fill
                    )
                }

                // Inner track
                val trackPath = Path().apply {
                    moveTo(221.398f, 456.642f)
                    lineTo(193.534f, 59.3367f)
                    cubicTo(192.059f, 38.3067f, 174.569f, 22f, 153.487f, 22f)
                    cubicTo(132.507f, 22f, 115.065f, 38.1545f, 113.459f, 59.0728f)
                    lineTo(82.9664f, 456.326f)
                    cubicTo(80.8267f, 484.202f, 102.868f, 508f, 130.826f, 508f)
                    lineTo(173.516f, 508f)
                    cubicTo(201.348f, 508f, 223.345f, 484.406f, 221.398f, 456.642f)
                    close()
                }
                drawPath(trackPath, color = Color(0xFF333030))

                // Pendulum pivot point in SVG coordinates
                val pivot = Offset(152f, 480f)
                
                rotate(degrees = angleDegrees, pivot = pivot) {
                    // Rod
                    val rodW = 9.37447f
                    val rodH = 434.975f
                    val rodX = 29.8469f
                    val rodY = 8.40002f
                    
                    val dx = 152f - 34.5f
                    val dy = 480f - 443.375f

                    translate(dx, dy) {
                        drawRoundRect(
                            color = Color.White,
                            topLeft = Offset(rodX, rodY),
                            size = Size(rodW, rodH),
                            cornerRadius = CornerRadius(4.68723f)
                        )

                        // Weight movement
                        // Original weight center was ~350
                        // new center is currentWeightY
                        val weightOffset = currentWeightY - 350f

                        translate(0f, weightOffset) {
                            // Weight
                            val weightPath = Path().apply {
                                moveTo(56.8975f, 357.396f)
                                lineTo(56.8975f, 342.397f)
                                cubicTo(56.8975f, 329.971f, 46.8245f, 319.898f, 34.3988f, 319.898f)
                                cubicTo(21.973f, 319.898f, 11.9f, 329.971f, 11.9f, 342.397f)
                                lineTo(11.9f, 357.396f)
                                cubicTo(11.9f, 369.822f, 21.973f, 379.895f, 34.3988f, 379.895f)
                                cubicTo(46.8245f, 379.895f, 56.8975f, 369.822f, 56.8975f, 357.396f)
                                close()
                            }
                            drawPath(weightPath, color = Color(0xFFE1E1E1))
                            drawPath(weightPath, color = Color.White, style = Stroke(width = 1f))

                            // Lines on weight
                            drawLine(Color(0xFF6A6A6A), Offset(11.9581f, 338.647f), Offset(56.8461f, 338.647f), strokeWidth = 1f)
                            drawLine(Color(0xFF6A6A6A), Offset(57.1149f, 345.209f), Offset(11.6893f, 345.209f), strokeWidth = 1f)
                            drawLine(Color(0xFF6A6A6A), Offset(11.6893f, 351.772f), Offset(57.1149f, 351.772f), strokeWidth = 1f)
                        }
                    }
                }
            }
        }
    }
}
