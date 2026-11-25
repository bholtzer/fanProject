package com.bihstudio.fan

import android.app.Application
import com.fansim.app.platform.PlatformContext

class FanApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        PlatformContext.appContext = applicationContext
    }
}
