package com.state.greenmiles.com_state_greenmiles.presentation.screens.aadhaar_kyc

expect class FilePicker {

    fun pickFile(onFilePicked: (ByteArray) -> Unit)
}