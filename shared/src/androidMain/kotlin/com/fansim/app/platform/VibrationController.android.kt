package com.fansim.app.platform

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

actual class VibrationController actual constructor() {

    private val vibrator: Vibrator by lazy {
        val context = PlatformContext.appContext
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    actual fun vibrateTick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(90, 180))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(90)
        }
    }

    actual fun vibrateContinuous(intensity: Float) {
        val amplitude = (intensity.coerceIn(0f, 1f) * 255).toInt().coerceIn(180, 255)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(250, amplitude))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(250)
        }
    }

    actual fun stop() {
        vibrator.cancel()
    }
}
