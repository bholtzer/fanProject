package com.fansim.app.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset

@Composable
internal actual fun rememberFanTilt(): State<Offset> = remember { mutableStateOf(Offset.Zero) }
