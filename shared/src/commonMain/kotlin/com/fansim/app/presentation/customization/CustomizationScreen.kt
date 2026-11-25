package com.fansim.app.presentation.customization

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import com.fansim.app.platform.CustomizationBack
import androidx.compose.ui.semantics.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import fansimulator.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fansim.app.domain.model.FanConfig
import com.fansim.app.presentation.localizedName
import com.fansim.app.domain.model.FanType
import com.fansim.app.domain.model.FanSpeedLevel
import com.fansim.app.presentation.components.FanFullScreenDialog
import com.fansim.app.presentation.components.rememberFanAnimationPhase
import com.fansim.app.presentation.fanselection.FanProductImage

@Composable
private fun rememberCustomizationViewModel(fanType: FanType): CustomizationViewModel {
    val defaultName = stringResource(Res.string.my_fan)
    return remember(fanType, defaultName) { CustomizationViewModel(fanType, defaultName) }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CustomizationScreen(
    fanType: FanType,
    viewModel: CustomizationViewModel = rememberCustomizationViewModel(fanType),
    beforeFullscreen: (() -> Unit) -> Unit = { it() },
    onBack: () -> Unit = {},
    onDone: (FanConfig) -> Unit
) {
    val state by viewModel.state.collectAsState()
    var fullScreen by rememberSaveable(fanType.id) { mutableStateOf(false) }
    if (fullScreen) {
        var speed by remember { mutableStateOf(FanSpeedLevel.OFF) }
        val phase by rememberFanAnimationPhase(fanType.id, speed.ordinal * 100f)
        FanFullScreenDialog(
            fan = fanType,
            speed = speed,
            animationPhase = phase,
            onPower = {
                speed = if (speed == FanSpeedLevel.OFF) FanSpeedLevel.LOW else FanSpeedLevel.OFF
            },
            onIncreaseSpeed = { speed = FanSpeedLevel.entries[speed.ordinal + 1] },
            onDecreaseSpeed = { speed = FanSpeedLevel.entries[speed.ordinal - 1] },
            canIncreaseSpeed = speed != FanSpeedLevel.HIGH,
            canDecreaseSpeed = speed != FanSpeedLevel.OFF,
            onClose = { fullScreen = false }
        )
        return
    }


    Column(
        modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()
            .imePadding().verticalScroll(rememberScrollState()).padding(24.dp)
    ) {
        CustomizationBack(onBack)
        Text(stringResource(Res.string.make_it_yours), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Text(
            text = stringResource(Res.string.customize_intro),
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        Box(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primaryContainer, androidx.compose.foundation.shape.RoundedCornerShape(28.dp)).padding(24.dp), contentAlignment = Alignment.Center) {
            FanProductImage(fanType, Modifier.size(180.dp).clickable(onClickLabel = stringResource(Res.string.open_full)) { beforeFullscreen { fullScreen = true } })
        }

        Text(stringResource(Res.string.finish, state.selectedColor.localizedName()), fontSize = 16.sp, modifier = Modifier.padding(top = 24.dp, bottom = 8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            state.colors.forEach { color ->
                val isSelected = color == state.selectedColor
                val colorName = color.localizedName()
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(color.hex), CircleShape)
                        .border(
                            width = if (isSelected) 3.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                            shape = CircleShape
                        )
                        .semantics { contentDescription = colorName }
                        .selectable(selected = isSelected, role = Role.RadioButton) { viewModel.selectColor(color) }
                )
            }
        }

        OutlinedTextField(
            value = state.name,
            onValueChange = viewModel::updateName,
            label = { Text(stringResource(Res.string.fan_name)) },
            singleLine = true,
            supportingText = { Text(stringResource(Res.string.characters, state.name.length, 20)) },
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
        )

        Button(
            onClick = { onDone(viewModel.buildConfig()) },
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp).heightIn(min = 56.dp)
        ) {
            Text(stringResource(Res.string.start_breeze))
        }
    }
}
