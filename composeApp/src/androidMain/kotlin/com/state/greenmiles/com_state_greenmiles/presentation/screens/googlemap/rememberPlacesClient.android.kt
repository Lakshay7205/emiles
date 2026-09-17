package com.state.greenmiles.com_state_greenmiles.presentation.screens.googlemap

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.libraries.places.api.Places

@Composable
actual fun rememberPlacesClient(): Any {
    val context = LocalContext.current
    return remember {
        Places.createClient(context)
    }
}