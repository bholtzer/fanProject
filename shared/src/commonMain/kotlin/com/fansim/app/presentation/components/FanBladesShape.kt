package com.fansim.app.presentation.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.cos
import kotlin.math.sin

/** Simple procedural fan-blade graphic, shared by the splash and main screens. */
object FanBladesShape {
    fun draw(scope: DrawScope, bladeCount: Int, color: Color) {
        val center = Offset(scope.size.width / 2f, scope.size.height / 2f)
        val bladeLength = scope.size.minDimension / 2f
        val count = bladeCount.coerceAtLeast(3)
        val step = 360f / count
        for (i in 0 until count) {
            scope.rotate(degrees = step * i, pivot = center) {
                drawOval(
                    color = color,
                    topLeft = Offset(center.x - bladeLength * 0.12f, center.y - bladeLength),
                    size = androidx.compose.ui.geometry.Size(bladeLength * 0.24f, bladeLength)
                )
            }
        }
        scope.drawCircle(color = Color(0xFF616161), radius = bladeLength * 0.14f, center = center)
    }
}
