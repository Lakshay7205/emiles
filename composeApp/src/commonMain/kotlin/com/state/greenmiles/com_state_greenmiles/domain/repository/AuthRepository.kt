package com.state.greenmiles.com_state_greenmiles.domain.repository

import com.state.greenmiles.com_state_greenmiles.domain.model.AuthResult
import com.state.greenmiles.com_state_greenmiles.domain.model.OtpResult
import com.state.greenmiles.com_state_greenmiles.domain.model.TempTokenResult

interface AuthRepository {

    suspend fun sendRegisterOtp(mobile: String): OtpResult

    suspend fun verifyRegisterOtp(
        otpToken: String,
        otp: String
    ): TempTokenResult

    suspend fun completeRegistration(
        name: String,
        email: String,
        password: String,
        verifiedToken: String
    ): AuthResult

    suspend fun login(
        mobile: String,
        password: String
    ): AuthResult

    suspend fun sendForgotPasswordOtp(mobile: String): OtpResult

    suspend fun verifyForgotPasswordOtp(
        otpToken: String,
        otp: String
    ): TempTokenResult

    suspend fun resetPassword(
        tempToken: String,
        newPassword: String
    )
}
