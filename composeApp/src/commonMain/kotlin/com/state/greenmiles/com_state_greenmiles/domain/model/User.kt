package com.state.greenmiles.com_state_greenmiles.domain.model

data class User(
    val id: String,
    val name: String,
    val mobile: String,
    val email: String?,
    val isVerified: Boolean,
    val createdAt: String?

)
