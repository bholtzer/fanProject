package com.fansim.app.domain.repository

import com.fansim.app.domain.model.FanConfig
import kotlinx.coroutines.flow.Flow

interface FanRepository {
    fun observeFanConfig(): Flow<FanConfig?>
    suspend fun saveFanConfig(config: FanConfig)

    fun observeFirstLaunchAdsShown(): Flow<Boolean>
    suspend fun markFirstLaunchAdsShown()
}
