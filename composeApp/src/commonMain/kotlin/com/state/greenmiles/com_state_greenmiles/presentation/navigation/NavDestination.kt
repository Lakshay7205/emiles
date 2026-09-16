package com.state.greenmiles.com_state_greenmiles.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

enum class NavDestination(
    val icon: ImageVector,
    val label: String
) {
    HOME(Icons.Outlined.Home, "Home"),
    TRIPS(Icons.Outlined.DirectionsCar, "My Trips"),
    PROFILE(Icons.Outlined.Person, "Profile")
}