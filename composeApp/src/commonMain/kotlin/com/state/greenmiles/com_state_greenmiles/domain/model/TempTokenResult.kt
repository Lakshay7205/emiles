package com.state.greenmiles.com_state_greenmiles.domain.model

data class TempTokenResult(
    val message: String,
    val tempToken: String,
    val expiresIn: Int
)