package com.state.greenmiles.com_state_greenmiles.domain.usecases

import com.state.greenmiles.com_state_greenmiles.domain.repository.AuthRepository

class ResetPasswordUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        tempToken: String,
        newPassword: String
    ) {
        require(tempToken.isNotBlank())
        require(newPassword.length >= 6)

        repository.resetPassword(
            tempToken = tempToken,
            newPassword = newPassword
        )
    }
}
