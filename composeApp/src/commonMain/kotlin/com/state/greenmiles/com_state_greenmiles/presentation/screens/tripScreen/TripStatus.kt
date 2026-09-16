package com.state.greenmiles.com_state_greenmiles.presentation.screens.tripScreen

enum class TripStatus(val label: String, val apiValue: String) {
    UPCOMING("Upcoming", "upcoming"),
    COMPLETED("Completed", "completed"),
    CANCELLED("Cancelled", "cancelled")
}