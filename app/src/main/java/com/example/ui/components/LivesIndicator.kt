package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LivesIndicator(
    livesRemaining: Int,
    maxLives: Int = 3,
    canReviveWithAd: Boolean = false,
    onWatchAdClicked: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "heart_pulse")
    val heartScale by if (livesRemaining == 1) {
        infiniteTransition.animateFloat(
            initialValue = 1.0f,
            targetValue = 1.25f,
            animationSpec = infiniteRepeatable(
                animation = tween(400),
                repeatMode = RepeatMode.Reverse
            ),
            label = "critical_heart_pulse"
        )
    } else {
        infiniteTransition.animateFloat(
            initialValue = 1.0f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(tween(1000)),
            label = "normal"
        )
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFEF4444).copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag("lives_indicator"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        for (i in 1..maxLives) {
            val hasLife = i <= livesRemaining
            Icon(
                imageVector = if (hasLife) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = if (hasLife) "Active life $i" else "Lost life $i",
                tint = if (hasLife) Color(0xFFEF4444) else Color(0xFF94A3B8),
                modifier = Modifier
                    .scale(if (hasLife && livesRemaining == 1) heartScale else 1.0f)
                    .testTag("heart_icon_$i")
            )
        }

        if (livesRemaining == 0 && canReviveWithAd && onWatchAdClicked != null) {
            Text(
                text = "+1 🎬",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF59E0B),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFFEF3C7))
                    .clickable { onWatchAdClicked() }
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                    .testTag("revive_ad_badge")
            )
        }
    }
}
