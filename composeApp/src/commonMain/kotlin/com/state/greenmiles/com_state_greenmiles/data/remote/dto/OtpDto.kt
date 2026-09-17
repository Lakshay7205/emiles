package com.state.greenmiles.com_state_greenmiles.data.remote.dto

@kotlinx.serialization.Serializable
data class OtpDto(
    val success: Boolean,
    val message: String,
    val mobile: String? = null,
    val otpToken: String,
    val expiresIn: Int
)
