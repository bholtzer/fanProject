package com.fansim.app.presentation.main

import com.fansim.app.domain.model.FanConfig
import com.fansim.app.domain.model.FanSpeedLevel
import com.fansim.app.platform.ProximitySensorController
import com.fansim.app.platform.SoundPlayer
import com.fansim.app.platform.VibrationController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class MainState(
    val config: FanConfig,
    val speed: FanSpeedLevel = FanSpeedLevel.OFF,
    val isNear: Boolean = false,
    val rotationDegreesPerSecond: Float = 0f
)

/**
 * Drives the main fan screen:
 * - Tapping the fan cycles OFF -> LOW -> MEDIUM -> HIGH -> OFF, starting/stopping sound.
 * - Swiping the fan spins it manually; every full revolution ("reaches its limit")
 *   fires a short vibration tick, independent of the power state.
 * - While the fan is on, a background sound loops. If the phone is held near the
 *   user's face (proximity sensor), both the sound volume and a continuous vibration
 *   increase; moving away drops back to sound only.
 */
class MainViewModel(
    initialConfig: FanConfig,
    private val vibrationController: VibrationController,
    private val proximitySensorController: ProximitySensorController,
    private val soundPlayer: SoundPlayer
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var hapticLoopJob: Job? = null
    private var swipeAccumulatedDegrees = 0f

    private val _state = MutableStateFlow(MainState(config = initialConfig))
    val state: StateFlow<MainState> = _state.asStateFlow()

    fun start() {
        proximitySensorController.start { isNear ->
            _state.value = _state.value.copy(isNear = isNear)
            refreshAudioAndHaptics()
        }
    }

    fun stop() {
        proximitySensorController.stop()
        soundPlayer.stop()
        vibrationController.stop()
        hapticLoopJob?.cancel()
    }

    fun onPowerTapped() {
        val next = _state.value.speed.next()
        _state.value = _state.value.copy(speed = next, rotationDegreesPerSecond = speedToRps(next))
        refreshAudioAndHaptics()
    }

    /** Call with the drag delta angle (degrees) as the user swipes the fan. */
    fun onSwipeDragged(deltaDegrees: Float) {
        swipeAccumulatedDegrees += deltaDegrees
        if (swipeAccumulatedDegrees >= 360f || swipeAccumulatedDegrees <= -360f) {
            swipeAccumulatedDegrees = 0f
            vibrationController.vibrateTick()
        }
    }

    private fun speedToRps(speed: FanSpeedLevel): Float = when (speed) {
        FanSpeedLevel.OFF -> 0f
        FanSpeedLevel.LOW -> 90f
        FanSpeedLevel.MEDIUM -> 200f
        FanSpeedLevel.HIGH -> 340f
    }

    private fun refreshAudioAndHaptics() {
        val current = _state.value
        hapticLoopJob?.cancel()

        if (current.speed == FanSpeedLevel.OFF) {
            soundPlayer.stop()
            vibrationController.stop()
            return
        }

        soundPlayer.start()
        val baseVolume = when (current.speed) {
            FanSpeedLevel.LOW -> 0.35f
            FanSpeedLevel.MEDIUM -> 0.6f
            FanSpeedLevel.HIGH -> 0.9f
            FanSpeedLevel.OFF -> 0f
        }
        // Near the face: louder sound + a steady vibration. Far away: just the sound.
        val volume = if (current.isNear) (baseVolume * 1.3f).coerceAtMost(1f) else baseVolume * 0.6f
        soundPlayer.setVolume(volume)

        if (current.isNear) {
            val intensity = when (current.speed) {
                FanSpeedLevel.LOW -> 0.4f
                FanSpeedLevel.MEDIUM -> 0.7f
                FanSpeedLevel.HIGH -> 1f
                FanSpeedLevel.OFF -> 0f
            }
            hapticLoopJob = scope.launch {
                while (isActive) {
                    vibrationController.vibrateContinuous(intensity)
                    delay(200)
                }
            }
        } else {
            vibrationController.stop()
        }
    }
}
