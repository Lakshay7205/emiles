package com.state.greenmiles.com_state_greenmiles.domain.repository


import com.state.greenmiles.com_state_greenmiles.domain.model.AadhaarKyc

/**
 * Repository interface for Aadhaar-related operations.
 * Following Clean Architecture, this sits in the Domain layer.
 */
interface AadhaarRepository {
    suspend fun uploadAadhaarXml(
        fileBytes: ByteArray
    ): String

    suspend fun verifyAadhaar(
        s3Path: String,
        pin: String
    ): AadhaarKyc

    suspend fun confirmProfileUpdate(
        kyc: AadhaarKyc
    )
}

