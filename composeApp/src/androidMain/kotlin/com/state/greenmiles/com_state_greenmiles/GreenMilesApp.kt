package com.state.greenmiles.com_state_greenmiles

import android.app.Application
import com.state.greenmiles.com_state_greenmiles.di.authModule
import com.state.greenmiles.com_state_greenmiles.di.common
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class GreenMilesApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@GreenMilesApp)
            modules(
                common,
                authModule
            )
        }
    }
}
