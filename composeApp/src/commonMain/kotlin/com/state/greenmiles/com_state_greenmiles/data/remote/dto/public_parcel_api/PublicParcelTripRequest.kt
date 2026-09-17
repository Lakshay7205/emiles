package com.state.greenmiles.com_state_greenmiles.data.remote.dto.public_parcel_api

import com.state.greenmiles.com_state_greenmiles.data.remote.dto.CoordinateDto
import kotlinx.serialization.Serializable

@Serializable
data class PublicParcelTripRequest(
    val startPointName: String,
    val endPointName: String,
    val startpointlatitude: Double,
    val startpointlongitude: Double,
    val endpointlatitude: Double,
    val endpointlongitude: Double,
    val tripDate: String,
    val tripTime: String,
    val availableSpace: Int,
    val costPerKg: Double,
    val transportDetails: TransportDetailsDto,
    val message: String
)

@Serializable
data class TransportDetailsDto(
    val type: String,
    val name: String,
    val operator: String,
    val route: String? = null,
    val vehicleNumber: String
)

@Serializable
data class SearchPublicParcelTripRequest(
    val startCoordinate: CoordinateDto,
    val endCoordinate: CoordinateDto,
    val startPointName: String,
    val endPointName: String,
    val tripDate: String,
    val packageSize: Int,
    val transportType: String,
    val page: Int,
    val limit: Int,
    val cursor: String? = null
)
