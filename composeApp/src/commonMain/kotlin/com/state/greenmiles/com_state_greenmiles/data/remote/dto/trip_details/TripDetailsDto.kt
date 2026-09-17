package com.state.greenmiles.com_state_greenmiles.data.remote.dto.trip_details


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response DTO for trip details API
 */
@Serializable
data class TripDetailsResponse(
    @SerialName("success")
    val success: Boolean? = null,

    @SerialName("message")
    val message: String? = null,

    @SerialName("data")
    val data: TripDetailsData? = null  // Keep property name as "data"
)


@Serializable
data class TripDetailsData(
    @SerialName("id")
    val id: String? = null,

    @SerialName("userId")
    val userId: String? = null,

    @SerialName("selectedPolyline")
    val selectedPolyline: String? = null,

    @SerialName("startPointName")
    val startPointName: String? = null,

    @SerialName("endPointName")
    val endPointName: String? = null,

    @SerialName("startCoordinate")
    val startCoordinate: CoordinateDto? = null,

    @SerialName("endCoordinate")
    val endCoordinate: CoordinateDto? = null,

    @SerialName("totalDistanceKm")
    val totalDistanceKm: Double? = null,

    @SerialName("estimatedDurationSeconds")
    val estimatedDurationSeconds: Int? = null,

    @SerialName("tripDate")
    val tripDate: String? = null,

    @SerialName("tripTime")
    val tripTime: String? = null,

    @SerialName("availableSeats")
    val availableSeats: Int? = null,

    @SerialName("bookedSeats")
    val bookedSeats: Int? = null,

    @SerialName("costPerSeat")
    val costPerSeat: Double? = null,

    @SerialName("costPerKm")
    val costPerKm: Double? = null,

    @SerialName("carDetails")
    val carDetails: CarDetailsDto? = null,

    @SerialName("womanAccompany")
    val womanAccompany: Boolean? = null,

    @SerialName("status")
    val status: String? = null,

    @SerialName("createdAt")
    val createdAt: String? = null,

    @SerialName("updatedAt")
    val updatedAt: String? = null,

    @SerialName("user")
    val user: TripUserDto? = null,

    @SerialName("miscMessage")
    val miscMessage: String? = null,

    @SerialName("recommendedCost")
    val recommendedCost: Double? = null
)

@Serializable
data class CoordinateDto(
    @SerialName("latitude")
    val latitude: Double? = null,

    @SerialName("longitude")
    val longitude: Double? = null
)

@Serializable
data class CarDetailsDto(
    @SerialName("type")
    val type: String? = null,

    @SerialName("year")
    val year: Int? = null,

    @SerialName("color")
    val color: String? = null,

    @SerialName("model")
    val model: String? = null,

    @SerialName("licensePlate")
    val licensePlate: String? = null
)

@Serializable
data class TripUserDto(
    @SerialName("id")
    val id: String? = null,

    @SerialName("name")
    val name: String? = null,

    @SerialName("rating")
    val rating: Double? = null,

    @SerialName("isVerified")
    val isVerified: Boolean? = null,

    @SerialName("gender")
    val gender: String? = null
)