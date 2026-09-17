package com.state.greenmiles.com_state_greenmiles.data.remote.api

import com.state.greenmiles.com_state_greenmiles.data.remote.dto.ApiResponse
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.AuthDto
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.OtpDto
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.TempTokenDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class AuthApi(
    private val client: HttpClient
) {

    private val baseUrl = ApiConstants.BASE_URL

    suspend fun sendRegisterOtp(mobile: String): ApiResponse<OtpDto> =
        safeApiCall {

            val body = SendOtpRequest(mobile)

            println("📤 Sending OTP Request Body: $body")

            client.post("$baseUrl/api/auth/register/send-otp") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
        }


    suspend fun verifyRegisterOtp(
        body: VerifyOtpRequest
    ): ApiResponse<TempTokenDto> =
        safeApiCall {
            client.post("$baseUrl/api/auth/register/verify-otp") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
        }

    suspend fun completeRegistration(
        body: CompleteVerifiedRequest
    ): ApiResponse<AuthDto> =
        safeApiCall {
            client.post("$baseUrl/api/auth/register/complete-verified") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
        }

    suspend fun login(
        body: LoginRequest
    ): ApiResponse<AuthDto> =
        safeApiCall {
            client.post("$baseUrl/api/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
        }

    suspend fun sendForgotPasswordOtp(mobile: String): ApiResponse<OtpDto> =
        safeApiCall {
            client.post("$baseUrl/api/auth/forgot-password/send-otp") {
                contentType(ContentType.Application.Json)
                setBody(SendOtpRequest(mobile))
            }
        }

    suspend fun verifyForgotPasswordOtp(
        body: VerifyOtpRequest
    ): ApiResponse<TempTokenDto> =
        safeApiCall {
            client.post("$baseUrl/api/auth/forgot-password/verify-otp") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
        }

    suspend fun resetPassword(
        body: ResetPasswordRequest
    ): ApiResponse<ResetPasswordResponseDto> =
        safeApiCall {
            client.post("$baseUrl/api/auth/forgot-password/reset") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
        }
}

object ApiConstants {
    const val BASE_URL = "https://sonso-api-v3-431128457382.asia-south1.run.app"
}

/* =========================
   CENTRALIZED SAFE API CALL
   ========================= */

suspend inline fun <reified T> safeApiCall(
    crossinline apiCall: suspend () -> HttpResponse
): ApiResponse<T> {
    return try {
        val response = apiCall()

        println("📡 API Response | status=${response.status.value} url=${response.call.request.url}")

        if (response.status.value in 200..299) {
            val bodyText = response.bodyAsText()
            println("📡 API Success | status=${response.status.value} body=$bodyText")
            val json = Json { ignoreUnknownKeys = true }
            json.decodeFromString<ApiResponse<T>>(bodyText)
        } else {
            val rawJson = response.bodyAsText()

            println("❌ API Error | status=${response.status.value} body=$rawJson")

            val message = try {
                val json = Json.parseToJsonElement(rawJson).jsonObject
                json["error"]?.jsonObject?.get("message")
                    ?.jsonPrimitive?.content
                    ?: json["message"]?.jsonPrimitive?.content
                    ?: "Something went wrong"
            } catch (e: Exception) {
                "Something went wrong: $rawJson"
            }

            ApiResponse.error(message)
        }
    } catch (e: Exception) {
        println("🔥 API Exception | ${e::class.simpleName}: ${e.message}")
        ApiResponse.error(e.message ?: "Network error")
    }
}
/* =========================
   REQUEST MODELS
   ========================= */

@Serializable
data class SendOtpRequest(
    val mobile: String
)

@Serializable
data class CompleteVerifiedRequest(
    val name: String,
    val email: String,
    val password: String,
    val verifiedToken: String
)

@Serializable
data class LoginRequest(
    val mobile: String,
    val password: String
)

@Serializable
data class VerifyOtpRequest(
    val otpToken: String? = null,
    val mobile: String? = null,
    val otp: String
)

@Serializable
data class ResetPasswordRequest(
    val tempToken: String,
    val newPassword: String
)
@Serializable
data class ResetPasswordResponseDto(
    val success: Boolean,
    val message: String,
    val userId: String? = null,
    val token: String? = null
)
