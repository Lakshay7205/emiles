package com.state.greenmiles.com_state_greenmiles.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AuthDto(
    val userId: String? = null,
    val token: String? = null,
    val message: String? = null
)
