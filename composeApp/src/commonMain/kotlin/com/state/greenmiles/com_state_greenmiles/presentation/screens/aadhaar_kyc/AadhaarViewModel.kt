package com.state.greenmiles.com_state_greenmiles.presentation.screens.aadhaar_kyc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.state.greenmiles.com_state_greenmiles.domain.usecases.UploadAadhaarXmlUseCase
import com.state.greenmiles.com_state_greenmiles.domain.usecases.VerifyAadhaarUseCase
import com.state.greenmiles.com_state_greenmiles.domain.usecases.ConfirmProfileUpdateUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AadhaarViewModel(
    private val uploadUseCase: UploadAadhaarXmlUseCase,
    private val verifyUseCase: VerifyAadhaarUseCase,
    private val confirmUpdateUseCase: ConfirmProfileUpdateUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AadhaarUiState())
    val state: StateFlow<AadhaarUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<AadhaarUiEffect>()
    val effect: SharedFlow<AadhaarUiEffect> = _effect.asSharedFlow()

    fun uploadFile(fileBytes: ByteArray) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, uploadProgress = true, error = null) }
            
            try {
                val path = uploadUseCase(fileBytes)
                _state.update { 
                    it.copy(
                        isLoading = false,
                        uploadProgress = false,
                        s3Path = path,
                        step = AadhaarStep.VERIFY
                    )
                }
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Upload failed"
                _state.update { it.copy(isLoading = false, uploadProgress = false, error = errorMessage) }
                _effect.emit(AadhaarUiEffect.ShowError(errorMessage))
            }
        }
    }

    fun verifyPin(pin: String) {
        val s3Path = _state.value.s3Path ?: return
        
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, verificationProgress = true, error = null) }
            
            try {
                val kyc = verifyUseCase(s3Path, pin)
                _state.update { 
                    it.copy(
                        isLoading = false,
                        verificationProgress = false,
                        kycDetails = kyc,
                        step = AadhaarStep.CONFIRM_PROFILE
                    )
                }
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Verification failed"
                _state.update { it.copy(isLoading = false, verificationProgress = false, error = errorMessage) }
                _effect.emit(AadhaarUiEffect.ShowError(errorMessage))
            }
        }
    }

    fun confirmProfileUpdate() {
        val kyc = _state.value.kycDetails ?: return
        
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, isUpdatingProfile = true, error = null) }
            try {
                confirmUpdateUseCase(kyc)
                _state.update { 
                    it.copy(
                        isLoading = false, 
                        isUpdatingProfile = false,
                        step = AadhaarStep.SUCCESS
                    )
                }
                _effect.emit(AadhaarUiEffect.ProfileUpdateSuccess)
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Profile update failed"
                _state.update { it.copy(isLoading = false, isUpdatingProfile = false, error = errorMessage) }
                _effect.emit(AadhaarUiEffect.ShowError(errorMessage))
            }
        }
    }

    fun skipProfileUpdate() {
        _state.update { it.copy(step = AadhaarStep.SUCCESS) }
    }

    fun onReset() {
        _state.value = AadhaarUiState()
    }
}