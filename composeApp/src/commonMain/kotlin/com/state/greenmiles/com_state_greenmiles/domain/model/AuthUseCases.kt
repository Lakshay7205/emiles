package com.state.greenmiles.com_state_greenmiles.domain.model

import com.state.greenmiles.com_state_greenmiles.domain.usecases.CompleteRegistrationUseCase
import com.state.greenmiles.com_state_greenmiles.domain.usecases.LoginUseCase
import com.state.greenmiles.com_state_greenmiles.domain.usecases.ResetPasswordUseCase
import com.state.greenmiles.com_state_greenmiles.domain.usecases.SendForgotPasswordOtpUseCase
import com.state.greenmiles.com_state_greenmiles.domain.usecases.SendRegisterOtpUseCase
import com.state.greenmiles.com_state_greenmiles.domain.usecases.VerifyForgotPasswordOtpUseCase
import com.state.greenmiles.com_state_greenmiles.domain.usecases.VerifyRegisterOtpUseCase

data class AuthUseCases(
    val sendRegisterOtp: SendRegisterOtpUseCase,
    val verifyRegisterOtp: VerifyRegisterOtpUseCase,
    val completeRegistration: CompleteRegistrationUseCase,
    val login: LoginUseCase,
    val sendForgotPasswordOtp: SendForgotPasswordOtpUseCase,
    val verifyForgotPasswordOtp: VerifyForgotPasswordOtpUseCase,
    val resetPassword: ResetPasswordUseCase
)
