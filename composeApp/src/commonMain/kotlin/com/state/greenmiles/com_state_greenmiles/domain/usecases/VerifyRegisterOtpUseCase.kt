package com.state.greenmiles.com_state_greenmiles.domain.usecases

import com.state.greenmiles.com_state_greenmiles.domain.model.TempTokenResult
import com.state.greenmiles.com_state_greenmiles.domain.repository.AuthRepository

class VerifyRegisterOtpUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        otpToken: String,
        otp: String
    ): TempTokenResult {
        require(otpToken.isNotBlank()) { "OTP token cannot be blank" }
        require(otp.isNotBlank()) { "OTP cannot be blank" }

        return repository.verifyRegisterOtp(otpToken, otp)
    }
}
