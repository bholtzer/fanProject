package com.fansim.app.platform

import androidx.compose.runtime.Composable

@Composable
expect fun FanFullScreenHost(onClose: () -> Unit, content: @Composable () -> Unit)


@Composable
expect fun CustomizationBack(onBack: () -> Unit)
