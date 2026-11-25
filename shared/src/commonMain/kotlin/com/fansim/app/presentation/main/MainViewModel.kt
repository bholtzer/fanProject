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
    val isSoundEnabled: Boolean = true,
    val isNear: Boolean = false,
    val rotationDegreesPerSecond: Float = 0f
)

/**
 * Drives the main fan screen:
 * - Power toggles the fan; separate controls adjust speed and sound.
 * - Swiping the fan spins it manually; every full revolution ("reaches its limit")
 *   fires a short vibration tick, independent of the power state.
 * - While the fan is on, a background sound loops. Proximity or pickup movement
 *   boosts volume and vibration. High speed also vibrates.
 */
class MainViewModel(
    initialConfig: FanConfig,
    initialSoundEnabled: Boolean = true,
    private val onSoundEnabledChanged: (Boolean) -> Unit = {},
    private val vibrationController: VibrationController,
    private val proximitySensorController: ProximitySensorController,
    private val soundPlayer: SoundPlayer
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var hapticLoopJob: Job? = null
    private var swipeAccumulatedDegrees = 0f

    private val _state = MutableStateFlow(MainState(config = initialConfig, isSoundEnabled = initialSoundEnabled))
    val state: StateFlow<MainState> = _state.asStateFlow()

    fun start() {
        proximitySensorController.start { isNear ->
            val previous = _state.value
            val speed = if (isNear && !previous.isNear && previous.speed == FanSpeedLevel.OFF) {
                FanSpeedLevel.LOW
            } else {
                previous.speed
            }
            _state.value = previous.copy(
                isNear = isNear,
                speed = speed,
                rotationDegreesPerSecond = speedToRps(speed)
            )
            refreshAudioAndHaptics()
        }
        refreshAudioAndHaptics()
    }

    fun stop() {
        proximitySensorController.stop()
        hapticLoopJob?.cancel()
        hapticLoopJob = null
        soundPlayer.release()
        vibrationController.stop()
    }

    fun onPowerTapped() {
        val next = if (_state.value.speed == FanSpeedLevel.OFF) FanSpeedLevel.LOW else FanSpeedLevel.OFF
        setSpeed(next)
        if (next != FanSpeedLevel.OFF) vibrationController.vibrateTick()
    }

    fun increaseSpeed() {
        val levels = FanSpeedLevel.entries
        val next = levels[(_state.value.speed.ordinal + 1).coerceAtMost(levels.lastIndex)]
        setSpeed(next)
        vibrationController.vibrateTick()
    }

    fun decreaseSpeed() {
        val levels = FanSpeedLevel.entries
        val next = levels[(_state.value.speed.ordinal - 1).coerceAtLeast(0)]
        setSpeed(next)
    }

    fun setSoundEnabled(enabled: Boolean) {
        _state.value = _state.value.copy(isSoundEnabled = enabled)
        onSoundEnabledChanged(enabled)
        refreshAudioAndHaptics()
    }

    private fun setSpeed(next: FanSpeedLevel) {
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
        hapticLoopJob = null

        if (current.speed == FanSpeedLevel.OFF) {
            soundPlayer.stop()
            vibrationController.stop()
            return
        }

        val baseVolume = when (current.speed) {
            FanSpeedLevel.LOW -> 0.65f
            FanSpeedLevel.MEDIUM -> 0.8f
            FanSpeedLevel.HIGH -> 1f
            FanSpeedLevel.OFF -> 0f
        }
        // Near the face: louder sound + a steady vibration. Far away: just the sound.
        val volume = if (current.isNear) (baseVolume * 1.3f).coerceAtMost(1f) else baseVolume
        if (current.isSoundEnabled) {
            soundPlayer.setVolume(volume)
            val playbackRate = when (current.speed) {
                FanSpeedLevel.LOW -> .87f
                FanSpeedLevel.MEDIUM -> 1f
                FanSpeedLevel.HIGH -> 1.18f
                FanSpeedLevel.OFF -> 1f
            }
            // Stable for each catalog model, independent of its display name or language.
            val pitch = .66f + ((current.config.fanType.id * 8) % 21) * .05f
            soundPlayer.start(pitch, playbackRate)
        } else {
            soundPlayer.stop()
        }

        if (current.isNear || current.speed == FanSpeedLevel.HIGH) {
            val intensity = when (current.speed) {
                FanSpeedLevel.LOW -> 0.4f
                FanSpeedLevel.MEDIUM -> 0.7f
                FanSpeedLevel.HIGH -> if (current.isNear) 1f else 0.65f
                FanSpeedLevel.OFF -> 0f
            }
            hapticLoopJob = scope.launch {
                while (isActive) {
                    vibrationController.vibrateContinuous(intensity)
                    delay(300)
                }
            }
        } else {
            vibrationController.stop()
        }
    }
}
