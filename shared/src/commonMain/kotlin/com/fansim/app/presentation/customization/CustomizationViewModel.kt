package com.fansim.app.presentation.customization

import com.fansim.app.domain.model.FanColor
import com.fansim.app.domain.model.FanColorCatalog
import com.fansim.app.domain.model.FanConfig
import com.fansim.app.domain.model.FanType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class CustomizationState(
    val colors: List<FanColor> = FanColorCatalog.ALL,
    val selectedColor: FanColor = FanColorCatalog.ALL.first(),
    val name: String = ""
) {
    val canContinue: Boolean get() = name.isNotBlank()
}

class CustomizationViewModel(private val fanType: FanType) {
    private val _state = MutableStateFlow(CustomizationState())
    val state: StateFlow<CustomizationState> = _state.asStateFlow()

    fun selectColor(color: FanColor) {
        _state.value = _state.value.copy(selectedColor = color)
    }

    fun updateName(name: String) {
        _state.value = _state.value.copy(name = name.take(20))
    }

    fun buildConfig(): FanConfig = FanConfig(
        fanType = fanType,
        fanColor = _state.value.selectedColor,
        name = _state.value.name.ifBlank { "My Fan" }
    )
}
