package com.state.greenmiles.com_state_greenmiles.presentation.screens.car_pool__search_ride.RideDetailsScreen

sealed interface BookingUiState {
    data object Idle : BookingUiState
    data object Loading : BookingUiState
    data object Success : BookingUiState
    data class Error(val message: String) : BookingUiState
}