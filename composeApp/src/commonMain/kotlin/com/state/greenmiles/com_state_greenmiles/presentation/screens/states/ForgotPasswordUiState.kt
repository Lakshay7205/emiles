package com.state.greenmiles.com_state_greenmiles.presentation.screens.states

import com.state.greenmiles.com_state_greenmiles.domain.model.OtpResult
import com.state.greenmiles.com_state_greenmiles.domain.model.TempTokenResult

sealed class ForgotPasswordUiState {
    object Idle : ForgotPasswordUiState()
    object Loading : ForgotPasswordUiState()
    
    // Step 1: OTP Sent
    data class OtpSent(val result: OtpResult) : ForgotPasswordUiState()
    
    // Step 2: OTP Verified
    data class OtpVerified(val result: TempTokenResult) : ForgotPasswordUiState()
    
    // Step 3: Password Reset Success
    object Success : ForgotPasswordUiState()
    
    data class Error(val message: String, val lastState: ForgotPasswordUiState) : ForgotPasswordUiState()
}
