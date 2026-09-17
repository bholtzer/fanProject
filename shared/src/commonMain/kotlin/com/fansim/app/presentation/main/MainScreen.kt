package com.fansim.app.presentation.main

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fansim.app.domain.model.FanSpeedLevel
import com.fansim.app.presentation.components.FanBladesShape
import kotlin.math.atan2

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onOpenSettingsAgain: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    DisposableEffect(Unit) {
        viewModel.start()
        onDispose { viewModel.stop() }
    }

    var lastDragAngle by remember { mutableFloatStateOf(0f) }
    var manualRotation by remember { mutableFloatStateOf(0f) }

    // Continuous spin animation while the fan is powered on. Hooks must always run
    // unconditionally, so we always animate and just ignore the value when powered off.
    val infiniteTransition = androidx.compose.animation.core.rememberInfiniteTransition(label = "fan-spin")
    val revolutionMillis = (60_000 / state.rotationDegreesPerSecond.coerceAtLeast(1f)).toInt().coerceIn(150, 4000)
    val autoAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(revolutionMillis, easing = LinearEasing), RepeatMode.Restart),
        label = "auto-angle"
    )

    val displayAngle = if (state.speed != FanSpeedLevel.OFF) autoAngle else manualRotation

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(state.config.name, fontSize = 24.sp, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 24.dp))
        Text(state.config.fanType.displayName, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .size(240.dp)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { lastDragAngle = 0f },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                val center = Offset(size.width / 2f, size.height / 2f)
                                val previous = change.position - dragAmount
                                val angleNow = atan2(
                                    (change.position.y - center.y).toDouble(),
                                    (change.position.x - center.x).toDouble()
                                )
                                val anglePrev = atan2(
                                    (previous.y - center.y).toDouble(),
                                    (previous.x - center.x).toDouble()
                                )
                                var deltaDeg = ((angleNow - anglePrev) * 180.0 / Math.PI).toFloat()
                                if (deltaDeg > 180f) deltaDeg -= 360f
                                if (deltaDeg < -180f) deltaDeg += 360f
                                manualRotation += deltaDeg
                                viewModel.onSwipeDragged(deltaDeg)
                            }
                        )
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { viewModel.onPowerTapped() })
                    }
            ) {
                rotate(displayAngle) {
                    FanBladesShape.draw(
                        this,
                        bladeCount = state.config.fanType.bladeCount.takeIf { it > 0 } ?: 4,
                        color = Color(state.config.fanColor.hex)
                    )
                }
            }
        }

        Text(
            text = when (state.speed) {
                FanSpeedLevel.OFF -> "Tap the fan to turn it on"
                else -> "Speed: ${state.speed.name}  •  ${if (state.isNear) "Close to face 🌬️ (louder + buzzing)" else "At a distance (sound only)"}"
            },
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )
    }
}
