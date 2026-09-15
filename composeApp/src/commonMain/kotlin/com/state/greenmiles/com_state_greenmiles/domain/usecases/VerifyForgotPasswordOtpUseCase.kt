package com.state.greenmiles.com_state_greenmiles.domain.usecases

import com.state.greenmiles.com_state_greenmiles.domain.model.TempTokenResult
import com.state.greenmiles.com_state_greenmiles.domain.repository.AuthRepository

class VerifyForgotPasswordOtpUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        otpToken: String,
        otp: String
    ): TempTokenResult {

        require(otpToken.isNotBlank())
        require(otp.isNotBlank())

        return repository.verifyForgotPasswordOtp(
            otpToken = otpToken,
            otp = otp
        )
    }
}
