package com.example.service

import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.data.local.QuizPreferences
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class AdMobManager(private val context: Context, private val preferences: QuizPreferences) {

    companion object {
        private const val TAG = "AdMobManager"

        // Official Google AdMob Test Ad Unit IDs
        const val TEST_BANNER_ID = "ca-app-pub-3940256099942544/6300978111"
        const val TEST_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712"
        const val TEST_REWARDED_ID = "ca-app-pub-3940256099942544/5224354917"
    }

    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null
    private var isInitialized = false

    val bannerAdUnitId: String
        get() = TEST_BANNER_ID

    val interstitialAdUnitId: String
        get() = TEST_INTERSTITIAL_ID

    val rewardedAdUnitId: String
        get() = TEST_REWARDED_ID

    fun initialize() {
        if (isInitialized) return
        try {
            MobileAds.initialize(context) { status ->
                Log.d(TAG, "MobileAds initialized: $status")
                isInitialized = true
                loadInterstitialAd()
                loadRewardedAd()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing MobileAds", e)
        }
    }

    fun loadInterstitialAd() {
        try {
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(
                context,
                interstitialAdUnitId,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        interstitialAd = ad
                        Log.d(TAG, "Interstitial ad loaded successfully")
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        interstitialAd = null
                        Log.w(TAG, "Interstitial ad failed to load: ${error.message}")
                    }
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error loading interstitial ad", e)
        }
    }

    fun showInterstitial(activity: Activity, onDismissed: () -> Unit) {
        val ad = interstitialAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    loadInterstitialAd()
                    onDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    interstitialAd = null
                    loadInterstitialAd()
                    onDismissed()
                }
            }
            ad.show(activity)
        } else {
            // Not loaded or offline: proceed smoothly without interrupting user experience
            loadInterstitialAd()
            onDismissed()
        }
    }

    fun loadRewardedAd() {
        try {
            val adRequest = AdRequest.Builder().build()
            RewardedAd.load(
                context,
                rewardedAdUnitId,
                adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdLoaded(ad: RewardedAd) {
                        rewardedAd = ad
                        Log.d(TAG, "Rewarded ad loaded successfully")
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        rewardedAd = null
                        Log.w(TAG, "Rewarded ad failed to load: ${error.message}")
                    }
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error loading rewarded ad", e)
        }
    }

    fun showRewardedAd(
        activity: Activity,
        onRewardEarned: () -> Unit,
        onFailed: (isOfflineFallback: Boolean) -> Unit
    ) {
        val ad = rewardedAd
        if (ad != null) {
            var rewardGranted = false
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedAd = null
                    loadRewardedAd()
                    if (rewardGranted) {
                        onRewardEarned()
                    }
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    rewardedAd = null
                    loadRewardedAd()
                    onFailed(false)
                }
            }
            ad.show(activity) { _ ->
                rewardGranted = true
            }
        } else {
            // Offline or ad not ready: allow offline fallback reward so users can continue
            loadRewardedAd()
            onFailed(true)
        }
    }

    val isRewardedAdReady: Boolean
        get() = rewardedAd != null
}
