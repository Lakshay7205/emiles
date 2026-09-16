package com.state.greenmiles.com_state_greenmiles.Navigation

import androidx.compose.runtime.Composable
import com.state.greenmiles.com_state_greenmiles.presentation.screens.phone_number_screen.PhoneNumberScreen
import com.state.greenmiles.com_state_greenmiles.presentation.screens.splash_screen.SplashScreen
import com.state.greenmiles.com_state_greenmiles.presentation.screens.welcome_screen.WelcomeScreen

sealed class NavRoute {
    data object Splash : NavRoute()
    data object Login : NavRoute()
    data object Main : NavRoute()   // Contains BottomBar
    data object FindRide : NavRoute()
    data object PublishRide : NavRoute()

    data object Welcome: NavRoute()


}
