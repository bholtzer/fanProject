package com.fansim.app.platform

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

@Composable
actual fun FanFullScreenHost(onClose: () -> Unit, content: @Composable () -> Unit) {
    BackHandler(onBack = onClose)
    val window = PlatformContext.currentActivity?.window
    DisposableEffect(window) {
        val controller = window?.let { WindowCompat.getInsetsController(it, it.decorView) }
        val previousBehavior = controller?.systemBarsBehavior
        controller?.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller?.hide(WindowInsetsCompat.Type.systemBars())
        onDispose {
            controller?.show(WindowInsetsCompat.Type.systemBars())
            if (previousBehavior != null) controller?.systemBarsBehavior = previousBehavior
        }
    }
    content()
}


@Composable
actual fun CustomizationBack(onBack: () -> Unit) = BackHandler(onBack = onBack)
