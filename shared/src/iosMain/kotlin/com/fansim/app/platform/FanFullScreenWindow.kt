package com.fansim.app.platform

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import fansimulator.shared.generated.resources.Res
import fansimulator.shared.generated.resources.back
import org.jetbrains.compose.resources.stringResource

@Composable
actual fun FanFullScreenHost(onClose: () -> Unit, content: @Composable () -> Unit) {
    Box {
        content()
        Box(Modifier.align(Alignment.TopStart).statusBarsPadding()) {
            CustomizationBack(onClose)
        }
    }
}

@Composable
actual fun CustomizationBack(onBack: () -> Unit) {
    val label = stringResource(Res.string.back)
    TextButton(onClick = onBack, modifier = Modifier.semantics { contentDescription = label }) {
        Text("‹")
    }
}
