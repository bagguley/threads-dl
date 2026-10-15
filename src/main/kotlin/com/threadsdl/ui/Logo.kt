package com.threadsdl.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun ThreadsLogo(
    modifier: Modifier = Modifier,
    size: Int = 48
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val gradient = Brush.linearGradient(
        colors = listOf(primaryColor, secondaryColor)
    )

    Canvas(modifier = modifier.size(size.dp)) {
        val centerX = this.size.width / 2
        val centerY = this.size.height / 2
        val radius = this.size.width / 3
        val strokeWidth = 3.5.dp.toPx()

        // Draw stylized "@" symbol - cleaner design
        // Outer circle (main ring)
        drawCircle(
            brush = gradient,
            radius = radius * 1.1f,
            center = Offset(centerX, centerY),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Inner curved tail creating "@" effect
        val tailPath = Path().apply {
            // Start from inside circle, curve down and around
            val startAngle = -45f
            val endAngle = 270f

            // Inner circle arc
            arcTo(
                rect = androidx.compose.ui.geometry.Rect(
                    left = centerX - radius * 0.5f,
                    top = centerY - radius * 0.5f,
                    right = centerX + radius * 0.5f,
                    bottom = centerY + radius * 0.5f
                ),
                startAngleDegrees = startAngle,
                sweepAngleDegrees = endAngle - startAngle,
                forceMoveTo = false
            )

            // Tail extending outward
            lineTo(centerX + radius * 0.8f, centerY - radius * 0.5f)
        }

        drawPath(
            path = tailPath,
            brush = gradient,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Central dot for "@" symbol
        drawCircle(
            brush = gradient,
            radius = radius * 0.18f,
            center = Offset(centerX, centerY)
        )
    }
}
