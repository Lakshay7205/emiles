package com.state.greenmiles.com_state_greenmiles.presentation.screens.car_pool_1_publish_Ride

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.Create_trip_API.CarDetailsDto
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.Create_trip_API.CreateTripRequest
import com.state.greenmiles.com_state_greenmiles.domain.repository.TripsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.state.greenmiles.com_state_greenmiles.utils.LocalStorage

class CreateTripViewModel(
    private val tripsRepository: TripsRepository,
    private val localStorage: LocalStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateTripUiState())
    val uiState: StateFlow<CreateTripUiState> = _uiState.asStateFlow()

    init {
        _uiState.update {
            it.copy(
                savedCars = localStorage.getSavedCars()
            )
        }
    }

    fun onEvent(event: CreateTripEvent) {
        when (event) {
            is CreateTripEvent.StartLocationSelected -> {
                _uiState.update {
                    it.copy(
                        startPointName = event.name,
                        startCoordinate = event.coordinate,
                        routesError = null
                    )
                }
            }

            is CreateTripEvent.EndLocationSelected -> {
                _uiState.update {
                    it.copy(
                        endPointName = event.name,
                        endCoordinate = event.coordinate,
                        routesError = null
                    )
                }
            }

            CreateTripEvent.FetchRoutes -> {
                fetchRoutes()
            }

            is CreateTripEvent.RouteSelected -> {
                _uiState.update {
                    it.copy(
                        selectedRoute = event.route,
                        recommendedCost = event.route.recommendedCost,
                        costPerSeat = if (event.route.recommendedCost != null && event.route.recommendedCost > 0) event.route.recommendedCost else it.costPerSeat
                    )
                }
            }

            is CreateTripEvent.TripDateChanged -> {
                _uiState.update { it.copy(tripDate = event.date) }
            }

            is CreateTripEvent.TripTimeChanged -> {
                _uiState.update { it.copy(tripTime = event.time) }
            }

            is CreateTripEvent.AvailableSeatsChanged -> {
                _uiState.update { it.copy(availableSeats = event.seats) }
            }

            is CreateTripEvent.CostPerSeatChanged -> {
                _uiState.update { it.copy(costPerSeat = event.cost) }
            }

            is CreateTripEvent.WomanAccompanyChanged -> {
                _uiState.update { it.copy(womanAccompany = event.accompany) }
            }

            is CreateTripEvent.MiscMessageChanged -> {
                _uiState.update { it.copy(miscMessage = event.message) }
            }

            is CreateTripEvent.CarModelChanged -> {
                _uiState.update { it.copy(carModel = event.model) }
            }

            is CreateTripEvent.LicensePlateChanged -> {
                _uiState.update { it.copy(licensePlate = event.plate) }
            }

            is CreateTripEvent.CarColorChanged -> {
                _uiState.update { it.copy(carColor = event.color) }
            }

            is CreateTripEvent.CarYearChanged -> {
                _uiState.update { it.copy(carYear = event.year) }
            }

            is CreateTripEvent.CarTypeChanged -> {
                _uiState.update { it.copy(carType = event.type) }
            }
            
            is CreateTripEvent.LoadSavedCarDetails -> {
                _uiState.update { 
                    it.copy(
                        carModel = event.car.model ?: "",
                        licensePlate = event.car.licensePlate ?: "",
                        carColor = event.car.color ?: "",
                        carYear = event.car.year ?: 2026,
                        carType = event.car.type ?: ""
                    )
                }
            }

            CreateTripEvent.NextStep -> {
                moveToNextStep()
            }

            CreateTripEvent.PreviousStep -> {
                moveToPreviousStep()
            }

            CreateTripEvent.CreateTrip -> {
                createTrip()
            }

            CreateTripEvent.ClearError -> {
                _uiState.update {
                    it.copy(
                        routesError = null,
                        createTripError = null
                    )
                }
            }

            CreateTripEvent.ResetForm -> {
                _uiState.value = CreateTripUiState(

                )
            }
        }
    }

    private fun fetchRoutes() {
        val state = _uiState.value

        if (state.startCoordinate == null || state.endCoordinate == null) {
            _uiState.update {
                it.copy(routesError = "Please select both start and end locations")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingRoutes = true, routesError = null) }

            try {
                val result = tripsRepository.getRoutes(
                    start = state.startCoordinate,
                    end = state.endCoordinate,
                    mode = "driving"
                )

                _uiState.update {
                    it.copy(
                        routes = result.routes,
                        isLoadingRoutes = false,
                        currentStep = CreateTripStep.ROUTE_SELECTION
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoadingRoutes = false,
                        routesError = e.message ?: "Failed to fetch routes"
                    )
                }
            }
        }
    }

    private fun moveToNextStep() {
        val state = _uiState.value
        val nextStep = when (state.currentStep) {
            CreateTripStep.LOCATION_SELECTION -> {
                if (state.startCoordinate == null || state.endCoordinate == null) {
                    _uiState.update { it.copy(routesError = "Please select both locations") }
                    return
                }
                fetchRoutes()
                return
            }
            CreateTripStep.ROUTE_SELECTION -> {
                if (state.selectedRoute == null) {
                    _uiState.update { it.copy(routesError = "Please select a route") }
                    return
                }
                CreateTripStep.TRIP_DETAILS
            }
            CreateTripStep.TRIP_DETAILS -> {
                if (!validateTripDetails()) return
                CreateTripStep.CAR_DETAILS
            }
            CreateTripStep.CAR_DETAILS -> {
                if (!validateCarDetails()) return
                CreateTripStep.REVIEW
            }
            CreateTripStep.REVIEW -> return
        }

        _uiState.update { it.copy(currentStep = nextStep, routesError = null) }
    }

    private fun moveToPreviousStep() {
        val state = _uiState.value
        val previousStep = when (state.currentStep) {
            CreateTripStep.LOCATION_SELECTION -> return
            CreateTripStep.ROUTE_SELECTION -> CreateTripStep.LOCATION_SELECTION
            CreateTripStep.TRIP_DETAILS -> CreateTripStep.ROUTE_SELECTION
            CreateTripStep.CAR_DETAILS -> CreateTripStep.TRIP_DETAILS
            CreateTripStep.REVIEW -> CreateTripStep.CAR_DETAILS
        }

        _uiState.update { it.copy(currentStep = previousStep, routesError = null) }
    }

    private fun validateTripDetails(): Boolean {
        val state = _uiState.value

        return when {
            state.tripDate.isEmpty() -> {
                _uiState.update { it.copy(createTripError = "Please select trip date") }
                false
            }
            state.tripTime.isEmpty() -> {
                _uiState.update { it.copy(createTripError = "Please select trip time") }
                false
            }
            state.availableSeats < 1 -> {
                _uiState.update { it.copy(createTripError = "Available seats must be at least 1") }
                false
            }
            state.costPerSeat <= 0 -> {
                _uiState.update { it.copy(createTripError = "Cost per seat must be greater than 0") }
                false
            }
            else -> true
        }
    }

    private fun validateCarDetails(): Boolean {
        val state = _uiState.value

        return when {
            state.carModel.isEmpty() -> {
                _uiState.update { it.copy(createTripError = "Please enter car model") }
                false
            }
            state.licensePlate.isEmpty() -> {
                _uiState.update { it.copy(createTripError = "Please enter license plate") }
                false
            }
            state.carColor.isEmpty() -> {
                _uiState.update { it.copy(createTripError = "Please enter car color") }
                false
            }
            state.carType.isEmpty() -> {
                _uiState.update { it.copy(createTripError = "Please select car type") }
                false
            }
            else -> true
        }
    }

    private fun createTrip() {
        val state = _uiState.value

        if (state.selectedRoute == null) {
            _uiState.update { it.copy(createTripError = "No route selected") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isCreatingTrip = true, createTripError = null) }

            try {
                val request = CreateTripRequest(
                    selectedPolyline = state.selectedRoute.polyline,
                    startPointName = state.startPointName,
                    endPointName = state.endPointName,
                    totalDistance = state.selectedRoute.distanceKm,
                    tripDate = state.tripDate,
                    tripTime = state.tripTime,
                    availableSeats = state.availableSeats,
                    costPerSeat = state.costPerSeat,
                    womanAccompany = state.womanAccompany,
                    miscMessage = state.miscMessage.ifBlank { null },
                    carDetails = CarDetailsDto(
                        model = state.carModel,
                        licensePlate = state.licensePlate,
                        color = state.carColor,
                        year = state.carYear,
                        type = state.carType.lowercase()
                    ),
                    estimatedDurationSeconds = state.selectedRoute.durationSeconds
                )

                val trip = tripsRepository.createTrip(request)

                localStorage.saveCarToList(
                    CarDetailsDto(
                        type = state.carType,
                        model = state.carModel,
                        color = state.carColor,
                        year = state.carYear,
                        licensePlate = state.licensePlate
                    )
                )

                _uiState.update {
                    it.copy(
                        isCreatingTrip = false,
                        tripCreated = trip
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isCreatingTrip = false,
                        createTripError = e.message ?: "Failed to create trip"
                    )
                }
            }
        }
    }

    fun canProceedToNextStep(): Boolean {
        val state = _uiState.value
        return when (state.currentStep) {
            CreateTripStep.LOCATION_SELECTION ->
                state.startCoordinate != null && state.endCoordinate != null
            CreateTripStep.ROUTE_SELECTION ->
                state.selectedRoute != null
            CreateTripStep.TRIP_DETAILS ->
                state.tripDate.isNotEmpty() && state.tripTime.isNotEmpty() &&
                        state.availableSeats >= 1 && state.costPerSeat > 0
            CreateTripStep.CAR_DETAILS ->
                state.carModel.isNotEmpty() && state.licensePlate.isNotEmpty() &&
                        state.carColor.isNotEmpty() && state.carType.isNotEmpty()
            CreateTripStep.REVIEW -> true
        }
    }
}