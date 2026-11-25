package com.fansim.app.domain.model

data class FanColor(val name: String, val hex: Long)

object FanColorCatalog {
    /** Original accent/material for each catalog design, retained in saved FanConfig. */
    fun originalFor(fan: FanType): FanColor = when (fan.id) {
        1 -> FanColor("Cream cloth", 0xFFEDE2CA)
        2, 14 -> FanColor("Aged brass", 0xFFB58B45)
        13 -> FanColor("Walnut wood", 0xFF69402B)
        3 -> FanColor("Chrome", 0xFFBFC8CD)
        4 -> FanColor("Pastel mint", 0xFF9ACDBB)
        5 -> FanColor("Orange blades", 0xFFE18A45)
        6 -> FanColor("Blue blades", 0xFF77AED4)
        7 -> FanColor("Dark blue", 0xFF263F55)
        8 -> FanColor("Turquoise edge", 0xFF51B8C6)
        9, 18 -> FanColor("Porcelain white", 0xFFF1F2EE)
        10 -> FanColor("Violet LEDs", 0xFF9850EE)
        11 -> FanColor("Ivory paper", 0xFFECE4CF)
        12 -> FanColor("Golden straw", 0xFFCDB16B)
        15 -> FanColor("Brushed steel", 0xFF929A9D)
        16 -> FanColor("Warm yellow", 0xFFE8B93F)
        17 -> FanColor("Leaf green", 0xFF91BF88)
        19 -> FanColor("Red metal", 0xFFCD4235)
        20 -> FanColor("Purple trim", 0xFF8853AA)
        21 -> FanColor("Black metal", 0xFF303436)
        else -> ALL.first()
    }

    fun forFan(fan: FanType): List<FanColor> = listOf(originalFor(fan)) + ALL

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
