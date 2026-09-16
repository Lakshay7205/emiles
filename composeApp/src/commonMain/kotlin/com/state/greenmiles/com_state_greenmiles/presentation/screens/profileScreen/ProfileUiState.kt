package com.state.greenmiles.com_state_greenmiles.presentation.screens.profileScreen

import com.state.greenmiles.com_state_greenmiles.domain.model.UserProfile

data class ProfileUiState(
    val isLoading: Boolean = false,
    val profile: UserProfile? = null,
    val error: String? = null,
    val message: String? = null,
    val otpToken: String?=null
)