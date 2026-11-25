package com.fansim.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import fansimulator.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.fansim.app.platform.FanFullScreenHost
import com.fansim.app.domain.model.FanType
import com.fansim.app.domain.model.FanSpeedLevel
import com.fansim.app.presentation.fanselection.FanProductImage
import kotlin.math.atan2

/** Tap to open the viewer or drag around the rotor to spin it manually. */
@Composable
internal fun InteractiveFanImage(
    fan: FanType,
    isRunning: Boolean,
    modifier: Modifier,
    animationPhase: Float = 0f,
    onRotate: (Float) -> Unit = {},
    onClickLabel: String,
    onClick: (() -> Unit)?
) {
    val rotate by rememberUpdatedState(onRotate)
    val tapModifier = if (onClick != null) {
        Modifier.clickable(onClickLabel = onClickLabel, onClick = onClick)
    } else Modifier
    FanProductImage(
        fan = fan,
        animationPhase = animationPhase,
        isRunning = isRunning,
        modifier = modifier.then(tapModifier)
            .pointerInput(fan.id) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val previous = change.position - dragAmount
                    val currentAngle = atan2(change.position.y - center.y, change.position.x - center.x)
                    val previousAngle = atan2(previous.y - center.y, previous.x - center.x)
                    var delta = (currentAngle - previousAngle) * 180f / kotlin.math.PI.toFloat()
                    if (delta > 180f) delta -= 360f
                    if (delta < -180f) delta += 360f
                    rotate(delta)
                }
            }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun FanFullScreenDialog(
    fan: FanType,
    speed: FanSpeedLevel,
    animationPhase: Float = 0f,
    onClose: () -> Unit,
    onPower: (() -> Unit)? = null,
    onIncreaseSpeed: (() -> Unit)? = null,
    onDecreaseSpeed: (() -> Unit)? = null,
    canIncreaseSpeed: Boolean = true,
    canDecreaseSpeed: Boolean = true
) {
    val isRunning = speed != FanSpeedLevel.OFF
    FanFullScreenHost(onClose = onClose) {
        BoxWithConstraints(
            Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            val controlsHeight = if (onPower != null) 132.dp else 64.dp
            val artworkSize = minOf(maxWidth, (maxHeight - controlsHeight).coerceAtLeast(0.dp))
            val powerLabel = stringResource(if (isRunning) Res.string.turn_off else Res.string.turn_on)
            FanPhotoViewer(
                fan = fan,
                phase = animationPhase,
                speed = speed,
                powerLabel = powerLabel,
                onPower = onPower,
                artworkSize = artworkSize,
                modifier = Modifier.fillMaxSize()
            )

            val back = stringResource(Res.string.back)
            TextButton(
                onClick = onClose,
                modifier = Modifier.align(Alignment.TopStart).statusBarsPadding()
                    .semantics { contentDescription = back }
            ) {
                Text("‹", fontSize = 28.sp)
            }

            if (onPower != null) {
                FlowRow(
                    Modifier.align(Alignment.BottomCenter).fillMaxWidth().navigationBarsPadding(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                ) {
                    if (onDecreaseSpeed != null) {
                        OutlinedButton(onClick = onDecreaseSpeed, enabled = canDecreaseSpeed) {
                            Text(stringResource(Res.string.slower))
                        }
                    }
                    Button(onClick = onPower) {
                        Text(powerLabel)
                    }
                    if (onIncreaseSpeed != null) {
                        OutlinedButton(onClick = onIncreaseSpeed, enabled = canIncreaseSpeed) {
                            Text(stringResource(Res.string.faster))
                        }
                    }
                }
            }
        }
    }
}
