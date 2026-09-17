package com.fansim.app.platform

/**
 * Wraps Google Mobile Ads (AdMob). Interstitials are shown right after the user
 * finishes first-launch setup (naming + coloring their fan); rewarded video ads
 * are the app's monetisation hook (e.g. "watch a video to unlock a new fan color").
 */
expect class AdManager() {
    fun initialize()
    fun loadInterstitial()
    fun showInterstitial(onClosed: () -> Unit)
    fun loadRewarded()
    fun showRewarded(onReward: () -> Unit, onClosed: () -> Unit)
}
