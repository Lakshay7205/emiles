package com.state.greenmiles.com_state_greenmiles.presentation.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.state.greenmiles.com_state_greenmiles.domain.model.AuthUseCases
import com.state.greenmiles.com_state_greenmiles.presentation.screens.states.CompleteRegistrationState
import com.state.greenmiles.com_state_greenmiles.presentation.screens.states.LoginState
import com.state.greenmiles.com_state_greenmiles.presentation.screens.states.SendOtpState
import com.state.greenmiles.com_state_greenmiles.presentation.screens.states.VerifyRegisterOtpState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authUseCases: AuthUseCases
) : ViewModel() {


    var otpToken: String? = null
    var authType: String? = null
    var number: String? = null

    fun resetOtpStates() {
        _sendOtpState.value = SendOtpState.Idle
        _verifyRegisterOtpState.value = VerifyRegisterOtpState.Idle
        otpToken = null
    }

    private val _sendOtpState =
        MutableStateFlow<SendOtpState>(SendOtpState.Idle)
    val sendOtpState: StateFlow<SendOtpState> =
        _sendOtpState

    private val _verifyRegisterOtpState =
        MutableStateFlow<VerifyRegisterOtpState>(VerifyRegisterOtpState.Idle)
    val verifyRegisterOtpState: StateFlow<VerifyRegisterOtpState> =
        _verifyRegisterOtpState

    fun sendRegisterOtp(mobile: String) {
        println("🚀 sendRegisterOtp called with $mobile")

        execute(
            onLoading = {
                println("⏳ OTP Loading")
                _sendOtpState.value = SendOtpState.Loading
            },
            onError = {
                println("❌ OTP Error: $it")
                _sendOtpState.value = SendOtpState.Error(it)
            }
        ) {
            val result = authUseCases.sendRegisterOtp(mobile)
            println("✅ OTP API success")
            this.otpToken = result.otpToken
            _sendOtpState.value = SendOtpState.Success(result)
        }
    }

    fun sendForgotPasswordOtp(mobile: String) {
        println("🚀 sendForgotPasswordOtp called with $mobile")

        execute(
            onLoading = {
                _sendOtpState.value = SendOtpState.Loading
            },
            onError = {
                _sendOtpState.value = SendOtpState.Error(it)
            }
        ) {
            val result = authUseCases.sendForgotPasswordOtp(mobile)
            println("✅ Forgot Password OTP API success")
            this.otpToken = result.otpToken
            _sendOtpState.value = SendOtpState.Success(result)
        }
    }

    fun verifyRegisterOtp(otp: String) {
        val token = otpToken ?: return
        execute(
            onLoading = { _verifyRegisterOtpState.value = VerifyRegisterOtpState.Loading },
            onError = { _verifyRegisterOtpState.value = VerifyRegisterOtpState.Error(it) }
        ) {
            val result = authUseCases.verifyRegisterOtp(token, otp)
            _verifyRegisterOtpState.value = VerifyRegisterOtpState.Success(result)
        }
    }

    fun verifyForgotPasswordOtp(otp: String) {
        val token = otpToken ?: return
        execute(
            onLoading = { _verifyRegisterOtpState.value = VerifyRegisterOtpState.Loading },
            onError = { _verifyRegisterOtpState.value = VerifyRegisterOtpState.Error(it) }
        ) {
            val result = authUseCases.verifyForgotPasswordOtp(token, otp)
            _verifyRegisterOtpState.value = VerifyRegisterOtpState.Success(result)
        }
    }


    private val _completeRegistrationState =
        MutableStateFlow<CompleteRegistrationState>(CompleteRegistrationState.Idle)
    val completeRegistrationState: StateFlow<CompleteRegistrationState> =
        _completeRegistrationState

    fun completeRegistration(
        name: String,
        email: String,
        password: String,
        verifiedToken: String
    ) {
        execute(
            onLoading = { _completeRegistrationState.value = CompleteRegistrationState.Loading },
            onError = { _completeRegistrationState.value = CompleteRegistrationState.Error(it) }
        ) {
            val result = authUseCases.completeRegistration(
                name = name,
                email = email,
                password = password,
                verifiedToken = verifiedToken
            )
            
            _completeRegistrationState.value = CompleteRegistrationState.Success(result)
        }
    }

    private val _loginState =
        MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(mobile: String, password: String) {
        execute(
            onLoading = { _loginState.value = LoginState.Loading },
            onError = { _loginState.value = LoginState.Error(it) }
        ) {
            val result = authUseCases.login(mobile, password)
            _loginState.value = LoginState.Success(result)
        }
    }

    private fun execute(
        onLoading: () -> Unit,
        onError: (String) -> Unit,
        block: suspend () -> Unit
    ) {
        viewModelScope.launch {
            try {
                onLoading()
                block()
            } catch (e: Exception) {
                onError(e.message ?: "Something went wrong")
            }
        }
    }
}
