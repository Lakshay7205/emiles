package com.state.greenmiles.com_state_greenmiles.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class VerifyMobileRequest(
    val newMobile: String,
    val otp: String,
    val otpToken: String
)