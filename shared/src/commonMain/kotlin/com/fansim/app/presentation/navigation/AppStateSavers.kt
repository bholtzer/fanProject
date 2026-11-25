package com.fansim.app.presentation.navigation

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import com.fansim.app.domain.model.FanCatalog
import com.fansim.app.domain.model.FanColor
import com.fansim.app.domain.model.FanConfig
import com.fansim.app.domain.model.FanEra
import com.fansim.app.domain.model.FanType
import com.fansim.app.presentation.fanselection.FanSelectionViewModel

internal val ScreenSaver = Saver<Screen, String>(
    save = {
        when (it) {
            Screen.Splash -> "splash"
            Screen.FanSelection -> "selection"
            Screen.Customization -> "customization"
            Screen.Main -> "main"
        }
    },
    restore = {
        when (it) {
            "customization" -> Screen.Customization
            "main" -> Screen.Main
            "selection" -> Screen.FanSelection
            else -> Screen.Splash
        }
    }
)

internal val FanTypeSaver = Saver<FanType, Int>(
    save = { it.id },
    restore = { id -> FanCatalog.ALL.firstOrNull { it.id == id } ?: FanCatalog.ALL.first() }
)

internal val FanConfigSaver = listSaver<FanConfig, Any>(
    save = { listOf(it.fanType.id, it.fanColor.name, it.fanColor.hex, it.name) },
    restore = {
        FanConfig(
            FanCatalog.ALL.firstOrNull { fan -> fan.id == it[0] as Int } ?: FanCatalog.ALL.first(),
            FanColor(it[1] as String, it[2] as Long),
            it[3] as String
        )
    }
)

internal val FanSelectionSaver = listSaver<FanSelectionViewModel, String>(
    save = { listOf(it.state.value.era?.name.orEmpty(), it.state.value.selected?.id?.toString().orEmpty()) },
    restore = { saved ->
        FanSelectionViewModel().apply {
            filter(FanEra.entries.firstOrNull { it.name == saved[0] })
            FanCatalog.ALL.firstOrNull { it.id.toString() == saved[1] }?.let(::select)
        }
    }
)
