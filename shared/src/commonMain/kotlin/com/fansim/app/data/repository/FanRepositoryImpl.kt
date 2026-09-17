package com.fansim.app.data.repository

import com.fansim.app.data.local.Keys
import com.fansim.app.domain.model.FanCatalog
import com.fansim.app.domain.model.FanColorCatalog
import com.fansim.app.domain.model.FanConfig
import com.fansim.app.domain.repository.FanRepository
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.toFlowSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Persists the user's fan choice + first-launch flag using multiplatform-settings,
 * which is backed by SharedPreferences on Android and NSUserDefaults on iOS.
 */
@OptIn(ExperimentalSettingsApi::class)
class FanRepositoryImpl(settings: Settings) : FanRepository {

    private val flowSettings = (settings as ObservableSettings).toFlowSettings()

    override fun observeFanConfig(): Flow<FanConfig?> = combine(
        flowSettings.getStringOrNullFlow(Keys.FAN_NAME),
        flowSettings.getIntOrNullFlow(Keys.FAN_TYPE_ID),
        flowSettings.getStringOrNullFlow(Keys.FAN_COLOR_NAME)
    ) { name, typeId, colorName ->
        if (name == null || typeId == null || colorName == null) return@combine null
        val type = FanCatalog.ALL.firstOrNull { it.id == typeId } ?: return@combine null
        val color = FanColorCatalog.ALL.firstOrNull { it.name == colorName } ?: return@combine null
        FanConfig(type, color, name)
    }

    override suspend fun saveFanConfig(config: FanConfig) {
        flowSettings.putInt(Keys.FAN_TYPE_ID, config.fanType.id)
        flowSettings.putString(Keys.FAN_COLOR_NAME, config.fanColor.name)
        flowSettings.putString(Keys.FAN_NAME, config.name)
    }

    override fun observeFirstLaunchAdsShown(): Flow<Boolean> =
        flowSettings.getBooleanFlow(Keys.FIRST_LAUNCH_ADS_SHOWN, false)

    override suspend fun markFirstLaunchAdsShown() {
        flowSettings.putBoolean(Keys.FIRST_LAUNCH_ADS_SHOWN, true)
    }
}
