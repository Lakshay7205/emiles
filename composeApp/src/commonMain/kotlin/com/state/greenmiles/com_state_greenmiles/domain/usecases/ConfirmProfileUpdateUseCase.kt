package com.state.greenmiles.com_state_greenmiles.domain.usecases

import com.state.greenmiles.com_state_greenmiles.domain.model.AadhaarKyc
import com.state.greenmiles.com_state_greenmiles.domain.repository.AadhaarRepository

class ConfirmProfileUpdateUseCase(
    private val repository: AadhaarRepository
) {
    suspend operator fun invoke(kyc: AadhaarKyc) {
        repository.confirmProfileUpdate(kyc)
    }
}
