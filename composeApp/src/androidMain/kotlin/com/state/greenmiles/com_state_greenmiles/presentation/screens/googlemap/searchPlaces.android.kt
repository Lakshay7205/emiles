package com.state.greenmiles.com_state_greenmiles.presentation.screens.googlemap

import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

actual suspend fun searchPlaces(
    placesClient: Any,
    query: String
): List<PlacePrediction> = withContext(Dispatchers.IO) {


    try {
        val client = placesClient as PlacesClient

        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .build()

        val response = client.findAutocompletePredictions(request).await()

        response.autocompletePredictions.map { prediction: AutocompletePrediction ->
            PlacePrediction(
                placeId = prediction.getPlaceId(),
                primaryText = prediction.getPrimaryText(null).toString(),
                secondaryText = prediction.getSecondaryText(null).toString()
            )
        }



    } catch (e: Exception) {
        emptyList()
    }
}