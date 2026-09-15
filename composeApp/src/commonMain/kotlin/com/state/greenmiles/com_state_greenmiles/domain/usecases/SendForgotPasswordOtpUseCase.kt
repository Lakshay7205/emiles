package com.state.greenmiles.com_state_greenmiles.domain.usecases

import com.state.greenmiles.com_state_greenmiles.domain.model.OtpResult
import com.state.greenmiles.com_state_greenmiles.domain.repository.AuthRepository

class SendForgotPasswordOtpUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(mobile: String): OtpResult {
        require(mobile.isNotBlank())
        return repository.sendForgotPasswordOtp(mobile)
    }
}
