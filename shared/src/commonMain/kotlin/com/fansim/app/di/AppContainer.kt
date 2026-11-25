package com.fansim.app.di

import com.fansim.app.data.repository.FanRepositoryImpl
import com.fansim.app.domain.repository.FanRepository
import com.fansim.app.domain.usecase.GetFanConfigUseCase
import com.fansim.app.domain.usecase.SaveFanConfigUseCase
import com.fansim.app.platform.AdManager
import com.fansim.app.platform.ProximitySensorController
import com.fansim.app.platform.SoundPlayer
import com.fansim.app.platform.VibrationController
import com.russhwolf.settings.ObservableSettings

/**
 * Minimal hand-rolled DI container (no extra framework needed for this app's size).
 * Created once per process and threaded through the composables.
 */
class AppContainer(private val settings: ObservableSettings) {

    private val fanRepository: FanRepository = FanRepositoryImpl(settings)

    val getFanConfig = GetFanConfigUseCase(fanRepository)
    val saveFanConfig = SaveFanConfigUseCase(fanRepository)

    val vibrationController = VibrationController()
    val proximitySensorController = ProximitySensorController()
    val soundPlayer = SoundPlayer()
    val adManager = AdManager().apply { initialize() }

    val soundEnabled: Boolean get() = settings.getBoolean("fan_sound_enabled", true)

    fun setSoundEnabled(enabled: Boolean) {
        settings.putBoolean("fan_sound_enabled", enabled)
    }
}
