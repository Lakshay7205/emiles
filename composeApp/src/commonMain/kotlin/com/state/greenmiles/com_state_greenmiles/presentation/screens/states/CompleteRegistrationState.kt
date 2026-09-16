package com.state.greenmiles.com_state_greenmiles.presentation.screens.states

import com.state.greenmiles.com_state_greenmiles.domain.model.AuthResult

sealed interface CompleteRegistrationState {
    object Idle : CompleteRegistrationState
    object Loading : CompleteRegistrationState
    data class Success(val auth: AuthResult) : CompleteRegistrationState
    data class Error(val message: String) : CompleteRegistrationState
}

