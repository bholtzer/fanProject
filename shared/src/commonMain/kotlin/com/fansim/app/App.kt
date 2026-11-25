package com.fansim.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.lightColorScheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import com.fansim.app.presentation.navigation.ScreenSaver
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.fansim.app.di.AppContainer
import com.fansim.app.domain.model.FanConfig
import com.fansim.app.domain.model.FanCatalog
import com.fansim.app.presentation.navigation.FanConfigSaver
import com.fansim.app.presentation.navigation.FanSelectionSaver
import com.fansim.app.presentation.customization.CustomizationScreen
import com.fansim.app.presentation.fanselection.FanSelectionScreen
import com.fansim.app.presentation.fanselection.FanSelectionViewModel
import com.fansim.app.presentation.fanselection.LegalDocument
import com.fansim.app.presentation.main.MainScreen
import com.fansim.app.presentation.main.MainViewModel
import com.fansim.app.presentation.navigation.Screen
import com.fansim.app.presentation.splash.SplashScreen
import kotlinx.coroutines.launch

/**
 * Root composable shared by both androidApp and iosApp. Handles the full flow:
 * Splash -> pick fan type -> customize color/name -> Main.
 */
@Composable
fun App(
    container: AppContainer,
    languageScreen: (@Composable (() -> Unit) -> Unit)? = null,
    onOpenLegalDocument: ((LegalDocument) -> Unit)? = null
) {
    var screen by rememberSaveable(stateSaver = ScreenSaver) { mutableStateOf<Screen>(Screen.Splash) }
    var choosingLanguage by rememberSaveable { mutableStateOf(false) }
    var showingAd by remember { mutableStateOf(false) }
    val withInterstitial: (() -> Unit) -> Unit = { proceed ->
        if (!showingAd) {
            showingAd = true
            container.adManager.showInterstitial {
                showingAd = false
                proceed()
            }
        }
    }
    var chosenFanId by rememberSaveable { mutableStateOf<Int?>(null) }
    var fanConfig by rememberSaveable(stateSaver = androidx.compose.runtime.saveable.Saver<FanConfig?, Any>(
        save = { config -> config?.let { with(FanConfigSaver) { save(it) } } },
        restore = FanConfigSaver::restore
    )) { mutableStateOf<FanConfig?>(null) }
    val scope = rememberCoroutineScope()
    val fanSelectionViewModel = rememberSaveable(saver = FanSelectionSaver) { FanSelectionViewModel() }

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF007D79), onPrimary = Color.White,
            primaryContainer = Color(0xFFD1F3EC), onPrimaryContainer = Color(0xFF003D39),
            secondary = Color(0xFF526B78), background = Color(0xFFF5F8FA),
            surface = Color.White, onSurface = Color(0xFF182D38),
            onSurfaceVariant = Color(0xFF546873), outline = Color(0xFF788B95),
            surfaceVariant = Color(0xFFE8EFF2)
        ),
        shapes = Shapes(small = RoundedCornerShape(12.dp), medium = RoundedCornerShape(20.dp), large = RoundedCornerShape(28.dp))
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            if (choosingLanguage && languageScreen != null) {
                languageScreen { choosingLanguage = false }
                return@Surface
            }
            when (screen) {
                Screen.Splash -> SplashScreen(
                    onFinished = {
                        // Every fresh launch begins with choosing a fan, even when a choice was saved.
                        screen = Screen.FanSelection
                    }
                )

                Screen.FanSelection -> FanSelectionScreen(
                    viewModel = fanSelectionViewModel,
                    onOpenLanguage = languageScreen?.let { { choosingLanguage = true } },
                    onOpenLegalDocument = onOpenLegalDocument,
                    onFanChosen = { fanType ->
                        chosenFanId = fanType.id
                        screen = Screen.Customization
                    }
                )

                Screen.Customization -> {
                    val type = FanCatalog.ALL.firstOrNull { it.id == chosenFanId }
                    if (type != null) {
                        CustomizationScreen(
                            fanType = type,
                            beforeFullscreen = withInterstitial,
                            onBack = { screen = Screen.FanSelection },
                            onDone = { config ->
                                fanConfig = config
                                scope.launch { container.saveFanConfig(config) }
                                screen = Screen.Main
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
                                initialSoundEnabled = container.soundEnabled,
                                onSoundEnabledChanged = container::setSoundEnabled,
                                vibrationController = container.vibrationController,
                                proximitySensorController = container.proximitySensorController,
                                soundPlayer = container.soundPlayer
                            )
                        }
                        MainScreen(
                            viewModel = viewModel,
                            beforeFullscreen = withInterstitial,
                            onOpenSettingsAgain = {
                                withInterstitial { screen = Screen.FanSelection }
                            }
                        )
                    }
                }
            }
        }
    }
}
