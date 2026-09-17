package com.state.greenmiles.com_state_greenmiles.presentation.screens.googlemap

import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.state.greenmiles.com_state_greenmiles.domain.model.trip.Coordinate

actual fun getPlaceDetails(
    placesClient: Any,
    placeId: String,
    onResult: (name: String, coordinate: Coordinate) -> Unit
) {
    val client = placesClient as PlacesClient

    val placeFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
    val request = FetchPlaceRequest.newInstance(placeId, placeFields)

    client.fetchPlace(request).addOnSuccessListener { response ->
        val place = response.place
        val latLng = place.latLng

        if (latLng != null) {
            onResult(
                place.name ?: "",
                Coordinate(
                    latitude = latLng.latitude,
                    longitude = latLng.longitude
                )
            )
        }
    }
}