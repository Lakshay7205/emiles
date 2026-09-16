package com.state.greenmiles.com_state_greenmiles.presentation.screens.parcel

import com.state.greenmiles.com_state_greenmiles.domain.model.parcel.CarDetails
import com.state.greenmiles.com_state_greenmiles.domain.model.parcel.ParcelTrip
import com.state.greenmiles.com_state_greenmiles.domain.model.trip.Coordinate
import com.state.greenmiles.com_state_greenmiles.domain.model.trip.Route

enum class CreateParcelStep {
    PARCEL_TYPE,        // NEW: Select if it's a Personal Car Trip or a Public Prep (Bus/Train/Flight)
    LOCATION_SELECTION,
    ROUTE_SELECTION,
    TRIP_DETAILS,
    CAR_DETAILS,        // Only for Personal Car Trip
    REVIEW
}

data class CreateParcelTripUiState(
    val currentStep: CreateParcelStep = CreateParcelStep.LOCATION_SELECTION,
    
    // Type selection
    val isPersonalCar: Boolean = false,
    val transportType: String = "bus", // bus, train, flight
    
    // Locations
    val startPointName: String = "",
    val endPointName: String = "",
    val startCoordinate: Coordinate? = null,
    val endCoordinate: Coordinate? = null,
    
    // Route
    val routes: List<Route> = emptyList(),
    val selectedRoute: Route? = null,
    val isLoadingRoutes: Boolean = false,
    val routesError: String? = null,
    
    // Trip Details
    val tripDate: String = "",
    val tripTime: String = "",
    val availableSpace: String = "small", // small, medium, large, extra_large
    val costPerKg: Double = 0.0,
    val message: String = "",
    
    // Public Transport specifics
    val operatorName: String = "",
    val vehicleNumber: String = "",
    
    // Car Details
    val carModel: String = "",
    val licensePlate: String = "",
    val carColor: String = "",
    val carYear: Int = 2026,
    val carType: String = "",
    val savedCars: List<CarDetails> = emptyList(),
    
    // Loading/Error
    val isCreatingTrip: Boolean = false,
    val createTripError: String? = null,
    val tripCreated: ParcelTrip? = null
)
