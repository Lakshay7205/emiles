package com.state.greenmiles.com_state_greenmiles.presentation.screens.parcel

import com.state.greenmiles.com_state_greenmiles.domain.model.trip.Coordinate
import com.state.greenmiles.com_state_greenmiles.domain.model.parcel.ParcelTrip
import com.state.greenmiles.com_state_greenmiles.presentation.screens.googlemap.PlacePrediction
import com.state.greenmiles.com_state_greenmiles.presentation.common.models.ValidationState
import com.state.greenmiles.com_state_greenmiles.presentation.common.models.LocationSelection

// LocationSelection and ValidationState moved to common/models/CommonStates.kt

data class SearchParcelFormValidation(
    val startLocationValid: ValidationState = ValidationState.Invalid("Start location required"),
    val endLocationValid: ValidationState = ValidationState.Invalid("End location required"),
    val dateValid: ValidationState = ValidationState.Invalid("Trip date required"),
    val packageSizeValid: ValidationState = ValidationState.Valid,
    val transportTypeValid: ValidationState = ValidationState.Valid
) {
    val isValid: Boolean
        get() = startLocationValid is ValidationState.Valid &&
                endLocationValid is ValidationState.Valid &&
                dateValid is ValidationState.Valid &&
                packageSizeValid is ValidationState.Valid &&
                transportTypeValid is ValidationState.Valid
}

data class ParcelTripsUiState(
    // Location selection
    val startLocation: LocationSelection? = null,
    val endLocation: LocationSelection? = null,
    val tripDate: String = "",
    val packageSize: String = "small", // small, medium, large
    val transportType: String = "bus",

    // Search results
    val searchResults: List<ParcelTrip> = emptyList(),
    val isSearching: Boolean = false,
    val error: String? = null,
    val nextCursor: String? = null,
    val hasMoreResults: Boolean = false,
    val isLoadingMore: Boolean = false,

    // Place search states
    val startPlaceQuery: String = "",
    val endPlaceQuery: String = "",
    val startPlacePredictions: List<PlacePrediction> = emptyList(),
    val endPlacePredictions: List<PlacePrediction> = emptyList(),
    val isSearchingStartPlaces: Boolean = false,
    val isSearchingEndPlaces: Boolean = false,
    val showStartPredictions: Boolean = false,
    val showEndPredictions: Boolean = false,

    // UI states
    val showDatePicker: Boolean = false,
    val showFilters: Boolean = false,
    val selectedTripId: String? = null,
    val message: String? = null,

    // Booking state
    val isBooking: Boolean = false,
    val bookingSuccess: Boolean = false,

    // Validation
    val validation: SearchParcelFormValidation = SearchParcelFormValidation()
) {
    val canSearch: Boolean
        get() = validation.isValid && !isSearching

    val hasSearched: Boolean
        get() = searchResults.isNotEmpty() || (!isSearching && searchResults.isEmpty() && error == null)
}

sealed class ParcelTripsEvent {
    // Location events
    data class UpdateStartQuery(val query: String, val placeClient: Any) : ParcelTripsEvent()
    data class UpdateEndQuery(val query: String, val placeClient: Any) : ParcelTripsEvent()
    data class SelectStartPlace(val prediction: PlacePrediction, val placeClient: Any) : ParcelTripsEvent()
    data class SelectEndPlace(val prediction: PlacePrediction, val placeClient: Any) : ParcelTripsEvent()
    data class StartLocationSelected(val name: String, val coordinate: Coordinate) : ParcelTripsEvent()
    data class EndLocationSelected(val name: String, val coordinate: Coordinate) : ParcelTripsEvent()
    data object SwapLocations : ParcelTripsEvent()
    data object ClearStartLocation : ParcelTripsEvent()
    data object ClearEndLocation : ParcelTripsEvent()

    // Date, size and type events
    data class UpdateTripDate(val date: String) : ParcelTripsEvent()
    data class UpdatePackageSize(val size: String) : ParcelTripsEvent()
    data class UpdateTransportType(val type: String) : ParcelTripsEvent()

    // Search events
    data object SearchTrips : ParcelTripsEvent()
    data object LoadMoreTrips : ParcelTripsEvent()
    data object ClearSearch : ParcelTripsEvent()
    data object RetrySearch : ParcelTripsEvent()

    // UI events
    data object ShowDatePicker : ParcelTripsEvent()
    data object HideDatePicker : ParcelTripsEvent()
    data class SelectTrip(val tripId: String) : ParcelTripsEvent()
    data object ClearError : ParcelTripsEvent()
    data object BookTrip : ParcelTripsEvent()
}

sealed class ParcelTripsEffect {
    data class ShowToast(val message: String) : ParcelTripsEffect()
    data class NavigateToTripDetails(val tripId: String) : ParcelTripsEffect()
    data object ScrollToTop : ParcelTripsEffect()
}
