package com.state.greenmiles.com_state_greenmiles.presentation.screens.googlemap

import com.state.greenmiles.com_state_greenmiles.domain.model.trip.Coordinate

expect fun getPlaceDetails(
    placesClient: Any,
    placeId: String,
    onResult: (name: String, coordinate: Coordinate) -> Unit
)