package com.fansim.app

import androidx.compose.ui.window.ComposeUIViewController
import com.fansim.app.di.AppContainer
import com.fansim.app.di.createSettings
import platform.UIKit.UIViewController

/** Called from iosApp/ContentView.swift to bootstrap the shared Compose UI. */
fun MainViewController(): UIViewController {
    val container = AppContainer(createSettings())
    return ComposeUIViewController {
        App(container)
    }
}
