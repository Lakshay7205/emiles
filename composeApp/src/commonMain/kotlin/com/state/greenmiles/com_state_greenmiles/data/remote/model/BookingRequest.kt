package com.state.greenmiles.com_state_greenmiles.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class BookingRequest(
    val tripId: String,
    val pickupPointName: String,
    val dropPointName: String,
    val partialDistanceKm: Double,
    val seatsRequested: Int,
    val totalCost: Double,
    val passengerNotes: String = ""
)