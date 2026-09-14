package com.state.greenmiles.com_state_greenmiles.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UploadAadhaarResponseDto(
    val s3Path: String
)

@Serializable
data class AadhaarVerifyRequestDto(
    val s3Path: String,
    val pin: String
)

@Serializable
data class AadhaarPersonalInfoDto(
    val name: String? = null,
    val dateOfBirth: String? = null,
    val gender: String? = null,
    val careOf: String? = null
)

@Serializable
data class AadhaarAddressDto(
    val house: String? = null,
    val street: String? = null,
    val locality: String? = null,
    val village: String? = null,
    val district: String? = null,
    val state: String? = null,
    val pincode: String? = null
)

@Serializable
data class AadhaarKycDto(
    val personalInfo: AadhaarPersonalInfoDto? = null,
    val address: AadhaarAddressDto? = null,
    val maskedAadhaar: String? = null
)

@Serializable
data class ProfileDetailsDto(
    val name: String,
    val dateOfBirth: String,
    val gender: String,
    val address: String,
    val city: String,
    val state: String,
    val pincode: String
)

@Serializable
data class ProfileUpdateRequest(
    val profileUpdates: ProfileDetailsDto
)
