package com.state.greenmiles.com_state_greenmiles.domain.model

data class OtpResult(
    val message: String,
    val otpToken: String,
    val expiresIn: Int
)
