package com.fansim.app.platform

/**
 * Wraps Google Mobile Ads (AdMob). Call from the UI thread.
 * A preloaded interstitial may be shown when changing fans. If no ad can be
 * presented, onClosed is called immediately so navigation can continue.
 * Rewarded ads call onReward only when the SDK reports an earned reward;
 * onClosed reports dismissal, unavailable ads, or presentation failure.
 * The iOS host must install IosAdsBridge before creating the shared UI.
 */
expect class AdManager() {
    fun initialize()
    fun loadInterstitial()
    fun showInterstitial(onClosed: () -> Unit)
    fun loadRewarded()
    fun showRewarded(onReward: () -> Unit, onClosed: () -> Unit)
}
