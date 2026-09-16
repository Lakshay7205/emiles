package com.state.greenmiles.com_state_greenmiles.presentation.common.models

import com.state.greenmiles.com_state_greenmiles.domain.model.trip.Coordinate

sealed class ValidationState {
    data object Valid : ValidationState()
    data class Invalid(val message: String) : ValidationState()
}

data class LocationSelection(
    val id: String? = null,
    val name: String,
    val coordinate: Coordinate
)
