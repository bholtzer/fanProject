package com.fansim.app.di

import com.fansim.app.data.repository.FanRepositoryImpl
import com.fansim.app.domain.repository.FanRepository
import com.fansim.app.domain.usecase.GetFanConfigUseCase
import com.fansim.app.domain.usecase.MarkFirstLaunchAdsShownUseCase
import com.fansim.app.domain.usecase.ObserveFirstLaunchAdsShownUseCase
import com.fansim.app.domain.usecase.SaveFanConfigUseCase
import com.fansim.app.platform.AdManager
import com.fansim.app.platform.ProximitySensorController
import com.fansim.app.platform.SoundPlayer
import com.fansim.app.platform.VibrationController
import com.russhwolf.settings.Settings

/**
 * Minimal hand-rolled DI container (no extra framework needed for this app's size).
 * Created once per process and threaded through the composables.
 */
class AppContainer(settings: Settings) {

    private val fanRepository: FanRepository = FanRepositoryImpl(settings)

    val getFanConfig = GetFanConfigUseCase(fanRepository)
    val saveFanConfig = SaveFanConfigUseCase(fanRepository)
    val observeFirstLaunchAdsShown = ObserveFirstLaunchAdsShownUseCase(fanRepository)
    val markFirstLaunchAdsShown = MarkFirstLaunchAdsShownUseCase(fanRepository)

    val vibrationController = VibrationController()
    val proximitySensorController = ProximitySensorController()
    val soundPlayer = SoundPlayer()
    val adManager = AdManager().apply { initialize() }
}
