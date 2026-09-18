package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.service.AdMobManager
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

@Composable
fun AdMobBanner(
    modifier: Modifier = Modifier,
    adUnitId: String = AdMobManager.TEST_BANNER_ID
) {
    val isInspection = LocalInspectionMode.current
    var hasError by remember { mutableStateOf(false) }

    if (isInspection || hasError) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFE2E8F0))
                .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                .testTag("admob_banner_placeholder"),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isInspection) "AdMob Banner Preview" else "Sponsored Trivia Content",
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
        }
    } else {
        var adViewInstance by remember { mutableStateOf<AdView?>(null) }

        DisposableEffect(Unit) {
            onDispose {
                try {
                    adViewInstance?.destroy()
                } catch (e: Throwable) {
                    // Safe cleanup
                }
            }
        }

        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .testTag("admob_banner_container"),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admob_banner_view"),
                factory = { context ->
                    try {
                        AdView(context).apply {
                            setAdSize(AdSize.BANNER)
                            setAdUnitId(adUnitId)
                            adListener = object : AdListener() {
                                override fun onAdFailedToLoad(error: LoadAdError) {
                                    // Graceful handling for offline
                                }
                            }
                            loadAd(AdRequest.Builder().build())
                            adViewInstance = this
                        }
                    } catch (t: Throwable) {
                        hasError = true
                        android.view.View(context)
                    }
                }
            )
        }
    }
}
