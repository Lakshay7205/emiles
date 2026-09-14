package com.state.greenmiles.com_state_greenmiles.domain.model.trip_details


/**
 * Domain model for trip details
 */
data class TripDetails(
    val id: String,
    val userId: String,
    val selectedPolyline: String,
    val startPointName: String,
    val endPointName: String,
    val startCoordinate: Coordinate,
    val endCoordinate: Coordinate,
    val totalDistanceKm: Double,
    val estimatedDurationSeconds: Int,
    val tripDate: String,
    val tripTime: String,
    val availableSeats: Int,
    val bookedSeats: Int,
    val costPerSeat: Double,
    val costPerKm: Double,
    val carDetails: CarDetails,
    val womanAccompany: Boolean,
    val status: String,
    val createdAt: String,
    val updatedAt: String,
    val user: TripUser,
    val miscMessage: String?,
    val recommendedCost: Double?
)

data class Coordinate(
    val latitude: Double,
    val longitude: Double
)

data class CarDetails(
    val type: String,
    val year: Int,
    val color: String,
    val model: String,
    val licensePlate: String
)

data class TripUser(
    val id: String,
    val name: String,
    val rating: Double,
    val isVerified: Boolean,
    val gender: String
)