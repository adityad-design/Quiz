package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import kotlin.random.Random

private data class ConfettiParticle(
    val xRatio: Float,
    val speed: Float,
    val size: Float,
    val color: Color,
    val oscillationSpeed: Float,
    val initialDelay: Float
)

@Composable
fun ConfettiEffect(
    modifier: Modifier = Modifier,
    particleCount: Int = 60
) {
    val colors = listOf(
        Color(0xFFFFD700), // Gold
        Color(0xFFFF4081), // Pink
        Color(0xFF00E5FF), // Cyan
        Color(0xFF76FF03), // Lime
        Color(0xFFFF9100), // Orange
        Color(0xFF7C4DFF), // Purple
        Color(0xFF00E676)  // Green
    )

    val particles = remember {
        val rng = Random(42)
        List(particleCount) {
            ConfettiParticle(
                xRatio = rng.nextFloat(),
                speed = rng.nextFloat() * 0.7f + 0.4f,
                size = rng.nextFloat() * 10f + 8f,
                color = colors[rng.nextInt(colors.size)],
                oscillationSpeed = rng.nextFloat() * 4f + 2f,
                initialDelay = rng.nextFloat() * 0.3f
            )
        }
    }

    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 3500, easing = LinearEasing)
        )
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .testTag("confetti_canvas")
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val t = progress.value

        for (p in particles) {
            val adjustedT = ((t - p.initialDelay) * p.speed).coerceIn(0f, 1.2f)
            if (adjustedT <= 0f) continue

            val y = adjustedT * (canvasHeight + 100f) - 50f
            val xOffset = kotlin.math.sin((adjustedT * p.oscillationSpeed * Math.PI).toFloat()) * 30f
            val x = (p.xRatio * canvasWidth) + xOffset

            val alpha = if (adjustedT > 0.8f) (1.2f - adjustedT) / 0.4f else 1f

            drawRect(
                color = p.color.copy(alpha = alpha.coerceIn(0f, 1f)),
                topLeft = Offset(x, y),
                size = Size(p.size, p.size * 0.6f)
            )
        }
    }
}
