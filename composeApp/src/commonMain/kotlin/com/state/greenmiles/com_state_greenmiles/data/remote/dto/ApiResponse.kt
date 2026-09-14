package com.state.greenmiles.com_state_greenmiles.data.remote.dto

import kotlinx.serialization.Serializable

// data/remote/dto/ApiResponse.kt
@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null
) {
    companion object {
        fun <T> success(data: T): ApiResponse<T> =
            ApiResponse(success = true, data = data)

        fun <T> error(message: String): ApiResponse<T> =
            ApiResponse(success = false, message = message)
    }
}
