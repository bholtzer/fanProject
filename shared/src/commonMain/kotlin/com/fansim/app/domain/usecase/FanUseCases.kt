package com.fansim.app.domain.usecase

import com.fansim.app.domain.model.FanConfig
import com.fansim.app.domain.repository.FanRepository
import kotlinx.coroutines.flow.Flow

class GetFanConfigUseCase(private val repository: FanRepository) {
    operator fun invoke(): Flow<FanConfig?> = repository.observeFanConfig()
}

class SaveFanConfigUseCase(private val repository: FanRepository) {
    suspend operator fun invoke(config: FanConfig) = repository.saveFanConfig(config)
}

class ObserveFirstLaunchAdsShownUseCase(private val repository: FanRepository) {
    operator fun invoke(): Flow<Boolean> = repository.observeFirstLaunchAdsShown()
}

class MarkFirstLaunchAdsShownUseCase(private val repository: FanRepository) {
    suspend operator fun invoke() = repository.markFirstLaunchAdsShown()
}
