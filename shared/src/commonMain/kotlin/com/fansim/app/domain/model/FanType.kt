package com.fansim.app.domain.model

/**
 * Fan archetypes ordered from oldest/analog to newest/digital.
 */
enum class FanEra { ANTIQUE, CLASSIC, MODERN, DIGITAL }

data class FanType(
    val id: Int,
    val displayName: String,
    val era: FanEra,
    val isDigital: Boolean,
    val bladeCount: Int,
    val description: String,
    /** Resource key for the current product image; independent of saved fan IDs. */
    val imageKey: String
)

object FanCatalog {
    val ALL: List<FanType> = listOf(
        FanType(11, "Folding Hand Fan (Ancient)", FanEra.ANTIQUE, false, 0, "A folding handheld fan inspired by early designs.", imageKey = "fan_product_11"),
        FanType(12, "Palm Leaf Fan (Ancient)", FanEra.ANTIQUE, false, 0, "A simple woven palm fan.", imageKey = "fan_product_12"),
        FanType(1, "Hand Punkah (Antique Pull-Fan)", FanEra.ANTIQUE, false, 0, "A hand-pulled cloth fan from the 1800s.", imageKey = "fan_product_1"),
        FanType(13, "Victorian Ceiling Fan (1880)", FanEra.ANTIQUE, false, 4, "A belt-driven ceiling fan from the early electric age.", imageKey = "fan_product_13"),
        FanType(2, "Brass Desk Fan (1910)", FanEra.ANTIQUE, false, 4, "Heavy cast-iron base with exposed brass blades.", imageKey = "fan_product_2"),
        FanType(14, "Railway Carriage Fan (1920)", FanEra.ANTIQUE, false, 3, "A compact vintage fan for railway cabins.", imageKey = "fan_product_14"),
        FanType(3, "Art-Deco Oscillator (1930)", FanEra.CLASSIC, false, 4, "Chrome art-deco body with a caged guard.", imageKey = "fan_product_3"),
        FanType(15, "Industrial Metal Fan (1940)", FanEra.CLASSIC, false, 6, "A large metal workshop fan.", imageKey = "fan_product_15"),
        FanType(4, "Mid-Century Table Fan (1950)", FanEra.CLASSIC, false, 3, "Pastel-colored plastic housing, single speed.", imageKey = "fan_product_4"),
        FanType(5, "Box Fan (1970)", FanEra.CLASSIC, false, 5, "Classic square box fan for the window.", imageKey = "fan_product_5"),
        FanType(6, "Pedestal Fan (1990)", FanEra.MODERN, false, 5, "Tall adjustable pedestal with 3 speeds.", imageKey = "fan_product_6"),
        FanType(7, "Tower Fan (2005)", FanEra.MODERN, false, 0, "Bladeless-looking slim tower oscillator.", imageKey = "fan_product_7"),
        FanType(16, "USB Mini Fan (2010)", FanEra.MODERN, false, 3, "A small portable desk fan.", imageKey = "fan_product_16"),
        FanType(19, "ROTAKE Floor Fan", FanEra.MODERN, false, 3, "A red floor fan with a black tubular stand.", imageKey = "fan_product_19"),
        FanType(20, "Grey & Purple Desk Fan", FanEra.MODERN, false, 3, "A compact grey desk fan with a purple rim.", imageKey = "fan_product_20"),
        FanType(21, "Air King Wall Fan", FanEra.MODERN, false, 3, "A black three-blade air circulator with a metal grille.", imageKey = "fan_product_21"),
        FanType(8, "Bladeless Air Multiplier (2015)", FanEra.MODERN, true, 0, "Ring-shaped bladeless amplifier fan.", imageKey = "fan_product_8"),
        FanType(9, "Smart Wi-Fi Fan (2020)", FanEra.DIGITAL, true, 7, "App and voice-controlled smart fan.", imageKey = "fan_product_9"),
        FanType(17, "Rechargeable Clip Fan (2024)", FanEra.DIGITAL, true, 5, "A portable fan with a rechargeable battery.", imageKey = "fan_product_17"),
        FanType(18, "Air Purifying Smart Fan (2026)", FanEra.DIGITAL, true, 0, "A connected fan with air filtering controls.", imageKey = "fan_product_18"),
        FanType(10, "Holographic Digital Fan (Latest)", FanEra.DIGITAL, true, 4, "Fully digital fan with an LED display readout.", imageKey = "fan_product_10")
    )
}
