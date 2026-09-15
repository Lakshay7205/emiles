package com.state.greenmiles.com_state_greenmiles.domain.usecases

import com.state.greenmiles.com_state_greenmiles.domain.model.AuthResult
import com.state.greenmiles.com_state_greenmiles.domain.repository.AuthRepository
import com.state.greenmiles.com_state_greenmiles.utils.LocalStorage

class CompleteRegistrationUseCase(
    private val repository: AuthRepository,
    private val localStorage: LocalStorage
) {
    suspend operator fun invoke(
        name: String,
        email: String,
        password: String,
        verifiedToken: String
    ): AuthResult {

        require(name.isNotBlank()) { "Name cannot be blank" }
        require(email.isNotBlank()) { "Email cannot be blank" }
        require(password.length >= 5) { "Password must be at least 5 characters" }
        require(verifiedToken.isNotBlank()) { "Verified token cannot be blank" }

        val result = repository.completeRegistration(
            name = name,
            email = email,
            password = password,
            verifiedToken = verifiedToken
        )

        // 👇 Persist token after successful registration
        localStorage.saveToken(result.token)

        return result
    }
}

