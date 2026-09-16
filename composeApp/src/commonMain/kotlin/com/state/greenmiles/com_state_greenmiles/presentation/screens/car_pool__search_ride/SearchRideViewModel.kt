package com.state.greenmiles.com_state_greenmiles.presentation.screens.car_pool__search_ride

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.CoordinateDto
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.search_trip_apis.SearchTripsRequest
import com.state.greenmiles.com_state_greenmiles.data.remote.model.BookingRequest
import com.state.greenmiles.com_state_greenmiles.domain.model.trip_details.Coordinate
import com.state.greenmiles.com_state_greenmiles.domain.model.trip_details.TripDetails
import com.state.greenmiles.com_state_greenmiles.domain.repository.TripsRepository
import com.state.greenmiles.com_state_greenmiles.presentation.screens.car_pool__search_ride.RideDetailsScreen.BookingUiState
import com.state.greenmiles.com_state_greenmiles.presentation.screens.car_pool__search_ride.RideDetailsScreen.TripDetailsUiState
import com.state.greenmiles.com_state_greenmiles.presentation.screens.googlemap.PlacePrediction
import com.state.greenmiles.com_state_greenmiles.presentation.screens.googlemap.getPlaceDetails
import com.state.greenmiles.com_state_greenmiles.presentation.screens.googlemap.searchPlaces
import com.state.greenmiles.com_state_greenmiles.presentation.common.models.LocationSelection
import com.state.greenmiles.com_state_greenmiles.presentation.common.models.ValidationState
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.round

class SearchRideViewModel(
    private val tripsRepository: TripsRepository,
    private val savedStateHandle: SavedStateHandle

) : ViewModel() {
    private val _uiState = MutableStateFlow(SearchRideUiState())
    val uiState: StateFlow<SearchRideUiState> = _uiState.asStateFlow()


    private val _bookingUiState = MutableStateFlow<BookingUiState>(BookingUiState.Idle)
    val bookingUiState: StateFlow<BookingUiState> = _bookingUiState.asStateFlow()

    var pickupPoint: String =  ""
    var dropPointName: String =  ""
    var startCoordinates: Coordinate? = null
    var endCoordinate: Coordinate? = null


    private val _selectedTripId = MutableStateFlow<String?>(null)
    val selectedTripId = _selectedTripId.asStateFlow()

    fun selectTrip(id: String) {
        println("Selected Trip = $id")
        _selectedTripId.value = id
    }

    private val _effects = Channel<SearchRideEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    private var searchJob: Job? = null
    private var startPlaceSearchJob: Job? = null
    private var endPlaceSearchJob: Job? = null

    companion object {
        private const val DEFAULT_PAGE_LIMIT = 20
        private const val MIN_QUERY_LENGTH = 2
    }


    fun bookTrip(
        tripDetails: TripDetails,
        seatsRequested: Int,
        passengerNotes: String
    ) {
        viewModelScope.launch {
            _bookingUiState.value = BookingUiState.Loading
            try {
                val totalCost = round(tripDetails.costPerSeat * seatsRequested * 100.0) / 100.0

                val request = BookingRequest(
                    tripId = tripDetails.id,
                    pickupPointName = tripDetails.startPointName,
                    dropPointName = tripDetails.endPointName,
                    partialDistanceKm = tripDetails.totalDistanceKm,
                    seatsRequested = seatsRequested,
                    totalCost = totalCost,
                    passengerNotes = passengerNotes
                )

                val success = tripsRepository.bookTrip(request)

                _bookingUiState.value = if (success) {
                    BookingUiState.Success
                } else {
                    BookingUiState.Error("Booking failed. Please try again.")
                }
            } catch (e: Exception) {
                _bookingUiState.value = BookingUiState.Error(
                    e.message ?: "An unexpected error occurred"
                )
            }
        }
    }

    fun resetBookingState() {
        _bookingUiState.value = BookingUiState.Idle
    }
    private val _tripDetailsUiState =
        MutableStateFlow<TripDetailsUiState>(TripDetailsUiState.Initial)
    val tripDetailsUiState: StateFlow<TripDetailsUiState> = _tripDetailsUiState.asStateFlow()

    fun getTripDetails() {

        println("TRippppppp=====" + selectedTripId.value)
        viewModelScope.launch {
            val tripId = selectedTripId


            if (tripId.value == null) {
                _tripDetailsUiState.value = TripDetailsUiState.Error("Trip ID is missing")
                return@launch
            }

            _tripDetailsUiState.value = TripDetailsUiState.Loading
            try {
                val response = tripsRepository.getTripDetails(tripId.value ?: "")



                if (response.success && response.data != null) {
                    val searchResult = uiState.value.searchResults.find { it.id == tripId.value }
                    val requestedSeats = uiState.value.peopleCount
                    val finalDetails = response.data.copy(
                        startCoordinate = startCoordinates!!,
                        endCoordinate = endCoordinate!!,
                        startPointName = pickupPoint,
                        endPointName = dropPointName,
                        // Use estimated cost and distance from search result if available
                        costPerSeat = if (searchResult != null && requestedSeats > 0) {
                            round((searchResult.price / requestedSeats) * 100.0) / 100.0
                        } else {
                            response.data.costPerSeat
                        },
                        totalDistanceKm = searchResult?.estimatedDistanceKm ?: response.data.totalDistanceKm
                    )
                    _tripDetailsUiState.value = TripDetailsUiState.Success(finalDetails)
                } else {
                    _tripDetailsUiState.value = TripDetailsUiState.Error(
                        response.message ?: "Failed to load trip details"
                    )
                }
            } catch (e: Exception) {
                _tripDetailsUiState.value = TripDetailsUiState.Error(
                    e.message ?: "An unexpected error occurred"
                )
            }
        }
    }

    fun retry() {
        getTripDetails()
    }

    fun clearError() {
        if (_tripDetailsUiState.value is TripDetailsUiState.Error) {
            _tripDetailsUiState.value = TripDetailsUiState.Initial
        }
    }


    /**
     * Main event handler
     */
    fun onEvent(event: SearchRideEvent) {
        when (event) {
            is SearchRideEvent.UpdateStartQuery -> handleUpdateStartQuery(
                event.query,
                event.placeClient
            )

            is SearchRideEvent.UpdateEndQuery -> handleUpdateEndQuery(
                event.query,
                event.placeClient
            )

            is SearchRideEvent.SelectStartPlace -> handleSelectStartPlace(
                event.prediction,
                event.placeClient
            )

            is SearchRideEvent.SelectEndPlace -> handleSelectEndPlace(
                event.prediction,
                event.placeClient
            )

            SearchRideEvent.SwapLocations -> handleSwapLocations()
            SearchRideEvent.ClearStartLocation -> handleClearStartLocation()
            SearchRideEvent.ClearEndLocation -> handleClearEndLocation()

            is SearchRideEvent.UpdateTripDate -> handleUpdateTripDate(event.date)
            is SearchRideEvent.UpdatePeopleCount -> handleUpdatePeopleCount(event.count)
            SearchRideEvent.IncrementPeopleCount -> handleIncrementPeopleCount()
            SearchRideEvent.DecrementPeopleCount -> handleDecrementPeopleCount()

            SearchRideEvent.SearchTrips -> handleSearchTrips()
            SearchRideEvent.LoadMoreTrips -> handleLoadMoreTrips()
            SearchRideEvent.ClearSearch -> handleClearSearch()
            SearchRideEvent.RetrySearch -> handleSearchTrips()

            SearchRideEvent.ShowDatePicker -> _uiState.update { it.copy(showDatePicker = true) }
            SearchRideEvent.HideDatePicker -> _uiState.update { it.copy(showDatePicker = false) }
            SearchRideEvent.ToggleFilters -> _uiState.update { it.copy(showFilters = !it.showFilters) }
            is SearchRideEvent.SelectTrip -> handleSelectTrip(event.tripId)
            SearchRideEvent.ClearError -> _uiState.update { it.copy(error = null) }
        }
    }

    // ============ Location Search Handlers ============

    private fun handleUpdateStartQuery(query: String, placesClient: Any) {
        _uiState.update {
            it.copy(
                startPlaceQuery = query,
                showStartPredictions = query.isNotBlank()
            )
        }

        if (query.length >= MIN_QUERY_LENGTH) {
            searchStartPlaces(query, placesClient)
        } else {
            _uiState.update { it.copy(startPlacePredictions = emptyList()) }
        }
    }

    private fun handleUpdateEndQuery(query: String, placesClient: Any) {
        _uiState.update {
            it.copy(
                endPlaceQuery = query,
                showEndPredictions = query.isNotBlank()
            )
        }

        if (query.length >= MIN_QUERY_LENGTH) {
            searchEndPlaces(query, placesClient)
        } else {
            _uiState.update { it.copy(endPlacePredictions = emptyList()) }
        }
    }

    private fun searchStartPlaces(query: String, placesClient: Any) {
        startPlaceSearchJob?.cancel()
        startPlaceSearchJob = viewModelScope.launch {
            try {
                _uiState.update { it.copy(isSearchingStartPlaces = true) }
                val predictions = searchPlaces(placesClient, query)
                _uiState.update {
                    it.copy(
                        startPlacePredictions = predictions,
                        isSearchingStartPlaces = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isSearchingStartPlaces = false)
                }
                sendEffect(SearchRideEffect.ShowToast("Failed to search locations"))
            }
        }
    }

    private fun searchEndPlaces(query: String, placesClient: Any) {
        endPlaceSearchJob?.cancel()

        endPlaceSearchJob = viewModelScope.launch {
            try {
                _uiState.update { it.copy(isSearchingEndPlaces = true) }
                val predictions = searchPlaces(placesClient, query)
                _uiState.update {
                    it.copy(
                        endPlacePredictions = predictions,
                        isSearchingEndPlaces = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isSearchingEndPlaces = false)
                }
                sendEffect(SearchRideEffect.ShowToast("Failed to search locations"))
            }
        }
    }

    private fun handleSelectStartPlace(prediction: PlacePrediction, placesClient: Any) {
        getPlaceDetails(
            placesClient = placesClient,
            placeId = prediction.placeId
        ) { name, coordinate ->
            val location = LocationSelection(id = prediction.placeId, name = name, coordinate = coordinate)
            _uiState.update {
                it.copy(
                    startLocation = location,
                    startPlaceQuery = name,
                    startPlacePredictions = emptyList(),
                    showStartPredictions = false,
                    validation = validateForm(
                        startLocation = location,
                        endLocation = it.endLocation,
                        tripDate = it.tripDate,
                        peopleCount = it.peopleCount
                    )
                )
            }
        }
    }

    private fun handleSelectEndPlace(prediction: PlacePrediction, placesClient: Any) {
        getPlaceDetails(
            placesClient = placesClient,
            placeId = prediction.placeId
        ) { name, coordinate ->
            val location = LocationSelection(id = prediction.placeId, name = name, coordinate = coordinate)
            _uiState.update {
                it.copy(
                    endLocation = location,
                    endPlaceQuery = name,
                    endPlacePredictions = emptyList(),
                    showEndPredictions = false,
                    validation = validateForm(
                        startLocation = it.startLocation,
                        endLocation = location,
                        tripDate = it.tripDate,
                        peopleCount = it.peopleCount
                    )
                )
            }
        }
    }

    private fun handleSwapLocations() {
        val currentState = _uiState.value
        _uiState.update {
            it.copy(
                startLocation = currentState.endLocation,
                endLocation = currentState.startLocation,
                startPlaceQuery = currentState.endPlaceQuery,
                endPlaceQuery = currentState.startPlaceQuery,
                startPlacePredictions = emptyList(),
                endPlacePredictions = emptyList(),
                showStartPredictions = false,
                showEndPredictions = false
            )
        }
    }

    private fun handleClearStartLocation() {
        _uiState.update {
            it.copy(
                startLocation = null,
                startPlaceQuery = "",
                startPlacePredictions = emptyList(),
                showStartPredictions = false,
                validation = validateForm(
                    startLocation = null,
                    endLocation = it.endLocation,
                    tripDate = it.tripDate,
                    peopleCount = it.peopleCount
                )
            )
        }
    }

    private fun handleClearEndLocation() {
        _uiState.update {
            it.copy(
                endLocation = null,
                endPlaceQuery = "",
                endPlacePredictions = emptyList(),
                showEndPredictions = false,
                validation = validateForm(
                    startLocation = it.startLocation,
                    endLocation = null,
                    tripDate = it.tripDate,
                    peopleCount = it.peopleCount
                )
            )
        }
    }

    // ============ Trip Details Handlers ============

    private fun handleUpdateTripDate(date: String) {
        _uiState.update {
            it.copy(
                tripDate = date,
                showDatePicker = false,
                validation = validateForm(
                    startLocation = it.startLocation,
                    endLocation = it.endLocation,
                    tripDate = date,
                    peopleCount = it.peopleCount
                )
            )
        }
    }

    private fun handleUpdatePeopleCount(count: Int) {
        if (count > 0) {
            _uiState.update {
                it.copy(
                    peopleCount = count,
                    validation = validateForm(
                        startLocation = it.startLocation,
                        endLocation = it.endLocation,
                        tripDate = it.tripDate,
                        peopleCount = count
                    )
                )
            }
        }
    }

    private fun handleIncrementPeopleCount() {
        val newCount = _uiState.value.peopleCount + 1
        handleUpdatePeopleCount(newCount)
    }

    private fun handleDecrementPeopleCount() {
        val currentCount = _uiState.value.peopleCount
        if (currentCount > 1) {
            handleUpdatePeopleCount(currentCount - 1)
        }
    }

    // ============ Search Handlers ============

    private fun handleSearchTrips() {
        val state = _uiState.value


        pickupPoint = _uiState.value.startLocation?.name ?: ""
        dropPointName = _uiState.value.endLocation?.name ?: ""
        startCoordinates = Coordinate(
            latitude = _uiState.value.startLocation?.coordinate?.latitude?:0.0 ,
            longitude = _uiState.value.startLocation?.coordinate?.longitude?:0.0
        )
        endCoordinate = Coordinate(
            latitude = _uiState.value.endLocation?.coordinate?.latitude?:0.0 ,
            longitude = _uiState.value.endLocation?.coordinate?.longitude?:0.0
        )


        if (!state.canSearch) {
            sendEffect(SearchRideEffect.ShowToast("Please fill all required fields"))
            return
        }

        searchJob?.cancel()



        searchJob = viewModelScope.launch {
            try {
                _uiState.update {
                    it.copy(
                        isSearching = true,
                        error = null,
                        searchResults = emptyList(),
                        nextCursor = null
                    )
                }

                val request = createSearchRequest(state, cursor = _uiState.value.nextCursor)

                val result = tripsRepository.searchTrips(request)

                _uiState.update {
                    it.copy(
                        searchResults = result.trips,
                        isSearching = false,
                        nextCursor = result.nextCursor,
                        hasMoreResults = result.nextCursor != null
                    )
                }

                sendEffect(SearchRideEffect.ScrollToTop)

                if (result.trips.isEmpty()) {
                    sendEffect(SearchRideEffect.ShowToast("No trips found for your search"))
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSearching = false,
                        error = "Failed to search trips: ${e.message}"
                    )
                }
                sendEffect(SearchRideEffect.ShowToast("Failed to search trips"))
            }
        }
    }

    private fun handleLoadMoreTrips() {
        val state = _uiState.value

        if (!state.hasMoreResults || state.isLoadingMore || state.nextCursor == null) {
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoadingMore = true, error = null) }

                val request = createSearchRequest(state, cursor = state.nextCursor)
                val result = tripsRepository.searchTrips(request)

                _uiState.update {
                    it.copy(
                        searchResults = it.searchResults + result.trips,
                        isLoadingMore = false,
                        nextCursor = result.nextCursor,
                        hasMoreResults = result.nextCursor != null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoadingMore = false,
                        error = "Failed to load more trips"
                    )
                }
                sendEffect(SearchRideEffect.ShowToast("Failed to load more trips"))
            }
        }
    }

    private fun handleClearSearch() {
        searchJob?.cancel()
        _uiState.update {
            SearchRideUiState(peopleCount = 1)
        }
    }

    private fun handleSelectTrip(tripId: String) {
        _uiState.update { it.copy(selectedTripId = tripId) }
        sendEffect(SearchRideEffect.NavigateToTripDetails(tripId))
    }

    // ============ Helper Functions ============

    private fun createSearchRequest(
        state: SearchRideUiState,
        cursor: String?
    ): SearchTripsRequest {
        return SearchTripsRequest(
            startCoordinate = CoordinateDto(
                latitude = state.startLocation!!.coordinate.latitude,
                longitude = state.startLocation.coordinate.longitude
            ),
            endCoordinate = CoordinateDto(
                latitude = state.endLocation!!.coordinate.latitude,
                longitude = state.endLocation.coordinate.longitude
            ),
            startPointName = state.startLocation.name,
            endPointName = state.endLocation.name,
            tripDate = state.tripDate,
            peopleCount = state.peopleCount,
            page = 1,
            limit = DEFAULT_PAGE_LIMIT,
            cursor = cursor
        )
    }

    private fun validateForm(
        startLocation: LocationSelection?,
        endLocation: LocationSelection?,
        tripDate: String,
        peopleCount: Int
    ): SearchFormValidation {
        return SearchFormValidation(
            startLocationValid = if (startLocation != null)
                ValidationState.Valid
            else
                ValidationState.Invalid("Start location required"),

            endLocationValid = if (endLocation != null)
                ValidationState.Valid
            else
                ValidationState.Invalid("End location required"),

            dateValid = if (tripDate.isNotBlank())
                ValidationState.Valid
            else
                ValidationState.Invalid("Trip date required"),

            peopleCountValid = if (peopleCount > 0)
                ValidationState.Valid
            else
                ValidationState.Invalid("At least 1 person required")
        )
    }

    private fun sendEffect(effect: SearchRideEffect) {
        viewModelScope.launch {
            _effects.send(effect)
        }
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
        startPlaceSearchJob?.cancel()
        endPlaceSearchJob?.cancel()

        super.onCleared()
        println("❌ SearchRideViewModel CLEARED -> ${this.hashCode()}")
    }
}