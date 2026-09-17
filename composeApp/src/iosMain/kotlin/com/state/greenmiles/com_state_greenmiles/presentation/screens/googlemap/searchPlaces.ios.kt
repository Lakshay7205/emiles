package com.state.greenmiles.com_state_greenmiles.presentation.screens.googlemap

import cocoapods.GooglePlaces.GMSPlacesClient
import cocoapods.GooglePlaces.GMSAutocompleteFilter
import cocoapods.GooglePlaces.GMSAutocompletePrediction
import kotlinx.cinterop.ExperimentalForeignApi
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalForeignApi::class)
actual suspend fun searchPlaces(
    placesClient: Any,
    query: String
): List<PlacePrediction> = suspendCoroutine { continuation ->
    val client = placesClient as GMSPlacesClient

    val filter = GMSAutocompleteFilter()

    client.findAutocompletePredictionsFromQuery(
        query = query,
        filter = filter,
        sessionToken = null
    ) { results, error ->
        if (error != null) {
            continuation.resume(emptyList())
        } else {
            val predictions = results?.mapNotNull { prediction ->
                (prediction as? GMSAutocompletePrediction)?.let {
                    PlacePrediction(
                        placeId = it.placeID,
                        primaryText = it.attributedPrimaryText.string,
                        secondaryText = it.attributedSecondaryText?.string ?: ""
                    )
                }
            } ?: emptyList()

            continuation.resume(predictions)
        }
    }
}
