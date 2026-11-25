package com.fansim.app.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import com.fansim.app.domain.model.FanSpeedLevel
import com.fansim.app.domain.model.FanType
import com.fansim.app.presentation.fanselection.fanRotor
import kotlinx.coroutines.delay
import kotlin.math.sin

private enum class AirflowObject { PAPER, GUM, FLY }

@Composable
internal fun FanAirflowEffect(fan: FanType, speed: FanSpeedLevel, modifier: Modifier) {
    val running = speed != FanSpeedLevel.OFF
    val currentSpeed = rememberUpdatedState(speed)
    val progress = remember(fan.id, running) { mutableFloatStateOf(1f) }
    val item = AirflowObject.entries[(fan.id - 1) % AirflowObject.entries.size]
    val targetY = fanRotor(fan.id)?.second ?: .46f
    LaunchedEffect(fan.id, running) {
        if (!running) return@LaunchedEffect
        delay(4_000)
        var elapsed = 0f
        var previous = withFrameNanos { it }
        while (elapsed < 1f) {
            val now = withFrameNanos { it }
            val durationMs = when (currentSpeed.value) {
                FanSpeedLevel.OFF -> break
                FanSpeedLevel.LOW -> 3500f
                FanSpeedLevel.MEDIUM -> 2100f
                FanSpeedLevel.HIGH -> 1100f
            }
            elapsed += ((now - previous) / 1_000_000f).coerceAtMost(50f) / durationMs
            progress.floatValue = elapsed.coerceAtMost(1f)
            previous = now
        }
    }
    Canvas(modifier) {
        val t = progress.floatValue
        if (!running || t >= 1f) return@Canvas
        val approach = t * t
        val fade = ((1f - t) * 5f).coerceAtMost(1f)
        val center = Offset(
            size.width * (.92f - .42f * approach),
            size.height * (targetY + (.27f + sin(t * 25f) * .045f) * (1f - approach))
        )
        val radius = size.minDimension * .042f
        scale(.4f + fade * .6f, pivot = center) {
            rotate(t * 620f, pivot = center) {
                when (item) {
                    AirflowObject.PAPER -> {
                        val scrap = Path().apply {
                            moveTo(center.x - radius, center.y - radius * .72f)
                            lineTo(center.x + radius * .7f, center.y - radius)
                            lineTo(center.x + radius, center.y + radius * .69f)
                            lineTo(center.x - radius * .63f, center.y + radius)
                            close()
                        }
                        drawPath(scrap, Color(0xFFFFF0CC), alpha = fade)
                        drawPath(scrap, Color(0xFF906F45), alpha = fade,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(radius * .10f))
                        drawLine(Color(0xFFBA9566), center - Offset(radius * .55f, 0f),
                            center + Offset(radius * .58f, radius * .4f), radius * .10f, alpha = fade)
                    }
                    AirflowObject.GUM -> {
                        drawOval(Color(0xFFDD4F86), center - Offset(radius, radius * .65f),
                            Size(radius * 2f, radius * 1.3f), alpha = fade)
                        drawOval(Color(0xFFFFA5C4), center - Offset(radius * .67f, radius * .45f),
                            Size(radius * .88f, radius * .45f), alpha = fade)
                    }
                    AirflowObject.FLY -> {
                        val wingHeight = radius * (.5f + .42f * sin(t * 230f))
                        for (direction in listOf(-1f, 1f)) {
                            val wing = center + Offset(direction * radius * .55f, -radius * .45f)
                            drawOval(Color(0xFF779BB0), wing - Offset(radius * .45f, wingHeight / 2),
                                Size(radius * .9f, wingHeight), alpha = fade)
                        }
                        drawOval(Color(0xFF22252A), center - Offset(radius * .33f, radius * .52f),
                            Size(radius * .66f, radius * 1.04f), alpha = fade)
                        drawCircle(Color(0xFF852D3F), radius * .24f,
                            center - Offset(0f, radius * .5f), alpha = fade)
                    }
                }
            }
        }
    }
}
