package com.fansim.app.presentation.splash

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fansim.app.presentation.components.FanBladesShape
import kotlinx.coroutines.delay

/**
 * Opening screen: a funny, whirring fan image/animation as a welcome splash.
 * Auto-advances after a short delay.
 */
@Composable
fun SplashScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2200)
        onFinished()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "splash-spin")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(700, easing = LinearEasing), RepeatMode.Restart),
        label = "angle"
    )

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Canvas(modifier = Modifier.size(160.dp)) {
                rotate(angle) {
                    FanBladesShape.draw(this, bladeCount = 4, color = Color(0xFF42A5F5))
                }
            }
            Text(
                text = "Fan-tastic!",
                fontSize = 28.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = "Pick your fan, name it, and feel the breeze.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
