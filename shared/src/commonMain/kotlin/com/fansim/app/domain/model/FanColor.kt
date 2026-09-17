package com.fansim.app.domain.model

data class FanColor(val name: String, val hex: Long)

object FanColorCatalog {
    val ALL: List<FanColor> = listOf(
        FanColor("Classic White", 0xFFF5F5F5),
        FanColor("Jet Black", 0xFF212121),
        FanColor("Ocean Blue", 0xFF1976D2),
        FanColor("Sunset Red", 0xFFD32F2F),
        FanColor("Mint Green", 0xFF43A047),
        FanColor("Royal Purple", 0xFF7B1FA2),
        FanColor("Sunflower Yellow", 0xFFFBC02D),
        FanColor("Rose Gold", 0xFFE0A899)
    )
}
