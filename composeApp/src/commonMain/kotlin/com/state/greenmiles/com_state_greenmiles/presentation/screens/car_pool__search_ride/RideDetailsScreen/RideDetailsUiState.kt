package com.state.greenmiles.com_state_greenmiles.presentation.screens.car_pool__search_ride.RideDetailsScreen

import com.state.greenmiles.com_state_greenmiles.domain.model.trip_details.TripDetails

sealed interface TripDetailsUiState {
    data object Initial : TripDetailsUiState
    data object Loading : TripDetailsUiState
    data class Success(val tripDetails: TripDetails) : TripDetailsUiState
    data class Error(val message: String) : TripDetailsUiState
}

