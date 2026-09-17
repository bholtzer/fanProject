package com.fansim.app.presentation.fanselection

import com.fansim.app.domain.model.FanCatalog
import com.fansim.app.domain.model.FanType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class FanSelectionState(
    val fanTypes: List<FanType> = FanCatalog.ALL,
    val selected: FanType? = null
)

/** Plain StateFlow-based ViewModel; works identically on Android and iOS via Compose Multiplatform. */
class FanSelectionViewModel {
    private val _state = MutableStateFlow(FanSelectionState())
    val state: StateFlow<FanSelectionState> = _state.asStateFlow()

    fun select(fanType: FanType) {
        _state.value = _state.value.copy(selected = fanType)
    }
}
