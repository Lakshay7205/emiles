package com.state.greenmiles.com_state_greenmiles.presentation.screens.states

import com.state.greenmiles.com_state_greenmiles.domain.model.AuthResult

sealed interface LoginState {
    object Idle : LoginState
    object Loading : LoginState
    data class Success(val auth: AuthResult) : LoginState
    data class Error(val message: String) : LoginState
}
