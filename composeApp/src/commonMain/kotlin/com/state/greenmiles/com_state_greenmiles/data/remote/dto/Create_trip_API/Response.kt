package com.state.greenmiles.com_state_greenmiles.data.remote.dto.Create_trip_API

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class TripDto(

    val id: String? = null,

    val userId: String? = null,

    val startPointName: String? = null,
    val endPointName: String? = null,

    @SerialName("totalDistanceKm")
    val totalDistanceKm: Double? = null,

    @SerialName("estimatedDurationSeconds")
    val estimatedDurationSeconds: Int? = null,

    val tripDate: String? = null,
    val tripTime: String? = null,

    val availableSeats: Int? = null,

    val costPerSeat: Double? = null,
    val costPerKm: Double? = null,

    val carDetails: CarDetailsDto? = null,

    val womanAccompany: Boolean? = null,

    val estimatedRideDistance: Double? = null,
    val estimatedTotalCost: Double? = null,

    val proximityScore: Int? = null,

    val matchPriority: Int? = null,

    val userRating: Double? = null,

    val userIsVerified: Boolean? = null,

    val userGender: String? = null,
    val userName: String? = null,

    val geohashCount: Int? = null,

    val densityPerKm: Double? = null,

    val cursorValue: String? = null,
    
    val miscMessage: String? = null,

    val recommendedCost: Double? = null
)


