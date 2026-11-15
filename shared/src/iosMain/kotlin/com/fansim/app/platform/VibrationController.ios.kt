package com.fansim.app.platform

import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle
import platform.UIKit.UINotificationFeedbackGenerator
import platform.UIKit.UINotificationFeedbackType

actual class VibrationController actual constructor() {

    // iOS has no continuous-amplitude vibration API for third-party apps; we
    // approximate "stronger vibration when near the face" by choosing a heavier
    // UIImpactFeedbackGenerator style as intensity increases.
    actual fun vibrateTick() {
        UINotificationFeedbackGenerator().notificationOccurred(UINotificationFeedbackType.UINotificationFeedbackTypeWarning)
    }

    actual fun vibrateContinuous(intensity: Float) {
        val style = when {
            intensity > 0.66f -> UIImpactFeedbackStyle.UIImpactFeedbackStyleHeavy
            intensity > 0.33f -> UIImpactFeedbackStyle.UIImpactFeedbackStyleMedium
            else -> UIImpactFeedbackStyle.UIImpactFeedbackStyleLight
        }
        UIImpactFeedbackGenerator(style).impactOccurred()
    }

    actual fun stop() {
        // No-op: iOS haptic taps are fire-and-forget, nothing to cancel.
    }
}
