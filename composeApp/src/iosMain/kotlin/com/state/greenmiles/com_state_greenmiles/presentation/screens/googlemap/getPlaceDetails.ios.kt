@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.state.greenmiles.com_state_greenmiles.presentation.screens.googlemap

import cocoapods.GooglePlaces.*
import com.state.greenmiles.com_state_greenmiles.domain.model.trip.Coordinate
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents

actual fun getPlaceDetails(
    placesClient: Any,
    placeId: String,
    onResult: (name: String, coordinate: Coordinate) -> Unit
) {
    val client = placesClient as GMSPlacesClient

    client.fetchPlaceFromPlaceID(
        placeID = placeId,
        placeFields = GMSPlaceFieldName or GMSPlaceFieldCoordinate,
        sessionToken = null
    ) { place, error ->
        if (place != null && error == null) {

            val coord = place.coordinate.useContents {
                Coordinate(
                    latitude = latitude,
                    longitude = longitude
                )
            }

            onResult(place.name ?: "", coord)
        }
    }
}
