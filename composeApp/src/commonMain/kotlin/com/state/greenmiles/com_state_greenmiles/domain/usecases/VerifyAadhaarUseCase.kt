package com.state.greenmiles.com_state_greenmiles.domain.usecases

import com.state.greenmiles.com_state_greenmiles.domain.model.AadhaarKyc
import com.state.greenmiles.com_state_greenmiles.domain.repository.AadhaarRepository

class VerifyAadhaarUseCase(
    private val repository: AadhaarRepository
) {
    suspend operator fun invoke(s3Path: String, pin: String): AadhaarKyc {
        require(s3Path.isNotBlank()) { "Invalid Aadhaar file reference" }
        require(pin.isNotBlank()) { "PIN cannot be empty" }
        require(pin.length == 4) { "PIN must be exactly 4 digits" }
        return repository.verifyAadhaar(s3Path, pin)
    }
}
