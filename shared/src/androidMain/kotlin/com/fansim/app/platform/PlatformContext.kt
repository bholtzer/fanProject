package com.fansim.app.platform

import android.app.Activity
import android.content.Context

/**
 * The shared module has no Android Context of its own; the androidApp module
 * assigns these once at startup (Application.onCreate / Activity.onCreate).
 */
object PlatformContext {
    lateinit var appContext: Context
    var currentActivity: Activity? = null
}
