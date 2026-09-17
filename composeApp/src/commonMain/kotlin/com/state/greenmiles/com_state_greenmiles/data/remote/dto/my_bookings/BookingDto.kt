package com.state.greenmiles.com_state_greenmiles.data.remote.dto.my_bookings

import kotlinx.serialization.Serializable

@Serializable
data class BookingDto(
    val id: String = "",
    val tripId: String = "",
    val status: String = "",
    val seatsRequested: Int = 0,
    val totalCost: String = "",
    val partialDistanceKm: String = "",
    val pickupPointName: String = "",
    val dropPointName: String = "",
    val passengerNotes: String? = null,
    val respondedAt: String? = null,
    val trip: BookingTripDto = BookingTripDto(),
    val tripOwner: TripOwnerDto? = null,
    val otherPassengers: List<CoPassengerDto>?= emptyList()
)

@Serializable
data class TripOwnerDto(
    val id: String = "",
    val name: String = "",
    val rating: Int = 0,
    val isVerified: Boolean = false,
    val mobile: String? = null
)

@Serializable
data class BookingTripDto(
    val id: String = "",
    val startPointName: String = "",
    val endPointName: String = "",
    val tripDate: String = "",
    val tripTime: String = "",
    val costPerSeat: String = "",
    val totalDistanceKm: String = ""
)
@Serializable
data class MyBookingsResponseDto(
    val bookings: List<BookingDto> = emptyList(),
    val pagination: PaginationnDto? = null
)

@Serializable
data class PaginationnDto(
    val page: Int = 1,
    val limit: Int = 20,
    val total: Int = 0
)

@Serializable
data class CoPassengerDto(
    val id: String? = null,
    val name: String? = null
)
@Serializable
data class TripBookingsResponseDto(
    val tripId: String,
    val bookings: List<BookingDto> = emptyList()
)