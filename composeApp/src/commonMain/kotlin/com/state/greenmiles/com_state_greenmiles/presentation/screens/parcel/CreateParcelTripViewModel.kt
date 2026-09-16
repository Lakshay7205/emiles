package com.state.greenmiles.com_state_greenmiles.presentation.screens.parcel

// import androidx.lifecycle.ViewModel moved to line 3
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.state.greenmiles.com_state_greenmiles.domain.model.parcel.*
import com.state.greenmiles.com_state_greenmiles.domain.model.trip.Coordinate
import com.state.greenmiles.com_state_greenmiles.domain.model.trip.Route
import com.state.greenmiles.com_state_greenmiles.domain.repository.ParcelRepository
import com.state.greenmiles.com_state_greenmiles.domain.repository.TripsRepository
import com.state.greenmiles.com_state_greenmiles.utils.LocalStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class CreateParcelTripEvent {
    data class SelectParcelType(val isPersonalCar: Boolean) : CreateParcelTripEvent()
    data class UpdateTransportType(val type: String) : CreateParcelTripEvent()
    
    data class StartLocationSelected(val name: String, val coordinate: com.state.greenmiles.com_state_greenmiles.domain.model.trip.Coordinate) : CreateParcelTripEvent()
    data class EndLocationSelected(val name: String, val coordinate: com.state.greenmiles.com_state_greenmiles.domain.model.trip.Coordinate) : CreateParcelTripEvent()
    
    object FetchRoutes : CreateParcelTripEvent()
    data class RouteSelected(val route: com.state.greenmiles.com_state_greenmiles.domain.model.trip.Route) : CreateParcelTripEvent()
    
    data class TripDateChanged(val date: String) : CreateParcelTripEvent()
    data class TripTimeChanged(val time: String) : CreateParcelTripEvent()
    data class AvailableSpaceChanged(val space: String) : CreateParcelTripEvent()
    data class CostPerKgChanged(val cost: Double) : CreateParcelTripEvent()
    data class MessageChanged(val message: String) : CreateParcelTripEvent()
    
    // Public transport details
    data class OperatorNameChanged(val name: String) : CreateParcelTripEvent()
    data class VehicleNumberChanged(val number: String) : CreateParcelTripEvent()
    
    // Car details
    data class CarModelChanged(val model: String) : CreateParcelTripEvent()
    data class LicensePlateChanged(val plate: String) : CreateParcelTripEvent()
    data class CarColorChanged(val color: String) : CreateParcelTripEvent()
    data class CarYearChanged(val year: Int) : CreateParcelTripEvent()
    data class CarTypeChanged(val type: String) : CreateParcelTripEvent()
    data class LoadSavedCarDetails(val car: CarDetails) : CreateParcelTripEvent()
    
    object NextStep : CreateParcelTripEvent()
    object PreviousStep : CreateParcelTripEvent()
    object CreateTrip : CreateParcelTripEvent()
    object ClearError : CreateParcelTripEvent()
    object ResetForm : CreateParcelTripEvent()
}

class CreateParcelTripViewModel(
    private val parcelRepository: ParcelRepository,
    private val tripsRepository: TripsRepository,
    private val localStorage: LocalStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateParcelTripUiState())
    val uiState: StateFlow<CreateParcelTripUiState> = _uiState.asStateFlow()

    private fun List<com.state.greenmiles.com_state_greenmiles.data.remote.dto.Create_trip_API.CarDetailsDto>.mapToDomain(): List<CarDetails> {
        return this.map { 
            CarDetails(
                model = it.model ?: "",
                licensePlate = it.licensePlate ?: "",
                color = it.color ?: "",
                year = it.year ?: 2024,
                type = it.type ?: "sedan"
            )
        }
    }

    init {
        _uiState.update {
            it.copy(
                savedCars = localStorage.getSavedCars().mapToDomain()
            )
        }
    }

    fun onEvent(event: CreateParcelTripEvent) {
        when (event) {
            is CreateParcelTripEvent.SelectParcelType -> {
                _uiState.update { it.copy(isPersonalCar = event.isPersonalCar) }
            }
            is CreateParcelTripEvent.UpdateTransportType -> {
                _uiState.update { it.copy(transportType = event.type) }
            }
            is CreateParcelTripEvent.StartLocationSelected -> {
                _uiState.update {
                    it.copy(
                        startPointName = event.name,
                        startCoordinate = event.coordinate,
                        routesError = null
                    )
                }
            }
            is CreateParcelTripEvent.EndLocationSelected -> {
                _uiState.update {
                    it.copy(
                        endPointName = event.name,
                        endCoordinate = event.coordinate,
                        routesError = null
                    )
                }
            }
            CreateParcelTripEvent.FetchRoutes -> fetchRoutes()
            is CreateParcelTripEvent.RouteSelected -> {
                _uiState.update { it.copy(selectedRoute = event.route) }
            }
            is CreateParcelTripEvent.TripDateChanged -> _uiState.update { it.copy(tripDate = event.date) }
            is CreateParcelTripEvent.TripTimeChanged -> _uiState.update { it.copy(tripTime = event.time) }
            is CreateParcelTripEvent.AvailableSpaceChanged -> _uiState.update { it.copy(availableSpace = event.space) }
            is CreateParcelTripEvent.CostPerKgChanged -> _uiState.update { it.copy(costPerKg = event.cost) }
            is CreateParcelTripEvent.MessageChanged -> _uiState.update { it.copy(message = event.message) }
            
            is CreateParcelTripEvent.OperatorNameChanged -> _uiState.update { it.copy(operatorName = event.name) }
            is CreateParcelTripEvent.VehicleNumberChanged -> _uiState.update { it.copy(vehicleNumber = event.number) }
            
            is CreateParcelTripEvent.CarModelChanged -> _uiState.update { it.copy(carModel = event.model) }
            is CreateParcelTripEvent.LicensePlateChanged -> _uiState.update { it.copy(licensePlate = event.plate) }
            is CreateParcelTripEvent.CarColorChanged -> _uiState.update { it.copy(carColor = event.color) }
            is CreateParcelTripEvent.CarYearChanged -> _uiState.update { it.copy(carYear = event.year) }
            is CreateParcelTripEvent.CarTypeChanged -> _uiState.update { it.copy(carType = event.type) }
            is CreateParcelTripEvent.LoadSavedCarDetails -> {
                _uiState.update { 
                    it.copy(
                        carModel = event.car.model,
                        licensePlate = event.car.licensePlate,
                        carColor = event.car.color,
                        carYear = event.car.year,
                        carType = event.car.type
                    )
                }
            }
            CreateParcelTripEvent.NextStep -> moveToNextStep()
            CreateParcelTripEvent.PreviousStep -> moveToPreviousStep()
            CreateParcelTripEvent.CreateTrip -> createTrip()
            CreateParcelTripEvent.ClearError -> _uiState.update { it.copy(routesError = null, createTripError = null) }
            CreateParcelTripEvent.ResetForm -> _uiState.value = CreateParcelTripUiState(savedCars = localStorage.getSavedCars().mapToDomain())
        }
    }

    private fun fetchRoutes() {
        val state = _uiState.value
        if (state.startCoordinate == null || state.endCoordinate == null) {
            _uiState.update { it.copy(routesError = "Please select both start and end locations") }
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
                        currentStep = CreateParcelStep.ROUTE_SELECTION
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoadingRoutes = false, routesError = e.message ?: "Failed to fetch routes")
                }
            }
        }
    }

    private fun fetchDefaultRouteForPublicTransport() {
        val state = _uiState.value
        if (state.startCoordinate == null || state.endCoordinate == null) return

        viewModelScope.launch {
            try {
                val result = tripsRepository.getRoutes(
                    start = state.startCoordinate,
                    end = state.endCoordinate,
                    mode = "driving"
                )
                if (result.routes.isNotEmpty()) {
                    _uiState.update { it.copy(selectedRoute = result.routes.first()) }
                }
            } catch (e: Exception) {
                // Ignore errors for background fetch, will default to 0.0
            }
        }
    }

    private fun moveToNextStep() {
        val state = _uiState.value
        val nextStep = when (state.currentStep) {
            CreateParcelStep.PARCEL_TYPE -> CreateParcelStep.LOCATION_SELECTION
            CreateParcelStep.LOCATION_SELECTION -> {
                if (state.startCoordinate == null || state.endCoordinate == null) {
                    _uiState.update { it.copy(routesError = "Please select both locations") }
                    return
                }
                if (state.isPersonalCar) {
                    fetchRoutes()
                    return
                } else {
                    fetchDefaultRouteForPublicTransport()
                    CreateParcelStep.TRIP_DETAILS
                }
            }
            CreateParcelStep.ROUTE_SELECTION -> {
                if (state.selectedRoute == null) {
                    _uiState.update { it.copy(routesError = "Please select a route") }
                    return
                }
                CreateParcelStep.TRIP_DETAILS
            }
            CreateParcelStep.TRIP_DETAILS -> {
                if (!validateTripDetails()) return
                if (state.isPersonalCar) CreateParcelStep.CAR_DETAILS else CreateParcelStep.REVIEW
            }
            CreateParcelStep.CAR_DETAILS -> {
                if (!validateCarDetails()) return
                CreateParcelStep.REVIEW
            }
            CreateParcelStep.REVIEW -> return
        }
        _uiState.update { it.copy(currentStep = nextStep, routesError = null) }
    }

    private fun moveToPreviousStep() {
        val state = _uiState.value
        val previousStep = when (state.currentStep) {
            CreateParcelStep.PARCEL_TYPE -> return
            CreateParcelStep.LOCATION_SELECTION -> return // Screen handles onNavigateBack
            CreateParcelStep.ROUTE_SELECTION -> CreateParcelStep.LOCATION_SELECTION
            CreateParcelStep.TRIP_DETAILS -> {
                if (state.isPersonalCar) CreateParcelStep.ROUTE_SELECTION
                else CreateParcelStep.LOCATION_SELECTION
            }
            CreateParcelStep.CAR_DETAILS -> CreateParcelStep.TRIP_DETAILS
            CreateParcelStep.REVIEW -> {
                if (state.isPersonalCar) CreateParcelStep.CAR_DETAILS
                else CreateParcelStep.TRIP_DETAILS
            }
        }
        _uiState.update { it.copy(currentStep = previousStep, routesError = null) }
    }

    private fun validateTripDetails(): Boolean {
        val state = _uiState.value
        return when {
            state.tripDate.isEmpty() -> { _uiState.update { it.copy(createTripError = "Please select trip date") }; false }
            state.tripTime.isEmpty() -> { _uiState.update { it.copy(createTripError = "Please select trip time") }; false }
            state.availableSpace.isEmpty() -> { _uiState.update { it.copy(createTripError = "Please select package size") }; false }
            state.costPerKg <= 0 -> { _uiState.update { it.copy(createTripError = "Cost per kg must be greater than 0") }; false }
            !state.isPersonalCar && state.operatorName.isEmpty() -> { _uiState.update { it.copy(createTripError = "Please enter operator name") }; false }
            else -> true
        }
    }

    private fun validateCarDetails(): Boolean {
        val state = _uiState.value
        return when {
            state.carModel.isEmpty() -> { _uiState.update { it.copy(createTripError = "Please enter car model") }; false }
            state.licensePlate.isEmpty() -> { _uiState.update { it.copy(createTripError = "Please enter license plate") }; false }
            state.carColor.isEmpty() -> { _uiState.update { it.copy(createTripError = "Please enter car color") }; false }
            state.carType.isEmpty() -> { _uiState.update { it.copy(createTripError = "Please select car type") }; false }
            else -> true
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

    private fun createTrip() {
        val state = _uiState.value
        
        if (state.isPersonalCar && state.selectedRoute == null) {
            _uiState.update { it.copy(createTripError = "No route selected") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isCreatingTrip = true, createTripError = null) }
            try {
                val trip = if (state.isPersonalCar) {
                    val request = CreateCarParcelTripRequest(
                        selectedPolyline = state.selectedRoute?.polyline ?: "",
                        startPointName = state.startPointName,
                        endPointName = state.endPointName,
                        startCoordinate = state.startCoordinate ?: Coordinate(15.3647, 75.1240),
                        endCoordinate = state.endCoordinate ?: Coordinate(15.3647, 75.1240),
                        totalDistanceKm = state.selectedRoute?.distanceKm ?: 0.0,
                        tripDate = state.tripDate,
                        tripTime = state.tripTime,
                        availableSpace = state.availableSpace,
                        costPerKg = state.costPerKg,
                        message = state.message,
                        carDetails = CarDetails(
                            model = state.carModel,
                            licensePlate = state.licensePlate,
                            color = state.carColor,
                            year = state.carYear,
                            type = state.carType.lowercase()
                        ),
                        estimatedDurationSeconds = state.selectedRoute?.durationSeconds ?: 0
                    )
                    parcelRepository.createCarParcelTrip(request)
                } else {
                    val request = CreatePublicParcelTripRequest(
                        selectedPolyline = state.selectedRoute?.polyline ?: "",
                        startPointName = state.startPointName,
                        endPointName = state.endPointName,
                        startCoordinate = state.startCoordinate ?: Coordinate(15.3647, 75.1240),
                        endCoordinate = state.endCoordinate ?: Coordinate(15.3647, 75.1240),
                        totalDistanceKm = state.selectedRoute?.distanceKm ?: 0.0,
                        tripDate = state.tripDate,
                        tripTime = state.tripTime,
                        availableSpace = state.availableSpace,
                        costPerKg = state.costPerKg,
                        transportDetails = TransportDetails(
                            name = state.operatorName,
                            type = state.transportType,
                            vehicleNumber = state.vehicleNumber,
                            operator = state.operatorName,
                            route = ""
                        ),
                        message = state.message,
                        estimatedDurationSeconds = state.selectedRoute?.durationSeconds ?: 0
                    )
                    parcelRepository.createPublicParcelTrip(request)
                }

                if (state.isPersonalCar) {
                    localStorage.saveCarToList(
                        com.state.greenmiles.com_state_greenmiles.data.remote.dto.Create_trip_API.CarDetailsDto(
                            type = state.carType,
                            model = state.carModel,
                            color = state.carColor,
                            year = state.carYear,
                            licensePlate = state.licensePlate
                        )
                    )
                }

                _uiState.update { it.copy(isCreatingTrip = false, tripCreated = trip) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isCreatingTrip = false, createTripError = e.message ?: "Failed to create parcel trip") }
            }
        }
    }
}
