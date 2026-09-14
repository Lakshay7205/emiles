package com.state.greenmiles.com_state_greenmiles.data.remote.dto.car_parcel_api

import com.state.greenmiles.com_state_greenmiles.data.remote.dto.CoordinateDto
import kotlinx.serialization.Serializable

@Serializable
data class CarParcelTripRequest(
    val selectedPolyline: String,
    val startPointName: String,
    val endPointName: String,
    val totalDistance: Double,
    val tripDate: String,
    val tripTime: String,
    val availableSpace: Int,
    val costPerKg: Double,
    val message: String,
    val carDetails: CarDetailsDto,
    val estimatedDurationSeconds: Long
)

@Serializable
data class CarDetailsDto(
    val model: String,
    val licensePlate: String,
    val color: String,
    val year: Int,
    val type: String
)

@Serializable
data class SearchCarParcelTripRequest(
    val startCoordinate: CoordinateDto,
    val endCoordinate: CoordinateDto,
    val startPointName: String,
    val endPointName: String,
    val tripDate: String,
    val packageSize: Int,
    val page: Int = 1,
    val limit: Int = 10,
    val cursor: String? = null
)
