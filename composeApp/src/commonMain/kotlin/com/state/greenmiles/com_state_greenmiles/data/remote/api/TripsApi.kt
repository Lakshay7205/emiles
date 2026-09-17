package com.state.greenmiles.com_state_greenmiles.data.remote.api

import com.state.greenmiles.com_state_greenmiles.data.remote.api.ApiConstants.BASE_URL
import com.state.greenmiles.com_state_greenmiles.data.remote.constants.TripEndpoints
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.ApiResponse
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.Create_trip_API.CreateTripRequest
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.Create_trip_API.TripDto
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.ProfileResponseDto
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.SendMobileOtpRequest
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.SendMobileOtpResponseDto
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.UpdateEmailRequest
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.VerifyMobileRequest
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.my_bookings.BookingDto
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.my_bookings.MyBookingsResponseDto
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.my_bookings.TripBookingsResponseDto
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.my_created_trips.MyTripDto
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.my_trips.MyTripsDto
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.route_api.requests.GetRoutesRequest
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.route_api.responses.RoutesDto
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.search_trip_apis.SearchTripsDto
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.search_trip_apis.SearchTripsRequest
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.trip_details.TripDetailsData
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.trip_details.TripDetailsResponse
import com.state.greenmiles.com_state_greenmiles.data.remote.model.BookingRequest
import com.state.greenmiles.com_state_greenmiles.utils.LocalStorage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json

class TripsApi(
    private val client: HttpClient,
    private val localStorage: LocalStorage

) {

    private val baseUrl = ApiConstants.BASE_URL

    suspend fun getRoutes(body: GetRoutesRequest): ApiResponse<RoutesDto> =
        safeApiCall {
            client.post(baseUrl + TripEndpoints.ROUTES) {
                contentType(ContentType.Application.Json)
                headers.append("Authorization", authHeader())
                setBody(body)
            }
        }

    suspend fun createTrip(body: CreateTripRequest): ApiResponse<TripDto> =
        safeApiCall {
            client.post(baseUrl + TripEndpoints.CREATE_TRIP) {
                contentType(ContentType.Application.Json)
                headers.append("Authorization", authHeader())
                setBody(body)
            }
        }


    suspend fun searchTrips(body: SearchTripsRequest): ApiResponse<SearchTripsDto> =
        safeApiCall {
            client.post(baseUrl + TripEndpoints.SEARCH_TRIPS) {
                contentType(ContentType.Application.Json)
                headers.append("Authorization", authHeader())
                setBody(body)
            }
        }


    suspend fun getMyTrips(status: String, page: Int, limit: Int): ApiResponse<MyTripsDto> =
        safeApiCall {
            client.get(baseUrl + TripEndpoints.MY_TRIPS) {
                headers.append("Authorization", authHeader())
                parameter("status", status)
                parameter("page", page)
                parameter("limit", limit)
            }
        }


    /**
     * Get trip details by trip ID
     * @param tripId The unique identifier of the trip
     * @return ApiResponse containing TripDetailsResponse
     */
    suspend fun getTripDetails(tripId: String): ApiResponse<TripDetailsData> =
        safeApiCall<TripDetailsData> {  // ✅ Correct - deserialize the "data" field directly
            val url = "$baseUrl${TripEndpoints.TRIP_DETAILS}/$tripId"

            println("🚀 TripDetails API CALL")
            println("➡️ URL = $url")

            val response: HttpResponse = client.get(url) {
                contentType(ContentType.Application.Json)
                headers {
                    append("Authorization", authHeader())
                    append("accept", "application/json")
                }
            }

            println("✅ Status = ${response.status}")

            val rawJson = response.bodyAsText()
            println("📩 RAW JSON = $rawJson")

            response
        }


    suspend fun bookTrip(body: BookingRequest): Boolean {
        return try {
            val response: HttpResponse = client.post(baseUrl + TripEndpoints.TRIP_BOOKINGS) {
                contentType(ContentType.Application.Json)
                headers {
                    append("Authorization", authHeader())
                    append("accept", "application/json")
                }
                setBody(body)
            }
            println("🎫 BookTrip status = ${response.status}")
            val rawJson = response.bodyAsText()
            println("📩 BookTrip response = $rawJson")

            response.status.isSuccess()   // true for 200–299
        } catch (e: Exception) {
            println("❌ BookTrip exception: ${e.message}")
            throw e
        }
    }


    suspend fun getUserProfile(
        userId: String
    ): ApiResponse<ProfileResponseDto> =
        safeApiCall {
            client.get("${baseUrl}${TripEndpoints.GET_PROFILE}/$userId") {
                headers.append("Authorization", authHeader())
                accept(ContentType.Application.Json)
            }
        }

    suspend fun sendMobileOtp(
        newMobile: String
    ): ApiResponse<SendMobileOtpResponseDto> =
        safeApiCall {
            client.post("$baseUrl/api/users/profile/mobile/send-otp") {
                contentType(ContentType.Application.Json)
                headers.append("Authorization", authHeader())
                setBody(SendMobileOtpRequest(newMobile))
            }
        }

    suspend fun verifyAndUpdateMobile(
        newMobile: String,
        otp: String,
        otpToken: String
    ): ApiResponse<Unit> =
        safeApiCall {
            client.post("$baseUrl/api/users/profile/mobile/verify-and-update") {
                contentType(ContentType.Application.Json)
                headers.append("Authorization", authHeader())
                setBody(
                    VerifyMobileRequest(newMobile, otp, otpToken)
                )
            }
        }
    suspend fun updateEmail(
        email: String
    ): ApiResponse<Unit> =
        safeApiCall {
            client.put("$baseUrl/api/users/profile/email") {
                contentType(ContentType.Application.Json)
                headers.append("Authorization", authHeader())
                setBody(UpdateEmailRequest(email))
            }
        }

    suspend fun getMyBookings(
        page: Int,
        limit: Int,
        status: String? = null
    ): ApiResponse<MyBookingsResponseDto> =
        safeApiCall {
            client.get("$baseUrl/api/trip-bookings/my-bookings") {
                headers.append("Authorization", authHeader())
                parameter("page", page)
                parameter("limit", limit)
                status?.let { parameter("status", it) }
            }
        }

    suspend fun getBookingDetails(
        tripId: String
    ): ApiResponse<BookingDto> =
        safeApiCall {
            client.get("$baseUrl/api/trip-bookings/my-bookings/$tripId") {
                headers.append("Authorization", authHeader())
            }
        }

    suspend fun cancelBooking(
        bookingId: String
    ): ApiResponse<Unit> =
        safeApiCall {
            client.put("$baseUrl/api/trip-bookings/$bookingId/cancel") {
                headers.append("Authorization", authHeader())
            }
        }
    suspend fun getMyCreatedTrips(
        page: Int,
        limit: Int
    ): ApiResponse<List<MyTripDto>> =
        safeApiCall {
            client.get("$BASE_URL/api/trips/my-trips") {
                headers.append("Authorization", authHeader())
                parameter("page", page)
                parameter("limit", limit)
            }
        }

    suspend fun acceptBooking(
        bookingId: String
    ): ApiResponse<Unit> =
        safeApiCall {
            client.put("$baseUrl/api/trip-bookings/$bookingId/accept") {
                headers.append("Authorization", authHeader())
            }
        }

    suspend fun rejectBooking(
        bookingId: String
    ): ApiResponse<Unit> =
        safeApiCall {
            client.put("$baseUrl/api/trip-bookings/$bookingId/reject") {
                headers.append("Authorization", authHeader())
            }
        }

    suspend fun getBookingsForTrip(
        tripId: String
    ): ApiResponse<TripBookingsResponseDto> =
        safeApiCall {
            client.get("$baseUrl/api/trip-bookings/trip/$tripId") {
                headers.append("Authorization", authHeader())
            }
        }
    private fun authHeader(): String {
        val token = localStorage.getToken()
            ?: throw IllegalStateException("Auth token missing")
        return "Bearer $token"
    }

}
