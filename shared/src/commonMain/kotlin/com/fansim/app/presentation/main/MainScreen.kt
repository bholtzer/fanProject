package com.fansim.app.presentation.main

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.semantics.*
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import fansimulator.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fansim.app.presentation.components.FanFullScreenDialog
import com.fansim.app.presentation.components.InteractiveFanImage
import com.fansim.app.presentation.components.rememberFanAnimationPhase
import com.fansim.app.presentation.localizedName
import com.fansim.app.domain.model.FanSpeedLevel

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    beforeFullscreen: (() -> Unit) -> Unit = { it() },
    onOpenSettingsAgain: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val speed = state.speed.localizedName()
    val soundLabel = stringResource(Res.string.fan_sound)
    var animationPhase by rememberFanAnimationPhase(state.config.fanType.id, state.rotationDegreesPerSecond)
    val rotateFan: (Float) -> Unit = { delta ->
        animationPhase = (animationPhase + delta) % 360f
        viewModel.onSwipeDragged(delta)
    }
    var fullScreen by rememberSaveable(state.config.fanType.id) { mutableStateOf(false) }

    DisposableEffect(viewModel) {
        viewModel.start()
        onDispose { viewModel.stop() }
    }

    if (fullScreen) {
        FanFullScreenDialog(
            fan = state.config.fanType,
            animationPhase = animationPhase,
            onPower = viewModel::onPowerTapped,
            onIncreaseSpeed = viewModel::increaseSpeed,
            speed = state.speed,
            onDecreaseSpeed = viewModel::decreaseSpeed,
            canIncreaseSpeed = state.speed != FanSpeedLevel.HIGH,
            canDecreaseSpeed = state.speed != FanSpeedLevel.OFF,
            onClose = { fullScreen = false }
        )
        return
    }

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                state.config.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f).heightIn(min = 48.dp)
            )
            OutlinedButton(onClick = onOpenSettingsAgain) { Text(stringResource(Res.string.change_fan)) }
        }
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        Text(state.config.fanType.localizedName(), fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(50.dp), modifier = Modifier.padding(vertical = 12.dp)) {
            Text(if (state.speed == FanSpeedLevel.OFF) stringResource(Res.string.standby) else stringResource(Res.string.running, speed),
                style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
                .height(280.dp)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = .45f), RoundedCornerShape(28.dp))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            InteractiveFanImage(
                fan = state.config.fanType,
                isRunning = state.speed != FanSpeedLevel.OFF,
                animationPhase = animationPhase,
                modifier = Modifier.size(240.dp),
                onClickLabel = stringResource(Res.string.open_full),
                onClick = { beforeFullscreen { fullScreen = true } },
                onRotate = rotateFan
            )
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(stringResource(Res.string.airflow), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(speed, color = MaterialTheme.colorScheme.primary)
        }
        LinearProgressIndicator(progress = { state.speed.ordinal / FanSpeedLevel.HIGH.ordinal.toFloat() },
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 16.dp),
            color = MaterialTheme.colorScheme.primary, trackColor = MaterialTheme.colorScheme.surfaceVariant)
        Button(onClick = viewModel::onPowerTapped, modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)) {
            Text(if (state.speed == FanSpeedLevel.OFF) stringResource(Res.string.turn_on) else stringResource(Res.string.turn_off))
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = viewModel::decreaseSpeed,
                enabled = state.speed != FanSpeedLevel.OFF,
                modifier = Modifier.weight(1f).heightIn(min = 48.dp)
            ) { Text(stringResource(Res.string.slower)) }
            OutlinedButton(
                onClick = viewModel::increaseSpeed,
                enabled = state.speed != FanSpeedLevel.HIGH,
                modifier = Modifier.weight(1f).heightIn(min = 48.dp)
            ) { Text(stringResource(Res.string.faster)) }
        }
        Row(modifier = Modifier.fillMaxWidth().padding(top = 20.dp).background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp)).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(soundLabel, style = MaterialTheme.typography.titleSmall)
                Text(if (state.isSoundEnabled) stringResource(Res.string.sound_running) else stringResource(Res.string.sound_quiet),
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(checked = state.isSoundEnabled, onCheckedChange = viewModel::setSoundEnabled, modifier = Modifier.semantics { contentDescription = soundLabel })
        }
        Text(
            text = when (state.speed) {
                FanSpeedLevel.OFF -> stringResource(Res.string.pickup_hint)
                else -> stringResource(
                    Res.string.fan_status,
                    speed,
                    stringResource(if (state.isSoundEnabled) Res.string.sound_on else Res.string.sound_off),
                    stringResource(
                        if (state.isNear || state.speed == FanSpeedLevel.HIGH) Res.string.vibration_on
                        else Res.string.vibration_pickup
                    )
                )
            },
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
        )
        }
    }
}
