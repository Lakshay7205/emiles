package com.state.greenmiles.com_state_greenmiles.presentation.screens.forgot_password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.state.greenmiles.com_state_greenmiles.domain.model.AuthUseCases
import com.state.greenmiles.com_state_greenmiles.presentation.screens.states.ForgotPasswordUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val authUseCases: AuthUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow<ForgotPasswordUiState>(ForgotPasswordUiState.Idle)
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState

    var mobile: String = ""
    private var tempToken: String? = null

    fun initializeWithVerifiedToken(mobile: String, verifiedToken: String) {
        this.mobile = mobile
        this.tempToken = verifiedToken
        _uiState.value = ForgotPasswordUiState.Idle
    }

    fun resetPassword(newPassword: String) {
        val token = tempToken ?: return
        viewModelScope.launch {
            try {
                _uiState.value = ForgotPasswordUiState.Loading
                authUseCases.resetPassword(token, newPassword)
                _uiState.value = ForgotPasswordUiState.Success
            } catch (e: Exception) {
                _uiState.value = ForgotPasswordUiState.Error(
                    message = e.message ?: "Something went wrong",
                    lastState = ForgotPasswordUiState.Idle
                )
            }
        }
    }
}
