package com.fansim.app.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.fansim.app.App
import com.fansim.app.di.AppContainer
import com.fansim.app.di.createSettings
import com.fansim.app.platform.PlatformContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PlatformContext.appContext = applicationContext
        PlatformContext.currentActivity = this

        val container = AppContainer(createSettings())

        setContent {
            App(container)
        }
    }

    override fun onDestroy() {
        if (PlatformContext.currentActivity === this) {
            PlatformContext.currentActivity = null
        }
        super.onDestroy()
    }
}
