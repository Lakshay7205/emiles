package com.state.greenmiles.com_state_greenmiles.domain.usecases

import com.state.greenmiles.com_state_greenmiles.domain.repository.AadhaarRepository

class UploadAadhaarXmlUseCase(
    private val repository: AadhaarRepository
) {
    suspend operator fun invoke(fileBytes: ByteArray): String {
        require(fileBytes.isNotEmpty()) { "Aadhaar file cannot be empty" }
        require(fileBytes.size < 5 * 1024 * 1024) { "File size must be less than 5MB" }
        return repository.uploadAadhaarXml(fileBytes)
    }
}
