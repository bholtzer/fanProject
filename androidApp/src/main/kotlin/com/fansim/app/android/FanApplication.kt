package com.fansim.app.android

import android.app.Application
import com.fansim.app.platform.PlatformContext

class FanApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        PlatformContext.appContext = applicationContext
    }
}
