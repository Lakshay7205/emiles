package com.state.greenmiles.com_state_greenmiles

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.libraries.places.api.Places
import com.state.greenmiles.com_state_greenmiles.presentation.screens.aadhaar_kyc.FilePicker
import org.koin.dsl.module
import org.koin.core.context.loadKoinModules

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        // FilePicker calls registerForActivityResult() in its constructor,
        // which MUST happen before the Activity reaches STARTED state.
        // So we create it here, before super.onCreate().
        val filePicker = FilePicker(this)

        loadKoinModules(module {
            single { filePicker }
        })

        Places.initialize(
            applicationContext,
            "AIzaSyDaLrzuDut0Elh4XTQS7_Tku0az7UKK3rM"
        )
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}