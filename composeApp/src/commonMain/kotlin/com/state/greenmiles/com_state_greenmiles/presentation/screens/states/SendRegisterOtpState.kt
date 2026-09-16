package com.state.greenmiles.com_state_greenmiles.presentation.screens.states

import com.state.greenmiles.com_state_greenmiles.domain.model.OtpResult

sealed interface SendOtpState {
    object Idle : SendOtpState
    object Loading : SendOtpState
    data class Success(val otp: OtpResult) : SendOtpState
    data class Error(val message: String) : SendOtpState
}

