package com.fansim.app.platform

import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

actual class AdManager actual constructor() {

    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null
    private var interstitialLoading = false
    private val handler = Handler(Looper.getMainLooper())

    private val adUnits by lazy {
        val context = PlatformContext.appContext
        @Suppress("DEPRECATION")
        context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA).metaData
    }

    actual fun initialize() {
        MobileAds.initialize(PlatformContext.appContext) {
            handler.post {
                loadInterstitial()
                loadRewarded()
            }
        }
    }

    actual fun loadInterstitial() {
        if (interstitialLoading || interstitialAd != null) return
        val unitId = adUnits?.getString("com.fansim.app.ads.INTERSTITIAL_ID") ?: return
        interstitialLoading = true
        InterstitialAd.load(
            PlatformContext.appContext,
            unitId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialLoading = false
                    interstitialAd = ad
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialLoading = false
                    interstitialAd = null
                }
            }
        )
    }

    actual fun showInterstitial(onClosed: () -> Unit) {
        val ad = interstitialAd
        if (ad == null) {
            loadInterstitial()
            onClosed()
            return
        }
        showLoadedInterstitial(ad, onClosed)
    }

    private fun showLoadedInterstitial(ad: InterstitialAd, onClosed: () -> Unit) {
        val activity = PlatformContext.currentActivity ?: run {
            onClosed()
            return
        }
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
                loadInterstitial()
                onClosed()
            }
            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                interstitialAd = null
                loadInterstitial()
                onClosed()
            }
        }
        interstitialAd = null
        ad.show(activity)
    }

    actual fun loadRewarded() {
        val unitId = adUnits?.getString("com.fansim.app.ads.REWARDED_ID")?.takeIf { it.isNotBlank() } ?: return
        RewardedAd.load(
            PlatformContext.appContext,
            unitId,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewardedAd = null
                }
            }
        )
    }

    actual fun showRewarded(onReward: () -> Unit, onClosed: () -> Unit) {
        val activity = PlatformContext.currentActivity
        val ad = rewardedAd
        if (activity == null || ad == null) {
            onClosed()
            return
        }
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                rewardedAd = null
                loadRewarded()
                onClosed()
            }
            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                rewardedAd = null
                onClosed()
            }
        }
        ad.show(activity) { onReward() }
    }
}
