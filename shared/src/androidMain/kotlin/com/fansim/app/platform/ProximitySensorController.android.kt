package com.fansim.app.platform

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.Looper
import kotlin.math.sqrt

actual class ProximitySensorController actual constructor() {

    private var proximityListener: SensorEventListener? = null
    private var motionListener: SensorEventListener? = null
    private val handler = Handler(Looper.getMainLooper())
    private var near = false
    private var pickedUp = false
    private var resetPickup: Runnable? = null
    private val sensorManager: SensorManager by lazy {
        PlatformContext.appContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    actual fun start(onProximityChanged: (isNear: Boolean) -> Unit) {
        stop()
        sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)?.let { proximitySensor ->
            proximityListener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    val distance = event.values.getOrNull(0) ?: proximitySensor.maximumRange
                    val newNear = distance < proximitySensor.maximumRange
                    if (newNear != near) {
                        near = newNear
                        onProximityChanged(near || pickedUp)
                    }
                }
                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
            }
            sensorManager.registerListener(proximityListener, proximitySensor, SensorManager.SENSOR_DELAY_UI)
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)?.let { motionSensor ->
            motionListener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    val values = event.values
                    if (values.size < 3) return
                    val movement = sqrt(values[0] * values[0] + values[1] * values[1] + values[2] * values[2])
                    if (movement < 1.2f) return
                    if (!pickedUp) {
                        pickedUp = true
                        onProximityChanged(true)
                    }
                    resetPickup?.let(handler::removeCallbacks)
                    resetPickup = Runnable {
                        pickedUp = false
                        onProximityChanged(near)
                    }.also { handler.postDelayed(it, 2500L) }
                }
                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
            }
            sensorManager.registerListener(motionListener, motionSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    actual fun stop() {
        proximityListener?.let(sensorManager::unregisterListener)
        motionListener?.let(sensorManager::unregisterListener)
        resetPickup?.let(handler::removeCallbacks)
        proximityListener = null
        motionListener = null
        resetPickup = null
        near = false
        pickedUp = false
    }
}
