package com.state.greenmiles.com_state_greenmiles.data.remote.api

import com.state.greenmiles.com_state_greenmiles.data.remote.dto.AadhaarKycDto
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.AadhaarVerifyRequestDto
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.ApiResponse
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.ProfileUpdateRequest
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.UploadAadhaarResponseDto
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*

class AadhaarApi(
    private val client: HttpClient
) {
    private val baseUrl = ApiConstants.BASE_URL

    suspend fun uploadAadhaarXml(
        token: String,
        fileName: String = "aadhaar.zip",
        fileBytes: ByteArray
    ): ApiResponse<UploadAadhaarResponseDto> = safeApiCall {
        client.submitFormWithBinaryData(
            url = "$baseUrl/api/aadhaar/upload",
            formData = formData {
                append(
                    key = "aadhaarFile",
                    value = fileBytes,
                    headers = Headers.build {
                        append(HttpHeaders.ContentType, "application/zip")
                        append(
                            HttpHeaders.ContentDisposition,
                            "form-data; name=\"aadhaarFile\"; filename=\"$fileName\""
                        )
                    }
                )
            }
        ) {
            method = HttpMethod.Post
            header(HttpHeaders.Authorization, "Bearer $token")
        }
    }

    suspend fun verifyAadhaar(
        token: String,
        body: AadhaarVerifyRequestDto
    ): ApiResponse<AadhaarKycDto> = safeApiCall {
        client.post("$baseUrl/api/aadhaar/verify") {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(body)
        }
    }

    suspend fun confirmProfileUpdate(
        token: String,
        body: ProfileUpdateRequest
    ): ApiResponse<Unit> = safeApiCall {
        client.post("$baseUrl/api/aadhaar/confirm-profile-update") {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(body)
        }
    }
}
