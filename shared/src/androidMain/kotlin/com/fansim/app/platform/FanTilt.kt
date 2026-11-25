package com.fansim.app.platform

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.math.asin

@Composable
internal actual fun rememberFanTilt(): State<Offset> {
    val view = LocalView.current
    val owner = PlatformContext.currentActivity as? LifecycleOwner
    val tilt = remember { mutableStateOf(Offset.Zero) }
    DisposableEffect(view, owner) {
        val manager = view.context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor = manager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR)
            ?: manager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        val matrix = FloatArray(9)
        val screenMatrix = FloatArray(9)
        val baseline = FloatArray(9)
        var baselineRotation: Int? = null
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                SensorManager.getRotationMatrixFromVector(matrix, event.values)
                val rotation = view.display?.rotation ?: Surface.ROTATION_0
                val (x, y) = when (rotation) {
                    Surface.ROTATION_90 -> SensorManager.AXIS_Y to SensorManager.AXIS_MINUS_X
                    Surface.ROTATION_180 -> SensorManager.AXIS_MINUS_X to SensorManager.AXIS_MINUS_Y
                    Surface.ROTATION_270 -> SensorManager.AXIS_MINUS_Y to SensorManager.AXIS_X
                    else -> SensorManager.AXIS_X to SensorManager.AXIS_Y
                }
                SensorManager.remapCoordinateSystem(matrix, x, y, screenMatrix)
                if (baselineRotation != rotation) {
                    screenMatrix.copyInto(baseline)
                    baselineRotation = rotation
                    tilt.value = Offset.Zero
                    return
                }
                // Project the current screen normal onto the initial screen axes.
                fun relative(axis: Int) = (baseline[axis] * screenMatrix[2] +
                    baseline[axis + 3] * screenMatrix[5] +
                    baseline[axis + 6] * screenMatrix[8]).coerceIn(-1f, 1f)
                val degrees = 180f / kotlin.math.PI.toFloat()
                val target = Offset(-asin(relative(1)) * degrees, asin(relative(0)) * degrees)
                tilt.value = tilt.value * .78f + target * .22f
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }
        val observer = object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                if (sensor != null) manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME)
            }

            override fun onPause(owner: LifecycleOwner) {
                manager.unregisterListener(listener)
                baselineRotation = null
            }
        }
        owner?.lifecycle?.addObserver(observer)
        onDispose {
            owner?.lifecycle?.removeObserver(observer)
            manager.unregisterListener(listener)
        }
    }
    return tilt
}
