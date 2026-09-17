package com.state.greenmiles.com_state_greenmiles.presentation.screens.googlemap

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cocoapods.GooglePlaces.GMSPlacesClient
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberPlacesClient(): Any {
    return remember { GMSPlacesClient.sharedClient() }
}
