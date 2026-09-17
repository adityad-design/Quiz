package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TimerIndicator(
    secondsRemaining: Int,
    totalSeconds: Int = 60,
    modifier: Modifier = Modifier
) {
    val progress = (secondsRemaining.toFloat() / totalSeconds).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "timer_progress"
    )

    val timerColor by animateColorAsState(
        targetValue = when {
            secondsRemaining > 30 -> Color(0xFF10B981) // Emerald Green
            secondsRemaining > 10 -> Color(0xFFF59E0B) // Amber Warning
            else -> Color(0xFFEF4444)                  // Crimson Urgent
        },
        label = "timer_color"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by if (secondsRemaining in 1..10) {
        infiniteTransition.animateFloat(
            initialValue = 1.0f,
            targetValue = 1.15f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 500),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse_scale"
        )
    } else {
        animateFloatAsState(targetValue = 1.0f, label = "static_scale")
    }

    Box(
        modifier = modifier
            .size(56.dp)
            .scale(pulseScale)
            .testTag("timer_indicator"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(56.dp)) {
            // Background track
            drawCircle(
                color = timerColor.copy(alpha = 0.15f),
                style = Stroke(width = 5.dp.toPx())
            )
            // Progress arc
            drawArc(
                color = timerColor,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        Text(
            text = "$secondsRemaining",
            fontSize = if (secondsRemaining >= 10) 16.sp else 18.sp,
            fontWeight = FontWeight.Bold,
            color = timerColor
        )
    }
}
