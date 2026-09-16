package com.state.greenmiles.com_state_greenmiles.presentation.screens.googlemap

expect suspend fun searchPlaces(
    placesClient: Any,
    query: String
): List<PlacePrediction>

data class PlacePrediction(
    val placeId: String,
    val primaryText: String,
    val secondaryText: String
)