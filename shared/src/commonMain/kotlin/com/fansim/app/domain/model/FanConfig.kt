package com.fansim.app.domain.model

/** The user's fully customised fan: chosen type, color, and a personal name. */
data class FanConfig(
    val fanType: FanType,
    val fanColor: FanColor,
    val name: String
) {
    companion object {
        fun default() = FanConfig(
            fanType = FanCatalog.ALL.first(),
            fanColor = FanColorCatalog.ALL.first(),
            name = "My Fan"
        )
    }
}

/** 0 = off, 1..3 = increasing speed levels. */
enum class FanSpeedLevel(val level: Int, val rpmMultiplier: Float) {
    OFF(0, 0f),
    LOW(1, 1f),
    MEDIUM(2, 2f),
    HIGH(3, 3.2f);

    fun next(): FanSpeedLevel = entries.getOrElse(ordinal + 1) { OFF }
}
