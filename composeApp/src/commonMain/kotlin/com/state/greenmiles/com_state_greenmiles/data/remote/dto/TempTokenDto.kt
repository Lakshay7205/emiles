package com.state.greenmiles.com_state_greenmiles.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TempTokenDto(
    val success: Boolean,
    val message: String,
    val tempToken: String? = null,
    val verifiedToken: String? = null,
    val expiresIn: Int,
    val mobile: String? = null
)
