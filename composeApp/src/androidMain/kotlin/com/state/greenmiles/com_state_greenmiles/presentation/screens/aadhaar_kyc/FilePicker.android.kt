package com.state.greenmiles.com_state_greenmiles.presentation.screens.aadhaar_kyc

import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts

actual class FilePicker(
    private val activity: ComponentActivity
) {

    private val launcher =
        activity.registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->

            uri?.let {
                val bytes = activity.contentResolver
                    .openInputStream(it)!!
                    .readBytes()

                onResult?.invoke(bytes)
            }
        }

    private var onResult: ((ByteArray) -> Unit)? = null

    actual fun pickFile(onFilePicked: (ByteArray) -> Unit) {
        onResult = onFilePicked
        launcher.launch("application/zip")
    }
}