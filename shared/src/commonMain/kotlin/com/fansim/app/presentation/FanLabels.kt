package com.fansim.app.presentation

import androidx.compose.runtime.Composable
import com.fansim.app.domain.model.*
import fansimulator.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun FanEra.localizedName() = stringResource(when (this) {
    FanEra.ANTIQUE -> Res.string.antique
    FanEra.CLASSIC -> Res.string.classic
    FanEra.MODERN -> Res.string.modern
    FanEra.DIGITAL -> Res.string.digital
})

@Composable
internal fun FanSpeedLevel.localizedName() = stringResource(when (this) {
    FanSpeedLevel.OFF -> Res.string.speed_off
    FanSpeedLevel.LOW -> Res.string.speed_low
    FanSpeedLevel.MEDIUM -> Res.string.speed_medium
    FanSpeedLevel.HIGH -> Res.string.speed_high
})

@Composable
internal fun FanType.localizedName(): String {
    val resource = when (id) {
        1 -> Res.string.catalog_fan_1
        2 -> Res.string.catalog_fan_2
        3 -> Res.string.catalog_fan_3
        4 -> Res.string.catalog_fan_4
        5 -> Res.string.catalog_fan_5
        6 -> Res.string.catalog_fan_6
        7 -> Res.string.catalog_fan_7
        8 -> Res.string.catalog_fan_8
        9 -> Res.string.catalog_fan_9
        10 -> Res.string.catalog_fan_10
        11 -> Res.string.catalog_fan_11
        12 -> Res.string.catalog_fan_12
        13 -> Res.string.catalog_fan_13
        14 -> Res.string.catalog_fan_14
        15 -> Res.string.catalog_fan_15
        16 -> Res.string.catalog_fan_16
        17 -> Res.string.catalog_fan_17
        18 -> Res.string.catalog_fan_18
        19 -> Res.string.catalog_fan_19
        20 -> Res.string.catalog_fan_20
        21 -> Res.string.catalog_fan_21
        else -> return displayName
    }
    return stringResource(resource)
}

@Composable
internal fun FanColor.localizedName(): String {
    val resource = when (hex) {
        0xFFF5F5F5L -> Res.string.finish_f5f5f5
        0xFF212121L -> Res.string.finish_212121
        0xFF1976D2L -> Res.string.finish_1976d2
        0xFFD32F2FL -> Res.string.finish_d32f2f
        0xFF43A047L -> Res.string.finish_43a047
        0xFF7B1FA2L -> Res.string.finish_7b1fa2
        0xFFFBC02DL -> Res.string.finish_fbc02d
        0xFFE0A899L -> Res.string.finish_e0a899
        0xFFEDE2CAL -> Res.string.finish_ede2ca
        0xFFB58B45L -> Res.string.finish_b58b45
        0xFF69402BL -> Res.string.finish_69402b
        0xFFBFC8CDL -> Res.string.finish_bfc8cd
        0xFF9ACDBBL -> Res.string.finish_9acdbb
        0xFFE18A45L -> Res.string.finish_e18a45
        0xFF77AED4L -> Res.string.finish_77aed4
        0xFF263F55L -> Res.string.finish_263f55
        0xFF51B8C6L -> Res.string.finish_51b8c6
        0xFFF1F2EEL -> Res.string.finish_f1f2ee
        0xFF9850EEL -> Res.string.finish_9850ee
        0xFFECE4CFL -> Res.string.finish_ece4cf
        0xFFCDB16BL -> Res.string.finish_cdb16b
        0xFF929A9DL -> Res.string.finish_929a9d
        0xFFE8B93FL -> Res.string.finish_e8b93f
        0xFF91BF88L -> Res.string.finish_91bf88
        0xFFCD4235L -> Res.string.finish_cd4235
        0xFF8853AAL -> Res.string.finish_8853aa
        0xFF303436L -> Res.string.finish_303436
        else -> return name
    }
    return stringResource(resource)
}
