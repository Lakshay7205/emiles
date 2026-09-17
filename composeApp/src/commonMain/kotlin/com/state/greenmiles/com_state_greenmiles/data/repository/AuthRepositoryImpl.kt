package com.state.greenmiles.com_state_greenmiles.data.repository

import co.touchlab.kermit.Logger
import com.state.greenmiles.com_state_greenmiles.data.mappers.toDomain
import com.state.greenmiles.com_state_greenmiles.data.remote.api.*
import com.state.greenmiles.com_state_greenmiles.domain.model.AuthResult
import com.state.greenmiles.com_state_greenmiles.domain.model.OtpResult
import com.state.greenmiles.com_state_greenmiles.domain.model.TempTokenResult
import com.state.greenmiles.com_state_greenmiles.domain.repository.AuthRepository
import com.state.greenmiles.com_state_greenmiles.utils.LocalStorage

class AuthRepositoryImpl(
    private val api: AuthApi,
    private val localStorage: LocalStorage

) : AuthRepository {
    val logger = Logger.withTag("InfoWith")
    override suspend fun sendRegisterOtp(mobile: String): OtpResult {
        val response = api.sendRegisterOtp(mobile)

        if (!response.success || response.data == null) {
            throw IllegalStateException(response.message ?: "Failed to send OTP")
        }

        return response.data.toDomain()
    }

    override suspend fun verifyRegisterOtp(
        otpToken: String,
        otp: String
    ): TempTokenResult {
        val response = api.verifyRegisterOtp(
            VerifyOtpRequest(otpToken = otpToken, otp = otp)
        )

        if (!response.success || response.data == null) {
            throw IllegalStateException(response.message ?: "OTP verification failed")
        }

        return response.data.toDomain()
    }

    override suspend fun completeRegistration(
        name: String,
        email: String,
        password: String,
        verifiedToken: String
    ): AuthResult {
        val response = api.completeRegistration(
            CompleteVerifiedRequest(
                name = name,
                email = email,
                password = password,
                verifiedToken = verifiedToken
            )
        )

        if (!response.success || response.data == null) {
            throw IllegalStateException(response.message ?: "Registration failed")
        }

        return response.data.toDomain()
    }

    override suspend fun login(
        mobile: String,
        password: String
    ): AuthResult {
        val response = api.login(LoginRequest(mobile, password))


        if (!response.success || response.data == null) {
            throw IllegalStateException(response.message ?: "Login failed")
        }

        val data = response.data

        if (data.userId == null || data.token == null) {
            throw IllegalStateException("Invalid login response")
        }

        localStorage.saveToken(data.token)
        localStorage.saveUserId(data.userId)
        logger.d { "After saving userId = ${localStorage.getUserId()}" }

        return data.toDomain()
    }
    override suspend fun sendForgotPasswordOtp(mobile: String): OtpResult {
        val response = api.sendForgotPasswordOtp(mobile)

        if (!response.success || response.data == null) {
            throw IllegalStateException(response.message ?: "Failed to send OTP")
        }

        return response.data.toDomain()
    }

    override suspend fun verifyForgotPasswordOtp(
        otpToken: String,
        otp: String
    ): TempTokenResult {
        val response = api.verifyForgotPasswordOtp(
            VerifyOtpRequest(otpToken = otpToken, otp = otp)
        )

        if (!response.success || response.data == null) {
            throw IllegalStateException(response.message ?: "OTP verification failed")
        }

        return response.data.toDomain()
    }

    override suspend fun resetPassword(
        tempToken: String,
        newPassword: String
    ) {
        val response = api.resetPassword(
            ResetPasswordRequest(tempToken, newPassword)
        )

        if (!response.success) {
            throw IllegalStateException(response.message ?: "Password reset failed")
        }
    }
}
