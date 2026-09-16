package com.state.greenmiles.com_state_greenmiles.presentation.screens.parcel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.state.greenmiles.com_state_greenmiles.domain.model.parcel.*
import com.state.greenmiles.com_state_greenmiles.domain.model.trip.Coordinate
import com.state.greenmiles.com_state_greenmiles.domain.repository.ParcelRepository
import com.state.greenmiles.com_state_greenmiles.presentation.screens.googlemap.PlacePrediction
import com.state.greenmiles.com_state_greenmiles.presentation.screens.googlemap.getPlaceDetails
import com.state.greenmiles.com_state_greenmiles.presentation.screens.googlemap.searchPlaces
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import com.state.greenmiles.com_state_greenmiles.presentation.common.models.LocationSelection
import com.state.greenmiles.com_state_greenmiles.presentation.common.models.ValidationState
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock as StdClock

class ParcelTripsViewModel(
    private val repository: ParcelRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ParcelTripsUiState())
    val uiState: StateFlow<ParcelTripsUiState> = _uiState.asStateFlow()

    init {
        val today = StdClock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
        _uiState.update { 
            it.copy(
                tripDate = today,
                validation = validateForm(it.startLocation, it.endLocation, today, it.packageSize)
            ) 
        }
    }

    private val _effects = Channel<ParcelTripsEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    private var searchJob: Job? = null
    private var startPlaceSearchJob: Job? = null
    private var endPlaceSearchJob: Job? = null

    companion object {
        private const val DEFAULT_PAGE_LIMIT = 20
        private const val MIN_QUERY_LENGTH = 2
    }

    fun onEvent(event: ParcelTripsEvent) {
        when (event) {
            is ParcelTripsEvent.UpdateStartQuery -> handleUpdateStartQuery(event.query, event.placeClient)
            is ParcelTripsEvent.UpdateEndQuery -> handleUpdateEndQuery(event.query, event.placeClient)
            is ParcelTripsEvent.SelectStartPlace -> handleSelectStartPlace(event.prediction, event.placeClient)
            is ParcelTripsEvent.SelectEndPlace -> handleSelectEndPlace(event.prediction, event.placeClient)
            is ParcelTripsEvent.StartLocationSelected -> handleStartLocationSelected(event.name, event.coordinate)
            is ParcelTripsEvent.EndLocationSelected -> handleEndLocationSelected(event.name, event.coordinate)
            ParcelTripsEvent.SwapLocations -> handleSwapLocations()
            ParcelTripsEvent.ClearStartLocation -> handleClearStartLocation()
            ParcelTripsEvent.ClearEndLocation -> handleClearEndLocation()
            is ParcelTripsEvent.UpdateTripDate -> handleUpdateTripDate(event.date)
            is ParcelTripsEvent.UpdatePackageSize -> handleUpdatePackageSize(event.size)
            is ParcelTripsEvent.UpdateTransportType -> {
                _uiState.update { 
                    val newState = it.copy(transportType = event.type)
                    newState.copy(validation = validateForm(newState.startLocation, newState.endLocation, newState.tripDate, newState.packageSize))
                }
            }
            ParcelTripsEvent.SearchTrips -> handleSearchTrips()
            ParcelTripsEvent.LoadMoreTrips -> handleLoadMoreTrips()
            ParcelTripsEvent.ClearSearch -> handleClearSearch()
            ParcelTripsEvent.RetrySearch -> handleSearchTrips()
            ParcelTripsEvent.ShowDatePicker -> _uiState.update { it.copy(showDatePicker = true) }
            ParcelTripsEvent.HideDatePicker -> _uiState.update { it.copy(showDatePicker = false) }
            is ParcelTripsEvent.SelectTrip -> handleSelectTrip(event.tripId)
            ParcelTripsEvent.BookTrip -> handleBookTrip()
            ParcelTripsEvent.ClearError -> _uiState.update { it.copy(error = null) }
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
        if (query.length >= MIN_QUERY_LENGTH) searchStartPlaces(query, placesClient)
        else _uiState.update { it.copy(startPlacePredictions = emptyList()) }
    }

    private fun handleUpdateEndQuery(query: String, placesClient: Any) {
        _uiState.update {
            it.copy(
                endPlaceQuery = query,
                showEndPredictions = query.isNotBlank()
            )
        }
        if (query.length >= MIN_QUERY_LENGTH) searchEndPlaces(query, placesClient)
        else _uiState.update { it.copy(endPlacePredictions = emptyList()) }
    }

    private fun searchStartPlaces(query: String, placesClient: Any) {
        startPlaceSearchJob?.cancel()
        startPlaceSearchJob = viewModelScope.launch {
            try {
                _uiState.update { it.copy(isSearchingStartPlaces = true) }
                val predictions = searchPlaces(placesClient, query)
                _uiState.update {
                    it.copy(startPlacePredictions = predictions, isSearchingStartPlaces = false)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSearchingStartPlaces = false) }
                sendEffect(ParcelTripsEffect.ShowToast("Failed to search locations"))
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
                    it.copy(endPlacePredictions = predictions, isSearchingEndPlaces = false)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSearchingEndPlaces = false) }
                sendEffect(ParcelTripsEffect.ShowToast("Failed to search locations"))
            }
        }
    }

    private fun handleSelectStartPlace(prediction: PlacePrediction, placesClient: Any) {
        getPlaceDetails(placesClient, prediction.placeId) { name, coordinate ->
            val location = LocationSelection(id = prediction.placeId, name = name, coordinate = coordinate)
            _uiState.update {
                it.copy(
                    startLocation = location,
                    startPlaceQuery = name,
                    startPlacePredictions = emptyList(),
                    showStartPredictions = false,
                    validation = validateForm(
                        location,
                        it.endLocation,
                        it.tripDate,
                        it.packageSize
                    )
                )
            }
        }
    }

    private fun handleEndLocationSelected(name: String, coordinate: com.state.greenmiles.com_state_greenmiles.domain.model.trip.Coordinate) {
        val location = LocationSelection(name = name, coordinate = coordinate)
        _uiState.update {
            it.copy(
                endLocation = location,
                endPlaceQuery = name,
                endPlacePredictions = emptyList(),
                showEndPredictions = false,
                validation = validateForm(
                    it.startLocation,
                    location,
                    it.tripDate,
                    it.packageSize
                )
            )
        }
    }

    private fun handleStartLocationSelected(name: String, coordinate: com.state.greenmiles.com_state_greenmiles.domain.model.trip.Coordinate) {
        val location = LocationSelection(name = name, coordinate = coordinate)
        _uiState.update {
            it.copy(
                startLocation = location,
                startPlaceQuery = name,
                startPlacePredictions = emptyList(),
                showStartPredictions = false,
                validation = validateForm(
                    location,
                    it.endLocation,
                    it.tripDate,
                    it.packageSize
                )
            )
        }
    }

    private fun handleSelectEndPlace(prediction: PlacePrediction, placesClient: Any) {
        getPlaceDetails(placesClient, prediction.placeId) { name, coordinate ->
            val location = LocationSelection(id = prediction.placeId, name = name, coordinate = coordinate)
            _uiState.update {
                it.copy(
                    endLocation = location,
                    endPlaceQuery = name,
                    endPlacePredictions = emptyList(),
                    showEndPredictions = false,
                    validation = validateForm(
                        it.startLocation,
                        location,
                        it.tripDate,
                        it.packageSize
                    )
                )
            }
        }
    }

    private fun handleSwapLocations() {
        val st = _uiState.value
        _uiState.update {
            it.copy(
                startLocation = st.endLocation,
                endLocation = st.startLocation,
                startPlaceQuery = st.endPlaceQuery,
                endPlaceQuery = st.startPlaceQuery,
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
                startLocation = null, startPlaceQuery = "", startPlacePredictions = emptyList(),
                showStartPredictions = false,
                validation = validateForm(null, it.endLocation, it.tripDate, it.packageSize)
            )
        }
    }

    private fun handleClearEndLocation() {
        _uiState.update {
            it.copy(
                endLocation = null, endPlaceQuery = "", endPlacePredictions = emptyList(),
                showEndPredictions = false,
                validation = validateForm(it.startLocation, null, it.tripDate, it.packageSize)
            )
        }
    }

    // ============ Handlers ============

    private fun handleUpdateTripDate(date: String) {
        _uiState.update {
            it.copy(
                tripDate = date, showDatePicker = false,
                validation = validateForm(it.startLocation, it.endLocation, date, it.packageSize)
            )
        }
    }


    private fun handleUpdatePackageSize(size: String) {
        _uiState.update {
            it.copy(
                packageSize = size,
                validation = validateForm(it.startLocation, it.endLocation, it.tripDate, size)
            )
        }
    }

    private fun getPackageSizeIndexForSearch(size: String): Int {
        return when (size.lowercase()) {
            "small" -> 1
            "medium" -> 2
            "large" -> 3
            "extra_large" -> 4
            else -> 1
        }
    }

    private fun getPackageSizeWeightKg(size: String): Int {
        return when (size.lowercase()) {
            "small" -> 2
            "medium" -> 5
            "large" -> 15
            "extra_large" -> 40
            else -> 2
        }
    }

    private fun handleSearchTrips() {
        val state = _uiState.value
        if (!state.canSearch || state.startLocation == null || state.endLocation == null) {
            sendEffect(ParcelTripsEffect.ShowToast("Please fill all required fields"))
            return
        }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            try {
                _uiState.update { it.copy(isSearching = true, error = null, searchResults = emptyList(), nextCursor = null) }
                
                val result: SearchParcelTripsResult = if (state.transportType == "car") {
                    val req = SearchCarParcelTripDomainRequest(
                        startCoordinate = state.startLocation!!.coordinate,
                        endCoordinate = state.endLocation!!.coordinate,
                        startPointName = state.startLocation.name,
                        endPointName = state.endLocation.name,
                        tripDate = state.tripDate,
                        packageSize = state.packageSize,
                        page = 1, limit = DEFAULT_PAGE_LIMIT, cursor = state.nextCursor
                    )
                    repository.searchCarParcelTrips(req)
                } else {
                    val req = SearchPublicParcelTripDomainRequest(
                        startCoordinate = state.startLocation!!.coordinate,
                        endCoordinate = state.endLocation!!.coordinate,
                        startPointName = state.startLocation.name,
                        endPointName = state.endLocation.name,
                        tripDate = state.tripDate,
                        packageSize = state.packageSize,
                        transportType = state.transportType,
                        page = 1, limit = DEFAULT_PAGE_LIMIT, cursor = state.nextCursor
                    )
                    repository.searchPublicParcelTrips(req)
                }
                
                _uiState.update {
                    it.copy(
                        searchResults = result.trips, isSearching = false,
                        nextCursor = result.pagination?.nextCursor,
                        hasMoreResults = result.pagination?.nextCursor != null
                    )
                }
                sendEffect(ParcelTripsEffect.ScrollToTop)
                if (result.trips.isEmpty()) sendEffect(ParcelTripsEffect.ShowToast("No trips found for your search"))
            } catch (e: Exception) {
                _uiState.update { it.copy(isSearching = false, error = "Failed to search trips: ${e.message}") }
                sendEffect(ParcelTripsEffect.ShowToast("Failed to search trips"))
            }
        }
    }

    private fun handleLoadMoreTrips() {
        val state = _uiState.value
        if (!state.hasMoreResults || state.isLoadingMore || state.nextCursor == null) return

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoadingMore = true, error = null) }
                
                val result: SearchParcelTripsResult = if (state.transportType == "car") {
                    val req = SearchCarParcelTripDomainRequest(
                        startCoordinate = state.startLocation!!.coordinate,
                        endCoordinate = state.endLocation!!.coordinate,
                        startPointName = state.startLocation.name,
                        endPointName = state.endLocation.name,
                        tripDate = state.tripDate,
                        packageSize = state.packageSize,
                        page = 1, limit = DEFAULT_PAGE_LIMIT, cursor = state.nextCursor
                    )
                    repository.searchCarParcelTrips(req)
                } else {
                    val req = SearchPublicParcelTripDomainRequest(
                        startCoordinate = state.startLocation!!.coordinate,
                        endCoordinate = state.endLocation!!.coordinate,
                        startPointName = state.startLocation.name,
                        endPointName = state.endLocation.name,
                        tripDate = state.tripDate,
                        packageSize = state.packageSize,
                        transportType = state.transportType,
                        page = 1, limit = DEFAULT_PAGE_LIMIT, cursor = state.nextCursor
                    )
                    repository.searchPublicParcelTrips(req)
                }

                _uiState.update {
                    it.copy(
                        searchResults = it.searchResults + result.trips,
                        isLoadingMore = false,
                        nextCursor = result.pagination?.nextCursor,
                        hasMoreResults = result.pagination?.nextCursor != null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoadingMore = false, error = "Failed to load more trips") }
                sendEffect(ParcelTripsEffect.ShowToast("Failed to load more trips"))
            }
        }
    }

    private fun handleClearSearch() {
        searchJob?.cancel()
        val today = StdClock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
        _uiState.update { 
            val newState = ParcelTripsUiState(packageSize = "small", transportType = "bus", tripDate = today)
            newState.copy(validation = validateForm(newState.startLocation, newState.endLocation, newState.tripDate, newState.packageSize))
        }
    }

    private fun handleSelectTrip(tripId: String) {
        _uiState.update { it.copy(selectedTripId = tripId, bookingSuccess = false) }
        sendEffect(ParcelTripsEffect.NavigateToTripDetails(tripId))
    }

    private fun handleBookTrip() {
        val state = _uiState.value
        val trip = state.searchResults.find { it.id == state.selectedTripId } ?: return

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isBooking = true) }
                
                val request = ParcelBookingDomainRequest(
                    tripId = trip.id,
                    startPointName = trip.startPointName,
                    endPointName = trip.endPointName,
                    packageSize = state.packageSize,
                    offeredPrice = trip.costPerKg * getPackageSizeWeightKg(state.packageSize),
                    bookingNotes = ""
                )

                if (state.transportType == "car") {
                    repository.bookCarParcel(request)
                } else {
                    repository.bookPublicParcel(request)
                }

                _uiState.update { it.copy(isBooking = false, bookingSuccess = true) }
                sendEffect(ParcelTripsEffect.ShowToast("Booking request sent successfully!"))
            } catch (e: Exception) {
                _uiState.update { it.copy(isBooking = false) }
                sendEffect(ParcelTripsEffect.ShowToast("Failed to book: ${e.message}"))
            }
        }
    }

    private fun validateForm(
        startLoc: LocationSelection?, endLoc: LocationSelection?, date: String, pSize: String
    ): SearchParcelFormValidation {
        return SearchParcelFormValidation(
            startLocationValid = if (startLoc != null) ValidationState.Valid else ValidationState.Invalid("Start location required"),
            endLocationValid = if (endLoc != null) ValidationState.Valid else ValidationState.Invalid("End location required"),
            dateValid = if (date.isNotBlank()) ValidationState.Valid else ValidationState.Invalid("Trip date required"),
            packageSizeValid = if (pSize.isNotBlank()) ValidationState.Valid else ValidationState.Invalid("Size required")
        )
    }

    private fun sendEffect(effect: ParcelTripsEffect) {
        viewModelScope.launch { _effects.send(effect) }
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
        startPlaceSearchJob?.cancel()
        endPlaceSearchJob?.cancel()
    }
}
