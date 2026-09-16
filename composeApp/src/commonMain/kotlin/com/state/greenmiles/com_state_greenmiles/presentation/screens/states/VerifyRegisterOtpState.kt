package com.state.greenmiles.com_state_greenmiles.presentation.screens.states

import com.state.greenmiles.com_state_greenmiles.domain.model.TempTokenResult

sealed interface VerifyRegisterOtpState {
    object Idle : VerifyRegisterOtpState
    object Loading : VerifyRegisterOtpState
    data class Success(val result: TempTokenResult) : VerifyRegisterOtpState
    data class Error(val message: String) : VerifyRegisterOtpState
}
