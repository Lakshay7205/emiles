package com.state.greenmiles.com_state_greenmiles.presentation.screens.car_pool_1_publish_Ride

import com.state.greenmiles.com_state_greenmiles.domain.model.trip.Coordinate
import com.state.greenmiles.com_state_greenmiles.domain.model.trip.Route
import com.state.greenmiles.com_state_greenmiles.domain.model.trip.Trip
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.Create_trip_API.CarDetailsDto

data class CreateTripUiState(
    // Step 1: Location Selection
    val startPointName: String = "",
    val startCoordinate: Coordinate? = null,
    val endPointName: String = "",
    val endCoordinate: Coordinate? = null,

    // Step 2: Route Selection
    val routes: List<Route> = emptyList(),
    val selectedRoute: Route? = null,
    val isLoadingRoutes: Boolean = false,
    val routesError: String? = null,

    // Step 3: Trip Details
    val tripDate: String = "",
    val tripTime: String = "",
    val availableSeats: Int = 1,
    val costPerSeat: Double = 0.0,
    val womanAccompany: Boolean = false,
    val miscMessage: String = "",
    val recommendedCost: Double? = null,

    // Step 4: Car Details
    val carModel: String = "",
    val licensePlate: String = "",
    val carColor: String = "",
    val carYear: Int = 2026 ,
    val carType: String = "",

    // Saved Car Info
    val savedCars: List<CarDetailsDto> = emptyList(),

    // Overall State
    val currentStep: CreateTripStep = CreateTripStep.LOCATION_SELECTION,
    val isCreatingTrip: Boolean = false,
    val createTripError: String? = null,
    val tripCreated: Trip? = null
)

enum class CreateTripStep {
    LOCATION_SELECTION,
    ROUTE_SELECTION,
    TRIP_DETAILS,
    CAR_DETAILS,
    REVIEW
}

sealed class CreateTripEvent {
    data class StartLocationSelected(val name: String, val coordinate: Coordinate) : CreateTripEvent()
    data class EndLocationSelected(val name: String, val coordinate: Coordinate) : CreateTripEvent()
    object FetchRoutes : CreateTripEvent()
    data class RouteSelected(val route: Route) : CreateTripEvent()
    data class TripDateChanged(val date: String) : CreateTripEvent()
    data class TripTimeChanged(val time: String) : CreateTripEvent()
    data class AvailableSeatsChanged(val seats: Int) : CreateTripEvent()
    data class CostPerSeatChanged(val cost: Double) : CreateTripEvent()
    data class WomanAccompanyChanged(val accompany: Boolean) : CreateTripEvent()
    data class MiscMessageChanged(val message: String) : CreateTripEvent()
    data class CarModelChanged(val model: String) : CreateTripEvent()
    data class LicensePlateChanged(val plate: String) : CreateTripEvent()
    data class CarColorChanged(val color: String) : CreateTripEvent()
    data class CarYearChanged(val year: Int) : CreateTripEvent()
    data class CarTypeChanged(val type: String) : CreateTripEvent()
    data class LoadSavedCarDetails(val car: CarDetailsDto) : CreateTripEvent()
    object NextStep : CreateTripEvent()
    object PreviousStep : CreateTripEvent()
    object CreateTrip : CreateTripEvent()
    object ClearError : CreateTripEvent()
    object ResetForm : CreateTripEvent()
}