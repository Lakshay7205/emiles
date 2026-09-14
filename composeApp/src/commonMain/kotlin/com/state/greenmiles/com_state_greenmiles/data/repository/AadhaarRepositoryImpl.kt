package com.state.greenmiles.com_state_greenmiles.data.repository

import com.state.greenmiles.com_state_greenmiles.data.mappers.toDomain
import com.state.greenmiles.com_state_greenmiles.data.remote.api.AadhaarApi
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.AadhaarVerifyRequestDto
import com.state.greenmiles.com_state_greenmiles.domain.model.AadhaarKyc
import com.state.greenmiles.com_state_greenmiles.domain.repository.AadhaarRepository
import com.state.greenmiles.com_state_greenmiles.utils.LocalStorage

class AadhaarRepositoryImpl(
    private val api: AadhaarApi,
    private val localStorage: LocalStorage
) : AadhaarRepository {

    override suspend fun uploadAadhaarXml(
        fileBytes: ByteArray
    ): String {
        println("AadhaarRepo | uploadAadhaarXml called")

        val token = localStorage.getToken()
        if (token.isNullOrBlank()) {
            println("AadhaarRepo | upload FAILED -> No token")
            throw IllegalStateException("User not authenticated")
        }

        val response = api.uploadAadhaarXml(
            token = token,
            fileName = "aadhaar.zip",
            fileBytes = fileBytes
        )

        if (!response.success || response.data == null) {
            println("AadhaarRepo | upload FAILED -> ${response.message}")
            throw IllegalStateException(response.message ?: "Aadhaar upload failed")
        }

        return response.data.s3Path
    }

    override suspend fun verifyAadhaar(
        s3Path: String,
        pin: String
    ): AadhaarKyc {
        println("AadhaarRepo | verifyAadhaar -> s3Path=$s3Path")

        val token = localStorage.getToken()
        if (token.isNullOrBlank()) {
            println("AadhaarRepo | verify FAILED -> No token")
            throw IllegalStateException("User not authenticated")
        }

        val response = api.verifyAadhaar(
            token = token,
            body = AadhaarVerifyRequestDto(
                s3Path = s3Path,
                pin = pin
            )
        )

        // Log the response for debugging
        println("AadhaarRepo | verify Response: success=${response.success}, message=${response.message}, data=${response.data}")

        // Check if API call itself failed
        if (!response.success || response.data == null) {
            val errorMsg = response.message ?: "Aadhaar verification failed"
            println("AadhaarRepo | verify FAILED -> $errorMsg")
            throw IllegalStateException(errorMsg)
        }

        // Check if the response message indicates verification failure
        val message = response.message?.lowercase() ?: ""
        if (message.contains("fail") || message.contains("invalid") || message.contains("incorrect")) {
            println("AadhaarRepo | verify FAILED -> Verification not successful: ${response.message}")
            throw IllegalStateException(response.message ?: "Aadhaar verification failed")
        }

        // Validate that essential KYC data is present
        val kycData = response.data
        if (kycData.personalInfo?.name.isNullOrBlank() && kycData.maskedAadhaar.isNullOrBlank()) {
            println("AadhaarRepo | verify FAILED -> Empty KYC data returned")
            throw IllegalStateException("Verification failed: no KYC data received")
        }

        return kycData.toDomain()
    }

    override suspend fun confirmProfileUpdate(kyc: AadhaarKyc) {
        println("AadhaarRepo | confirmProfileUpdate called")

        val token = localStorage.getToken()
        if (token.isNullOrBlank()) {
            throw IllegalStateException("User not authenticated")
        }

        val body = com.state.greenmiles.com_state_greenmiles.data.remote.dto.ProfileUpdateRequest(
            profileUpdates = com.state.greenmiles.com_state_greenmiles.data.remote.dto.ProfileDetailsDto(
                name = kyc.name,
                dateOfBirth = kyc.dob,
                gender = kyc.gender,
                address = kyc.address,
                city = kyc.city,
                state = kyc.state,
                pincode = kyc.pincode
            )
        )

        val response = api.confirmProfileUpdate(token, body)

        if (!response.success) {
            throw IllegalStateException(response.message ?: "Profile update failed")
        }
    }
}
