package com.state.greenmiles.com_state_greenmiles.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SendMobileOtpRequest(
    val newMobile: String,
    val otpToken: String? = null
)