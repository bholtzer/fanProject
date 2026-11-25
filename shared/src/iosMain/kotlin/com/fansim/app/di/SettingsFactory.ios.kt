@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.fansim.app.di

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.ObservableSettings
import platform.Foundation.NSUserDefaults

actual fun createSettings(): ObservableSettings {
    return NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults)
}
