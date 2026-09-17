package com.state.greenmiles.com_state_greenmiles.domain.model.trip

data class Trips(
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