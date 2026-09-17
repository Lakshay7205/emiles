package com.state.greenmiles.com_state_greenmiles.data.remote.dto.my_created_trips

import kotlinx.serialization.Serializable

@Serializable
data class MyTripDto(
    val id: String = "",
    val startPointName: String = "",
    val endPointName: String = "",
    val tripDate: String = "",
    val tripTime: String = "",
    val totalDistanceKm: String = "",
    val availableSeats: Int = 0,
    val costPerSeat: String = "",
    val status: String = "",
    val createdAt: String = "",
    val bookingCounts: BookingCountsDto = BookingCountsDto()
)

@Serializable
data class BookingCountsDto(
    val pending: Int = 0,
    val approved: Int = 0
)