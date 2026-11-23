package com.fansim.app.platform

/**
 * Implemented by the native iOS host, keeping Google Mobile Ads out of common code.
 * Install the bridge before creating MainViewController. All calls run on the UI thread.
 */
interface IosAdsBridge {
    fun initialize()
    fun loadInterstitial()
    fun showInterstitial(onClosed: () -> Unit)
    fun loadRewarded()
    fun showRewarded(onReward: () -> Unit, onClosed: () -> Unit)
}

object IosAds {
    private var installedBridge: IosAdsBridge? = null

    fun install(bridge: IosAdsBridge) {
        check(installedBridge == null) { "Install the iOS ads bridge only once, before creating the UI." }
        installedBridge = bridge
    }

    internal fun requireBridge(): IosAdsBridge =
        checkNotNull(installedBridge) { "Install IosAdsBridge before creating MainViewController." }
}

actual class AdManager actual constructor() {
    private val bridge = IosAds.requireBridge()

    actual fun initialize() = bridge.initialize()
    actual fun loadInterstitial() = bridge.loadInterstitial()
    actual fun showInterstitial(onClosed: () -> Unit) = bridge.showInterstitial(onClosed)
    actual fun loadRewarded() = bridge.loadRewarded()
    actual fun showRewarded(onReward: () -> Unit, onClosed: () -> Unit) =
        bridge.showRewarded(onReward, onClosed)
}
