package com.state.greenmiles.com_state_greenmiles.domain.usecases

import com.state.greenmiles.com_state_greenmiles.domain.model.AuthResult
import com.state.greenmiles.com_state_greenmiles.domain.repository.AuthRepository
import com.state.greenmiles.com_state_greenmiles.utils.LocalStorage

class LoginUseCase(
    private val repository: AuthRepository,
    private val localStorage: LocalStorage
) {
    suspend operator fun invoke(
        mobile: String,
        password: String
    ): AuthResult {

        require(mobile.isNotBlank()) { "Mobile cannot be blank" }
        require(password.isNotBlank()) { "Password cannot be blank" }

        val result = repository.login(
            mobile = mobile,
            password = password
        )

        localStorage.saveToken(result.token)

        return result
    }
}
