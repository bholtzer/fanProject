package com.fansim.app.platform

import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.UIKit.UIDevice
import platform.UIKit.UIDeviceProximityStateDidChangeNotification

actual class ProximitySensorController actual constructor() {

    private var observerToken: Any? = null

    actual fun start(onProximityChanged: (isNear: Boolean) -> Unit) {
        val device = UIDevice.currentDevice
        device.proximityMonitoringEnabled = true
        observerToken = NSNotificationCenter.defaultCenter.addObserverForName(
            name = UIDeviceProximityStateDidChangeNotification,
            `object` = device,
            queue = NSOperationQueue.mainQueue
        ) {
            onProximityChanged(device.proximityState)
        }
    }

    actual fun stop() {
        observerToken?.let { NSNotificationCenter.defaultCenter.removeObserver(it) }
        observerToken = null
        UIDevice.currentDevice.proximityMonitoringEnabled = false
    }
}
