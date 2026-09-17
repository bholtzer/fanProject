package com.fansim.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.fansim.app.di.AppContainer
import com.fansim.app.domain.model.FanConfig
import com.fansim.app.domain.model.FanType
import com.fansim.app.presentation.customization.CustomizationScreen
import com.fansim.app.presentation.fanselection.FanSelectionScreen
import com.fansim.app.presentation.main.MainScreen
import com.fansim.app.presentation.main.MainViewModel
import com.fansim.app.presentation.navigation.Screen
import com.fansim.app.presentation.splash.SplashScreen
import kotlinx.coroutines.launch

/**
 * Root composable shared by both androidApp and iosApp. Handles the full flow:
 * Splash -> pick fan type -> customize color/name -> (show ad once) -> Main.
 */
@Composable
fun App(container: AppContainer) {
    var screen by remember { mutableStateOf<Screen>(Screen.Splash) }
    var chosenFanType by remember { mutableStateOf<FanType?>(null) }
    var fanConfig by remember { mutableStateOf<FanConfig?>(null) }
    val scope = rememberCoroutineScope()

    // Restore a previously saved fan (skips onboarding on subsequent launches).
    LaunchedEffect(Unit) {
        container.getFanConfig().collect { saved ->
            if (saved != null && screen == Screen.Splash && fanConfig == null) {
                fanConfig = saved
            }
        }
    }

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            when (val current = screen) {
                Screen.Splash -> SplashScreen(
                    onFinished = {
                        screen = if (fanConfig != null) Screen.Main else Screen.FanSelection
                    }
                )

                Screen.FanSelection -> FanSelectionScreen(
                    onFanChosen = { fanType ->
                        chosenFanType = fanType
                        screen = Screen.Customization
                    }
                )

                Screen.Customization -> {
                    val type = chosenFanType
                    if (type != null) {
                        CustomizationScreen(
                            fanType = type,
                            onDone = { config ->
                                fanConfig = config
                                scope.launch { container.saveFanConfig(config) }
                                // Ads run right after the user finishes first-launch setup.
                                scope.launch {
                                    val alreadyShown = container.observeFirstLaunchAdsShown()
                                    var shown = false
                                    alreadyShown.collect { value ->
                                        if (!shown) {
                                            shown = true
                                            if (!value) {
                                                container.adManager.showInterstitial {
                                                    scope.launch { container.markFirstLaunchAdsShown() }
                                                    screen = Screen.Main
                                                }
                                            } else {
                                                screen = Screen.Main
                                            }
                                        }
                                    }
                                }
                            }
                        )
                    }
                }

                Screen.Main -> {
                    val config = fanConfig
                    if (config != null) {
                        val viewModel = remember(config) {
                            MainViewModel(
                                initialConfig = config,
                                vibrationController = container.vibrationController,
                                proximitySensorController = container.proximitySensorController,
                                soundPlayer = container.soundPlayer
                            )
                        }
                        MainScreen(viewModel = viewModel, onOpenSettingsAgain = { screen = Screen.FanSelection })
                    }
                }
            }
        }
    }
}
