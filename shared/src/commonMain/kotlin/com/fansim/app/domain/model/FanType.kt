package com.fansim.app.domain.model

/**
 * The 10 selectable fan archetypes, ordered from oldest/analog to newest/digital,
 * as requested: "from very old FANs analogs to the latest fans that are digital".
 */
enum class FanEra { ANTIQUE, CLASSIC, MODERN, DIGITAL }

data class FanType(
    val id: Int,
    val displayName: String,
    val era: FanEra,
    val isDigital: Boolean,
    val bladeCount: Int,
    val description: String
)

object FanCatalog {
    val ALL: List<FanType> = listOf(
        FanType(1, "Hand Punkah (Antique Pull-Fan)", FanEra.ANTIQUE, false, 0, "A hand-pulled cloth fan from the 1800s."),
        FanType(2, "Brass Desk Fan (1910)", FanEra.ANTIQUE, false, 4, "Heavy cast-iron base with exposed brass blades."),
        FanType(3, "Art-Deco Oscillator (1930)", FanEra.CLASSIC, false, 4, "Chrome art-deco body with a caged guard."),
        FanType(4, "Mid-Century Table Fan (1950)", FanEra.CLASSIC, false, 3, "Pastel-colored plastic housing, single speed."),
        FanType(5, "Box Fan (1970)", FanEra.CLASSIC, false, 5, "Classic square box fan for the window."),
        FanType(6, "Pedestal Fan (1990)", FanEra.MODERN, false, 5, "Tall adjustable pedestal with 3 speeds."),
        FanType(7, "Tower Fan (2005)", FanEra.MODERN, false, 0, "Bladeless-looking slim tower oscillator."),
        FanType(8, "Bladeless Air Multiplier (2015)", FanEra.MODERN, true, 0, "Ring-shaped bladeless amplifier fan."),
        FanType(9, "Smart Wi-Fi Fan (2020)", FanEra.DIGITAL, true, 0, "App and voice-controlled smart fan."),
        FanType(10, "Holographic Digital Fan (Latest)", FanEra.DIGITAL, true, 0, "Fully digital fan with an LED display readout.")
    )
}
