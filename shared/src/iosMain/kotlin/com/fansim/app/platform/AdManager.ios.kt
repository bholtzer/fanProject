package com.fansim.app.platform

/**
 * iOS AdMob integration.
 *
 * The Google-Mobile-Ads-SDK for iOS is an Objective-C CocoaPod. Two supported ways to wire
 * it up from Kotlin/Native:
 *   1) Add the pod via the Kotlin CocoaPods Gradle plugin and call it directly through
 *      generated Kotlin/Native cinterop bindings (GADInterstitialAd, GADRewardedAd, etc.).
 *   2) Implement a small native Swift wrapper in iosApp exposing a simple
 *      Objective-C-friendly protocol, and call it from here via the shared framework's
 *      Objective-C interop.
 *
 * This stub keeps the same expect/actual contract as Android so the presentation layer
 * never needs to know which path you choose. Fill in the TODOs with your chosen approach;
 * see the project README for step-by-step setup notes and test ad unit IDs.
 */
actual class AdManager actual constructor() {

    actual fun initialize() {
        // TODO: GADMobileAds.sharedInstance().startWithCompletionHandler { }
    }

    actual fun loadInterstitial() {
        // TODO: load a GADInterstitialAd with your ad unit ID
    }

    actual fun showInterstitial(onClosed: () -> Unit) {
        // TODO: present the loaded interstitial from the current UIViewController,
        // then call onClosed() from the dismissal delegate callback.
        onClosed()
    }

    actual fun loadRewarded() {
        // TODO: load a GADRewardedAd with your ad unit ID
    }

    actual fun showRewarded(onReward: () -> Unit, onClosed: () -> Unit) {
        // TODO: present the rewarded ad; call onReward() from the reward callback
        // and onClosed() from the dismissal delegate callback.
        onClosed()
    }
}
