package com.state.greenmiles.com_state_greenmiles.presentation.screens.tripScreen

import com.state.greenmiles.com_state_greenmiles.domain.model.booking.Booking
import com.state.greenmiles.com_state_greenmiles.domain.model.trip.Trip
import com.state.greenmiles.com_state_greenmiles.domain.model.trip.Trips

data class MyTripsUiState(
    val isLoading: Boolean = false,
    val trips: List<Trips> = emptyList(),
    val bookings: List<Booking> = emptyList(),
    val error: String? = null,
    val message: String? = null
)