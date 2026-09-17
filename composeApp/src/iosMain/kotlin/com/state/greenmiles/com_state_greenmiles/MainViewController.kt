package com.state.greenmiles.com_state_greenmiles

import androidx.compose.ui.window.ComposeUIViewController
import com.state.greenmiles.com_state_greenmiles.di.authModule
import com.state.greenmiles.com_state_greenmiles.di.common
import org.koin.core.context.startKoin

fun MainViewController() = ComposeUIViewController {
    startKoin {
        modules(
            common,
            authModule
        )
    }

    App()
}