package com.fansim.app.platform

/**
 * Cross-platform haptics.
 * - vibrateTick: short, light pulse (used for the "swipe reached its limit" feedback)
 * - vibrateContinuous(intensity): sustained vibration while the fan spins and the phone
 *   is held near the user's face (intensity 0f..1f, driven by proximity).
 * - stop: cancels any ongoing continuous vibration.
 */
expect class VibrationController() {
    fun vibrateTick()
    fun vibrateContinuous(intensity: Float)
    fun stop()
}
