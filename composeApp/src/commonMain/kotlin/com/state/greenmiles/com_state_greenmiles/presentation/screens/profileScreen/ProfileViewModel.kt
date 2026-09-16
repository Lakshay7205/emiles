package com.state.greenmiles.com_state_greenmiles.presentation.screens.profileScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.state.greenmiles.com_state_greenmiles.domain.repository.TripsRepository
import com.state.greenmiles.com_state_greenmiles.utils.LocalStorage
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: TripsRepository,
    private val localStorage: LocalStorage
) : ViewModel() {

    var uiState by mutableStateOf(ProfileUiState())
        private set
    val logger = Logger.withTag("InfoWith")
    private var mobileOtpToken: String? = null

    fun sendMobileOtp(newMobile: String) {
        viewModelScope.launch {
            try {
                val otpToken = repository.sendMobileOtp(newMobile)

                // 🔥 store automatically
                mobileOtpToken = otpToken

                uiState = uiState.copy(
                    message = "OTP sent successfully",
                    error = null
                )

            } catch (e: Exception) {
                uiState = uiState.copy(
                    error = e.message,
                    message = null
                )
            }
        }
    }
    fun verifyAndUpdateMobile(
        newMobile: String,
        otp: String,
        string: String
    ) {
        viewModelScope.launch {
            try {

                val token = mobileOtpToken
                    ?: throw IllegalStateException("OTP not requested")

                repository.verifyAndUpdateMobile(
                    newMobile = newMobile,
                    otp = otp,
                    otpToken = token
                )

                uiState = uiState.copy(
                    message = "Mobile updated successfully"
                )

                loadProfile()

            } catch (e: Exception) {
                uiState = uiState.copy(
                    error = e.message
                )
            }
        }
    }
    fun updateEmail(email: String) {
        viewModelScope.launch {
            try {
                repository.updateEmail(email)
                uiState = uiState.copy(
                    message = "Email updated successfully"
                )
                loadProfile()
            } catch (e: Exception) {
                uiState = uiState.copy(
                    error = e.message
                )
            }
        }
    }


    fun loadProfile() {
        val userId = localStorage.getUserId()

        if (userId == null) {
            uiState = uiState.copy(error = "User not logged in")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)

            try {
                val profile = repository.getUserProfile(userId)

                uiState = uiState.copy(
                    isLoading = false,
                    profile = profile
                )

            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
}