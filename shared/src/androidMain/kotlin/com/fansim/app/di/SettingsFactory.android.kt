package com.fansim.app.di

import com.fansim.app.platform.PlatformContext
import com.russhwolf.settings.SharedPreferencesSettings
import com.russhwolf.settings.ObservableSettings

actual fun createSettings(): ObservableSettings {
    val prefs = PlatformContext.appContext.getSharedPreferences("fan_simulator_prefs", 0)
    return SharedPreferencesSettings(prefs)
}
