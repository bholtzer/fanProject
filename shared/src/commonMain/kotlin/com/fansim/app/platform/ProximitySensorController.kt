package com.fansim.app.platform

/**
 * Reports proximity or pickup movement on Android and proximity on iOS.
 * This drives louder sound and stronger vibration.
 */
expect class ProximitySensorController() {
    fun start(onProximityChanged: (isNear: Boolean) -> Unit)
    fun stop()
}
