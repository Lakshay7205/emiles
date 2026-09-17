package com.state.greenmiles.com_state_greenmiles.data.remote.dto.Create_trip_API

import kotlinx.serialization.Serializable

@Serializable
data class CreateTripRequest(
    val selectedPolyline: String,
    val startPointName: String,
    val endPointName: String,
    val totalDistance: Double,
    val tripDate: String,
    val tripTime: String,
    val availableSeats: Int,
    val costPerSeat: Double,
    val womanAccompany: Boolean,
    val carDetails: CarDetailsDto,
    val estimatedDurationSeconds: Long,
    val miscMessage: String? = null
)

@Serializable
data class CarDetailsDto(
    val type: String? = null,
    val year: Int? = null,
    val color: String? = null,
    val model: String? = null,
    val licensePlate: String? = null
)