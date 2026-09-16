package com.state.greenmiles.com_state_greenmiles.presentation.screens.car_pool__search_ride

import com.state.greenmiles.com_state_greenmiles.domain.model.trip.Coordinate
import com.state.greenmiles.com_state_greenmiles.domain.model.trip.Trip
import com.state.greenmiles.com_state_greenmiles.presentation.screens.googlemap.PlacePrediction
import com.state.greenmiles.com_state_greenmiles.presentation.common.models.ValidationState
import com.state.greenmiles.com_state_greenmiles.presentation.common.models.LocationSelection



// LocationSelection and ValidationState moved to common/models/CommonStates.kt

/**
 * Data class for form validation
 */
data class SearchFormValidation(
    val startLocationValid: ValidationState = ValidationState.Invalid("Start location required"),
    val endLocationValid: ValidationState = ValidationState.Invalid("End location required"),
    val dateValid: ValidationState = ValidationState.Invalid("Trip date required"),
    val peopleCountValid: ValidationState = ValidationState.Valid
) {
    val isValid: Boolean
        get() = startLocationValid is ValidationState.Valid &&
                endLocationValid is ValidationState.Valid &&
                dateValid is ValidationState.Valid &&
                peopleCountValid is ValidationState.Valid
}

/**
 * Extended UI State with validation
 */
data class SearchRideUiState(
    // Location selection
    val startLocation: LocationSelection? = null,
    val endLocation: LocationSelection? = null,
    val tripDate: String = "",
    val peopleCount: Int = 1,

    // Search results
    val searchResults: List<Trip> = emptyList(),
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

    // Validation
    val validation: SearchFormValidation = SearchFormValidation()
) {
    val canSearch: Boolean
        get() = validation.isValid && !isSearching

    val hasSearched: Boolean
        get() = searchResults.isNotEmpty() || (!isSearching && searchResults.isEmpty() && error == null)
}

/**
 * Events that can be triggered from the UI
 */
sealed class SearchRideEvent {
    // Location events
    data class UpdateStartQuery(val query: String,val placeClient: Any) : SearchRideEvent()
    data class UpdateEndQuery(val query: String,val placeClient: Any) : SearchRideEvent()
    data class SelectStartPlace(val prediction: PlacePrediction,val placeClient: Any) : SearchRideEvent()
    data class SelectEndPlace(val prediction: PlacePrediction,val placeClient: Any) : SearchRideEvent()
    data object SwapLocations : SearchRideEvent()
    data object ClearStartLocation : SearchRideEvent()
    data object ClearEndLocation : SearchRideEvent()

    // Date and people events
    data class UpdateTripDate(val date: String) : SearchRideEvent()
    data class UpdatePeopleCount(val count: Int) : SearchRideEvent()
    data object IncrementPeopleCount : SearchRideEvent()
    data object DecrementPeopleCount : SearchRideEvent()

    // Search events
    data object SearchTrips : SearchRideEvent()
    data object LoadMoreTrips : SearchRideEvent()
    data object ClearSearch : SearchRideEvent()
    data object RetrySearch : SearchRideEvent()

    // UI events
    data object ShowDatePicker : SearchRideEvent()
    data object HideDatePicker : SearchRideEvent()
    data object ToggleFilters : SearchRideEvent()
    data class SelectTrip(val tripId: String) : SearchRideEvent()
    data object ClearError : SearchRideEvent()
}

/**
 * Side effects that should be handled by the UI
 */
sealed class SearchRideEffect {
    data class ShowToast(val message: String) : SearchRideEffect()
    data class NavigateToTripDetails(val tripId: String) : SearchRideEffect()
    data object ScrollToTop : SearchRideEffect()
    data class OpenMap(val startCoordinate: Coordinate, val endCoordinate: Coordinate) : SearchRideEffect()
}

/**
 * Filter options for search results
 */
data class SearchFilters(
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val minSeats: Int? = null,
    val departureTimeRange: TimeRange? = null,
    val sortBy: SortOption = SortOption.EARLIEST_DEPARTURE
)

data class TimeRange(
    val startTime: String, // HH:mm format
    val endTime: String    // HH:mm format
)

enum class SortOption {
    EARLIEST_DEPARTURE,
    LATEST_DEPARTURE,
    LOWEST_PRICE,
    HIGHEST_PRICE,
    MOST_SEATS
}

/**
 * Represents a trip with additional UI-specific data
 */
data class TripWithUiState(
    val trip: Trip,
    val isSelected: Boolean = false,
    val isFavorite: Boolean = false,
    val availabilityStatus: AvailabilityStatus = AvailabilityStatus.AVAILABLE
)

enum class AvailabilityStatus {
    AVAILABLE,
    ALMOST_FULL,
    FULL,
    CANCELLED
}