package com.state.greenmiles.com_state_greenmiles.data.remote.dto.public_parcel_api

import com.state.greenmiles.com_state_greenmiles.data.remote.dto.CoordinateDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class PublicParcelTripData(
    val trip: ParcelTripDto? = null,
    val routeInfo: ParcelRouteInfoDto? = null
)

@Serializable
data class ParcelTripDto(
    val id: String,
    val userId: String? = null,
    val startPointName: String? = null,
    val endPointName: String? = null,
    val totalDistanceKm: Double? = null,
    val estimatedDurationSeconds: Long? = null,
    val tripDate: String? = null,
    val tripTime: String? = null,
    val availableSpace: Int? = null,
    val costPerKg: Double? = null,
    val message: String? = null,
    val costPerKm: Double? = null,
    val transportDetails: ParcelTransportDetailsDto? = null,
    val status: String? = null,
    val startCoordinate: CoordinateDto? = null,
    val endCoordinate: CoordinateDto? = null,
    val distanceFromUser: Double? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

@Serializable
data class ParcelTransportDetailsDto(
    val type: String? = null,
    val name: String? = null,
    val operator: String? = null,
    val route: String? = null,
    val vehicleNumber: String? = null
)

@Serializable
data class ParcelRouteInfoDto(
    val transportType: String? = null,
    val geometryType: String? = null,
    val startCoordinate: JsonObject? = null,
    val endCoordinate: JsonObject? = null
)

@Serializable
data class SearchParcelTripsData(
    val trips: List<ParcelTripDto> = emptyList(),
    val pagination: PaginationDto? = null
)

@Serializable
data class PaginationDto(
    val currentPage: Int? = null,
    val totalCount: Int? = null,
    val itemsPerPage: Int? = null,
    val totalPages: Int? = null,
    val hasNextPage: Boolean? = null,
    val hasPreviousPage: Boolean? = null,
    val nextCursor: String? = null
)
