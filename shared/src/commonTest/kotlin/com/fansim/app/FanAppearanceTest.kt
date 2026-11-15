package com.fansim.app

import com.fansim.app.domain.model.FanCatalog
import com.fansim.app.domain.model.FanColorCatalog
import com.fansim.app.presentation.customization.CustomizationViewModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FanAppearanceTest {
    @Test
    fun selectingEachFanKeepsItsOriginalFinishAndIdentity() {
        FanCatalog.ALL.forEach { fan ->
            val model = CustomizationViewModel(fan)
            val config = model.buildConfig()
            assertEquals(fan, config.fanType)
            assertEquals(FanColorCatalog.originalFor(fan), config.fanColor)
            // Saved color names must resolve in the same palette used by the repository.
            assertEquals(config.fanColor, FanColorCatalog.forFan(fan).single { it.name == config.fanColor.name })
        }
    }

    @Test
    fun customFinishesRemainSelectableForEveryDesign() {
        FanCatalog.ALL.forEach { fan ->
            val model = CustomizationViewModel(fan)
            val chosen = FanColorCatalog.ALL.first { it.name == "Sunset Red" }
            assertTrue(chosen in model.state.value.colors)
            model.selectColor(chosen)
            assertEquals(chosen, model.buildConfig().fanColor)
            assertEquals(fan, model.buildConfig().fanType)
        }
    }

    @Test
    fun visibleRotorCountsMatchTheProductDesigns() {
        val expected = mapOf(2 to 4, 3 to 4, 4 to 3, 5 to 5, 6 to 5, 9 to 7,
            10 to 4, 13 to 4, 14 to 3, 15 to 6, 16 to 3, 17 to 5, 19 to 3, 20 to 3, 21 to 3)
        expected.forEach { (id, count) ->
            assertEquals(count, FanCatalog.ALL.single { it.id == id }.bladeCount)
        }
    }
}
