package com.state.greenmiles.com_state_greenmiles.data.remote.dto.car_parcel_api

import com.state.greenmiles.com_state_greenmiles.data.remote.dto.public_parcel_api.PaginationDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CarParcelTripData(
    val trip: CarParcelTripDto? = null
)

@Serializable
data class SearchCarParcelTripsData(
    val trips: List<CarParcelTripDto>,
    val pagination: PaginationDto? = null,
    val searchMetadata: CarParcelSearchMetadataDto? = null
)

@Serializable
data class CarParcelSearchMetadataDto(
    val idk: String? = null
)

@Serializable
data class CarParcelTripDto(
    val id: String,
    val userId: String? = null,
    val selectedPolyline: String? = null,
    val totalDistanceKm: Double? = null,
    val estimatedDurationSeconds: Int? = null,
    val tripDate: String? = null,
    val tripTime: String? = null,
    val carDetails: CarParcelDetailsDto? = null,
    val status: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val miscMessage: String? = null,
    val availableSeats: Int? = null,
    val costPerSeat: Double? = null,
    val costPerKm: Double? = null,
    val availableSpace: Int? = null,
    val distanceFromUser: Double? = null,
    val canCarryParcel: Boolean? = null,
    val parcelCostPerKg: Double? = null,
    val partialCost: Double? = null,
    val bookedSeats: Int? = null,
    val startPointName: String? = null,
    val endPointName: String? = null
)

@Serializable
data class CarParcelDetailsDto(
    val model: String? = null,
    val licensePlate: String? = null,
    val color: String? = null,
    val year: Int? = null,
    val type: String? = null
)
