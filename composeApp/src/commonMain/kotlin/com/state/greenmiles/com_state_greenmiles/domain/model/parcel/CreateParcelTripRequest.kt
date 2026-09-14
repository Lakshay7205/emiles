package com.state.greenmiles.com_state_greenmiles.domain.model.parcel

import com.state.greenmiles.com_state_greenmiles.domain.model.trip.Coordinate

data class CreatePublicParcelTripRequest(
    val selectedPolyline: String,
    val startPointName: String,
    val endPointName: String,
    val startCoordinate: Coordinate,
    val endCoordinate: Coordinate,
    val totalDistanceKm: Double,
    val estimatedDurationSeconds: Long,
    val tripDate: String,
    val tripTime: String,
    val availableSpace: String,
    val costPerKg: Double,
    val transportDetails: TransportDetails,
    val message: String
)

data class CreateCarParcelTripRequest(
    val selectedPolyline: String,
    val startPointName: String,
    val endPointName: String,
    val startCoordinate: Coordinate,
    val endCoordinate: Coordinate,
    val totalDistanceKm: Double,
    val estimatedDurationSeconds: Long,
    val tripDate: String,
    val tripTime: String,
    val availableSpace: String,
    val costPerKg: Double,
    val message: String,
    val carDetails: CarDetails
)

data class SearchPublicParcelTripDomainRequest(
    val startCoordinate: Coordinate,
    val endCoordinate: Coordinate,
    val startPointName: String,
    val endPointName: String,
    val tripDate: String,
    val packageSize: String,
    val transportType: String,
    val page: Int,
    val limit: Int,
    val cursor: String? = null
)

data class SearchCarParcelTripDomainRequest(
    val startCoordinate: Coordinate,
    val endCoordinate: Coordinate,
    val startPointName: String,
    val endPointName: String,
    val tripDate: String,
    val packageSize: String,
    val page: Int,
    val limit: Int,
    val cursor: String? = null
)

data class ParcelBookingDomainRequest(
    val tripId: String,
    val startPointName: String,
    val endPointName: String,
    val packageSize: String,
    val offeredPrice: Double,
    val bookingNotes: String
)
