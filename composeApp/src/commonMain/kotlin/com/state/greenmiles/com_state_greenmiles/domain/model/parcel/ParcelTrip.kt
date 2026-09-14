package com.state.greenmiles.com_state_greenmiles.domain.model.parcel

data class ParcelTrip(
    val id: String,
    val userId: String,
    val startPointName: String,
    val endPointName: String,
    val totalDistanceKm: Double,
    val estimatedDurationSeconds: Long,
    val tripDate: String,
    val tripTime: String,
    val availableSpace: String,
    val costPerKg: Double,
    val message: String,
    val transportDetails: TransportDetails? = null,
    val carDetails: CarDetails? = null,
    val status: String? = null
)

data class TransportDetails(
    val type: String,
    val name: String,
    val operator: String,
    val route: String,
    val vehicleNumber: String
)

data class CarDetails(
    val model: String,
    val licensePlate: String,
    val color: String,
    val year: Int,
    val type: String
)
