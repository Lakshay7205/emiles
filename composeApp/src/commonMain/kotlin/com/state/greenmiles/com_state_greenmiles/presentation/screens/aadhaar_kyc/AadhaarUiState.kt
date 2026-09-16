package com.state.greenmiles.com_state_greenmiles.presentation.screens.aadhaar_kyc

import com.state.greenmiles.com_state_greenmiles.domain.model.AadhaarKyc

data class AadhaarUiState(
    val isLoading: Boolean = false,
    val uploadProgress: Boolean = false,
    val verificationProgress: Boolean = false,
    val isUpdatingProfile: Boolean = false,
    val s3Path: String? = null,
    val kycDetails: AadhaarKyc? = null,
    val error: String? = null,
    val step: AadhaarStep = AadhaarStep.UPLOAD
)

enum class AadhaarStep {
    UPLOAD,
    VERIFY,
    CONFIRM_PROFILE,
    SUCCESS
}

sealed class AadhaarUiEffect {
    data class ShowError(val message: String) : AadhaarUiEffect()
    object NavigationBack : AadhaarUiEffect()
    object ProfileUpdateSuccess : AadhaarUiEffect()
}