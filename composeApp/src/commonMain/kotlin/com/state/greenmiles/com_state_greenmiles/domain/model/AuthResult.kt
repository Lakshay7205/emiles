package com.state.greenmiles.com_state_greenmiles.domain.model

data class AuthResult(
    val userId: String,
    val token: String,
    val message: String
)

