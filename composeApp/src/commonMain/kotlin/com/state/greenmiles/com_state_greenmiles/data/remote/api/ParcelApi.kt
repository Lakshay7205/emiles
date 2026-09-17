package com.state.greenmiles.com_state_greenmiles.data.remote.api

import com.state.greenmiles.com_state_greenmiles.data.remote.api.ApiConstants
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.ApiResponse
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.public_parcel_api.PublicParcelTripData
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.public_parcel_api.PublicParcelTripRequest
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.public_parcel_api.SearchParcelTripsData
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.car_parcel_api.CarParcelTripData
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.car_parcel_api.CarParcelTripRequest
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.car_parcel_api.SearchCarParcelTripRequest
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.car_parcel_api.SearchCarParcelTripsData
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.public_parcel_api.SearchPublicParcelTripRequest
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.parcel_booking.ParcelBookingData
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.parcel_booking.ParcelBookingRequest
import com.state.greenmiles.com_state_greenmiles.utils.LocalStorage
import io.ktor.client.HttpClient
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class ParcelApi(
    private val client: HttpClient,
    private val localStorage: LocalStorage
) {
    private val baseUrl = ApiConstants.BASE_URL

    suspend fun createPublicParcelTrip(
        body: PublicParcelTripRequest
    ): ApiResponse<PublicParcelTripData> = safeApiCall {
        client.post(baseUrl + ParcelEndpoints.PUBLIC_PARCEL_TRIPS) {
            contentType(ContentType.Application.Json)
            headers.append("Authorization", authHeader())
            headers.append("accept", "application/json")
            setBody(body)
        }
    }

    suspend fun searchPublicParcelTrips(
        body: SearchPublicParcelTripRequest
    ): ApiResponse<SearchParcelTripsData> = safeApiCall {
        client.post(baseUrl + ParcelEndpoints.SEARCH_PUBLIC_PARCEL_TRIPS) {
            contentType(ContentType.Application.Json)
            headers.append("Authorization", authHeader())
            headers.append("accept", "application/json")
            setBody(body)
        }
    }

    suspend fun createCarParcelTrip(
        body: CarParcelTripRequest
    ): ApiResponse<CarParcelTripData> = safeApiCall {
        client.post(baseUrl + ParcelEndpoints.CAR_PARCEL_TRIPS) {
            contentType(ContentType.Application.Json)
            headers.append("Authorization", authHeader())
            headers.append("accept", "*/*")
            setBody(body)
        }
    }

    suspend fun searchCarParcelTrips(
        body: SearchCarParcelTripRequest
    ): ApiResponse<SearchCarParcelTripsData> = safeApiCall {
        client.post(baseUrl + ParcelEndpoints.SEARCH_CAR_PARCEL_TRIPS) {
            contentType(ContentType.Application.Json)
            headers.append("Authorization", authHeader())
            headers.append("accept", "application/json")
            setBody(body)
        }
    }

    suspend fun bookCarParcel(
        body: ParcelBookingRequest
    ): ApiResponse<ParcelBookingData> = safeApiCall {
        client.post(baseUrl + ParcelEndpoints.BOOK_CAR_PARCEL) {
            contentType(ContentType.Application.Json)
            headers.append("Authorization", authHeader())
            headers.append("accept", "application/json")
            setBody(body)
        }
    }

    suspend fun bookPublicParcel(
        body: ParcelBookingRequest
    ): ApiResponse<ParcelBookingData> = safeApiCall {
        client.post(baseUrl + ParcelEndpoints.BOOK_PUBLIC_PARCEL) {
            contentType(ContentType.Application.Json)
            headers.append("Authorization", authHeader())
            headers.append("accept", "application/json")
            setBody(body)
        }
    }

    private fun authHeader(): String {
        val token = localStorage.getToken()
            ?: throw IllegalStateException("Auth token missing")
        return "Bearer $token"
    }
}
