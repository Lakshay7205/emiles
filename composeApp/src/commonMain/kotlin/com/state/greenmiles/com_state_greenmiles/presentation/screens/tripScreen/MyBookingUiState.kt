package com.state.greenmiles.com_state_greenmiles.presentation.screens.tripScreen

import com.state.greenmiles.com_state_greenmiles.domain.model.booking.Booking

data class MyBookingsUiState(
    val isLoading: Boolean = false,
    val bookings: List<Booking> = emptyList(),
    val error: String? = null
)