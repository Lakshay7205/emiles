package com.state.greenmiles.com_state_greenmiles.domain.model.trip

import kotlinx.serialization.Serializable

data class Coordinate(
    val latitude: Double,
    val longitude: Double
)

data class Route(
    val index: Int,
    val polyline: String,
    val distanceKm: Double,
    val durationSeconds: Long,
    val estimatedCost: Double,
    val summary: String,
    val recommendedCost: Double? = null
)

data class RoutesResult(
    val routes: List<Route>
)

@Serializable
data class Trip(
    val id: String,
    val time: String = "12:26 PM",
    val price: Double,
    val seats: Int,
    val carType: String,
    val carName : String,
    val isUserVerified: Boolean,
    val rating: Double,
    val userName: String,
    val gender: String,
    val estimatedDistanceKm: Double? = null,
    val miscMessage: String? = null,
    val recommendedCost: Double? = null
)

@Serializable
data class SearchTripsResultPage(
    val trips: List<Trip>,
    val nextCursor: String?
)
@Serializable
data class MyTrip(
    val id: String,
    val startPointName: String,
    val endPointName: String,
    val tripDate: String,
    val tripTime: String,
    val totalDistanceKm: String,
    val availableSeats: Int,
    val costPerSeat: String,
    val status: String,
    val pendingBookings: Int,
    val approvedBookings: Int
)
