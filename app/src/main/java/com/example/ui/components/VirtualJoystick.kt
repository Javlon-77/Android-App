package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun VirtualJoystick(
    modifier: Modifier = Modifier,
    sizeDp: Float = 140f,
    onMove: (dx: Float, dy: Float) -> Unit
) {
    var thumbOffset by remember { mutableStateOf(Offset.Zero) }
    val maxRadiusPx = (sizeDp * 1.5f) // Limit radius in pixels

    Box(
        modifier = modifier
            .size(sizeDp.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val diff = offset - center
                        val dist = diff.getDistance()
                        val maxR = size.width / 2f * 0.75f
                        val clamped = if (dist > maxR) {
                            val angle = atan2(diff.y, diff.x)
                            Offset(cos(angle) * maxR, sin(angle) * maxR)
                        } else {
                            diff
                        }
                        thumbOffset = clamped
                        onMove(clamped.x / maxR, clamped.y / maxR)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val newOffset = thumbOffset + dragAmount
                        val dist = newOffset.getDistance()
                        val maxR = size.width / 2f * 0.75f
                        val clamped = if (dist > maxR) {
                            val angle = atan2(newOffset.y, newOffset.x)
                            Offset(cos(angle) * maxR, sin(angle) * maxR)
                        } else {
                            newOffset
                        }
                        thumbOffset = clamped
                        onMove(clamped.x / maxR, clamped.y / maxR)
                    },
                    onDragEnd = {
                        thumbOffset = Offset.Zero
                        onMove(0f, 0f)
                    },
                    onDragCancel = {
                        thumbOffset = Offset.Zero
                        onMove(0f, 0f)
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val outerRadius = size.width / 2f * 0.85f
            val innerRadius = outerRadius * 0.42f

            // Outer base circle
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x660F172A), Color(0x991E293B)),
                    center = center,
                    radius = outerRadius
                ),
                radius = outerRadius,
                center = center
            )
            drawCircle(
                color = Color(0x8838BDF8),
                radius = outerRadius,
                center = center,
                style = Stroke(width = 2.5f)
            )

            // Inner thumb stick
            val knobCenter = center + thumbOffset
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF38BDF8), Color(0xFF0284C7)),
                    center = knobCenter,
                    radius = innerRadius
                ),
                radius = innerRadius,
                center = knobCenter
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.85f),
                radius = innerRadius * 0.35f,
                center = knobCenter
            )
        }
    }
}
