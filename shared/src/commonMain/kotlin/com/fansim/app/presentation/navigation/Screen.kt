package com.fansim.app.presentation.navigation

sealed class Screen {
    data object Splash : Screen()
    data object FanSelection : Screen()
    data object Customization : Screen()
    data object Main : Screen()
}
