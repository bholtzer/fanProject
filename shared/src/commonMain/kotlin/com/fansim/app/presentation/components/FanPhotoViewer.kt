package com.fansim.app.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import com.fansim.app.domain.model.FanType
import com.fansim.app.domain.model.FanSpeedLevel
import com.fansim.app.platform.rememberFanTilt
import com.fansim.app.presentation.fanselection.FanProductImage
import fansimulator.shared.generated.resources.Res
import fansimulator.shared.generated.resources.zoom_in
import fansimulator.shared.generated.resources.zoom_out
import org.jetbrains.compose.resources.stringResource

private const val MIN_ZOOM = 1f
private const val MAX_ZOOM = 4f
private const val ZOOM_STEP = 1.5f

@Composable
internal fun FanPhotoViewer(
    fan: FanType,
    phase: Float,
    speed: FanSpeedLevel,
    powerLabel: String,
    onPower: (() -> Unit)?,
    artworkSize: Dp,
    modifier: Modifier = Modifier
) {
    val tilt by rememberFanTilt()
    var zoom by remember(fan.id) { mutableStateOf(MIN_ZOOM) }
    var pan by remember(fan.id) { mutableStateOf(Offset.Zero) }
    var angle by remember(fan.id) { mutableStateOf(Offset.Zero) }
    var viewport by remember { mutableStateOf(IntSize.Zero) }
    val artworkPx = with(LocalDensity.current) { artworkSize.toPx() }

    fun clampPan(offset: Offset, scale: Float, bounds: IntSize): Offset {
        val maxX = ((artworkPx * scale - bounds.width) / 2f).coerceAtLeast(0f)
        val maxY = ((artworkPx * scale - bounds.height) / 2f).coerceAtLeast(0f)
        return Offset(offset.x.coerceIn(-maxX, maxX), offset.y.coerceIn(-maxY, maxY))
    }

    fun changeZoom(target: Float, anchor: Offset = Offset.Zero, gesturePan: Offset = Offset.Zero) {
        val next = target.coerceIn(MIN_ZOOM, MAX_ZOOM)
        val ratio = next / zoom
        pan = clampPan(anchor + (pan - anchor) * ratio + gesturePan, next, viewport)
        zoom = next
    }

    val tap = if (onPower == null) Modifier else Modifier.clickable(onClickLabel = powerLabel, onClick = onPower)
    Box(modifier.clipToBounds().onSizeChanged { bounds ->
        viewport = bounds
        pan = clampPan(pan, zoom, bounds)
    }) {
        Box(
            Modifier.matchParentSize().pointerInput(fan.id, artworkPx) {
                detectTransformGestures { centroid, delta, zoomChange, _ ->
                    if (zoomChange != 1f || zoom > MIN_ZOOM) {
                        val center = Offset(size.width / 2f, size.height / 2f)
                        changeZoom(zoom * zoomChange, centroid - center, delta)
                    } else {
                        angle = Offset(
                            (angle.x - delta.y * .22f).coerceIn(-40f, 40f),
                            (angle.y + delta.x * .22f).coerceIn(-45f, 45f)
                        )
                    }
                }
            },
            contentAlignment = Alignment.Center
        ) {
            Box(Modifier.size(artworkSize).graphicsLayer {
                    scaleX = zoom
                    scaleY = zoom
                    translationX = pan.x
                    translationY = pan.y
                    rotationX = (angle.x + tilt.x * .65f).coerceIn(-45f, 45f)
                    rotationY = (angle.y + tilt.y * .65f).coerceIn(-50f, 50f)
                    cameraDistance = (size.maxDimension * 2f).coerceAtLeast(1f)
            }.then(tap)) {
                FanProductImage(
                    fan = fan,
                    animationPhase = phase,
                    isRunning = speed != FanSpeedLevel.OFF,
                    modifier = Modifier.matchParentSize()
                )
                FanAirflowEffect(fan, speed, Modifier.matchParentSize())
            }
        }

        Row(Modifier.align(Alignment.TopEnd).statusBarsPadding()) {
            val zoomOut = stringResource(Res.string.zoom_out)
            val zoomIn = stringResource(Res.string.zoom_in)
            IconButton(
                onClick = { changeZoom(zoom / ZOOM_STEP) },
                enabled = zoom > MIN_ZOOM,
                modifier = Modifier.semantics { contentDescription = zoomOut }
            ) {
                Text("−", Modifier.clearAndSetSemantics {}, fontSize = 26.sp)
            }
            IconButton(
                onClick = { changeZoom(zoom * ZOOM_STEP) },
                enabled = zoom < MAX_ZOOM,
                modifier = Modifier.semantics { contentDescription = zoomIn }
            ) {
                Text("+", Modifier.clearAndSetSemantics {}, fontSize = 26.sp)
            }
        }
    }
}
