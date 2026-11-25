package com.fansim.app.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.geometry.Offset

/** Pitch and yaw in degrees relative to the phone pose when the preview opened. */
@Composable
internal expect fun rememberFanTilt(): State<Offset>
