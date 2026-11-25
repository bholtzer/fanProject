package com.fansim.app.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.withFrameNanos
import kotlinx.coroutines.isActive

/** One frame clock shared by the inline and full-screen artwork. */
@Composable
internal fun rememberFanAnimationPhase(fanId: Int, degreesPerSecond: Float): MutableState<Float> {
    val phase = rememberSaveable(fanId) { mutableStateOf(0f) }
    LaunchedEffect(fanId, degreesPerSecond) {
        if (degreesPerSecond == 0f) return@LaunchedEffect
        var previousFrame = withFrameNanos { it }
        while (isActive) {
            withFrameNanos { frame ->
                val elapsedSeconds = (frame - previousFrame) / 1_000_000_000f
                phase.value = (phase.value + degreesPerSecond * elapsedSeconds) % 360f
                previousFrame = frame
            }
        }
    }
    return phase
}
