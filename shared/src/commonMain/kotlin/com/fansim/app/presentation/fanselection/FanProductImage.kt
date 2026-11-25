package com.fansim.app.presentation.fanselection

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import kotlin.math.sin
import com.fansim.app.presentation.localizedName
import com.fansim.app.domain.model.FanType
import fansimulator.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource

/** Current product imagery shared by every fan screen. */
@Composable
fun FanProductImage(
    fan: FanType,
    modifier: Modifier = Modifier,
    animationPhase: Float = 0f,
    isRunning: Boolean = false
) {
    val image = when (fan.imageKey) {
        "fan_product_1" -> Res.drawable.fan_product_1
        "fan_product_2" -> Res.drawable.fan_product_2
        "fan_product_3" -> Res.drawable.fan_product_3
        "fan_product_4" -> Res.drawable.fan_product_4
        "fan_product_5" -> Res.drawable.fan_product_5
        "fan_product_6" -> Res.drawable.fan_product_6
        "fan_product_7" -> Res.drawable.fan_product_7
        "fan_product_8" -> Res.drawable.fan_product_8
        "fan_product_9" -> Res.drawable.fan_product_9
        "fan_product_10" -> Res.drawable.fan_product_10
        "fan_product_11" -> Res.drawable.fan_product_11
        "fan_product_12" -> Res.drawable.fan_product_12
        "fan_product_13" -> Res.drawable.fan_product_13
        "fan_product_14" -> Res.drawable.fan_product_14
        "fan_product_15" -> Res.drawable.fan_product_15
        "fan_product_16" -> Res.drawable.fan_product_16
        "fan_product_17" -> Res.drawable.fan_product_17
        "fan_product_18" -> Res.drawable.fan_product_18
        "fan_product_19" -> Res.drawable.fan_product_19
        "fan_product_20" -> Res.drawable.fan_product_20
        "fan_product_21" -> Res.drawable.fan_product_21
        else -> error("Missing product image ${fan.imageKey} for fan ${fan.id}")
    }
    Image(
        painter = painterResource(image),
        contentDescription = fan.localizedName(),
        contentScale = ContentScale.Fit,
        modifier = modifier.drawWithContent {
            // Coordinates refer to the square source artwork, not the view bounds.
            val side = minOf(size.width, size.height)
            val origin = Offset((size.width - side) / 2f, (size.height - side) / 2f)
            val rotor = fanRotor(fan.id)
            if (rotor != null && animationPhase != 0f) {
                val center = origin + Offset(side * rotor.first, side * rotor.second)
                val radius = side * rotor.third
                val aperture = Path().apply {
                    addOval(Rect(center.x - radius, center.y - radius, center.x + radius, center.y + radius))
                }
                // Replace the rotor area instead of painting over stationary blades.
                // The stand and outer frame keep their original position.
                clipPath(aperture, ClipOp.Difference) { this@drawWithContent.drawContent() }
                clipPath(aperture) {
                    rotate(animationPhase, center) { this@drawWithContent.drawContent() }
                }
            } else if (fan.id in listOf(1, 11, 12)) {
                val pivot = origin + Offset(side * .5f, side * if (fan.id == 1) .1f else .9f)
                rotate(sin(animationPhase * kotlin.math.PI.toFloat() / 180f) * 14f, pivot) {
                    this@drawWithContent.drawContent()
                }
            } else {
                drawContent()
                if (isRunning && fan.bladeCount == 0) {
                    // Bladeless designs show airflow rather than spinning their housing.
                    repeat(4) { index ->
                        val progress = ((animationPhase / 180f + index / 4f) % 1f + 1f) % 1f
                        val x = origin.x + side * (.34f + index * .10f)
                        val y = origin.y + side * (.65f - progress * .45f)
                        drawLine(Color(0xFF65CFE9).copy(alpha = (1f - progress) * .8f),
                            Offset(x, y), Offset(x, y - side * .1f),
                            strokeWidth = side * .012f, cap = StrokeCap.Round)
                    }
                }
            }
        }
    )
}

internal fun fanRotor(id: Int): Triple<Float, Float, Float>? = when (id) {
    2, 3, 4 -> Triple(.5f, .36f, .32f)
    5 -> Triple(.5f, .48f, .34f)
    6, 9 -> Triple(.5f, .20f, .18f)
    10 -> Triple(.5f, .38f, .40f)
    13 -> Triple(.5f, .50f, .48f)
    14 -> Triple(.5f, .36f, .33f)
    15 -> Triple(.5f, .43f, .40f)
    16 -> Triple(.5f, .43f, .35f)
    17 -> Triple(.5f, .34f, .28f)
    19 -> Triple(.5f, .43f, .37f)
    20 -> Triple(.5f, .36f, .32f)
    21 -> Triple(.5f, .36f, .33f)
    else -> null
}
