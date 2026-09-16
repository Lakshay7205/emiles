package com.state.greenmiles.com_state_greenmiles.presentation.screens.tripScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.state.greenmiles.com_state_greenmiles.domain.repository.TripsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
class MyTripsViewModel(
    private val repository: TripsRepository
) : ViewModel() {

    var uiState by mutableStateOf(MyTripsUiState())
        private set

    // ---------------- PASSENGER BOOKINGS ----------------
    fun loadBookings(status: String? = null) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)

            try {
                val bookings = repository.getMyBookings(
                    page = 1,
                    limit = 20,
                    status = status
                )

                uiState = uiState.copy(
                    isLoading = false,
                    bookings = bookings
                )

            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun cancelBooking(id: String) {
        viewModelScope.launch {
            try {
                repository.cancelBooking(id)

                uiState = uiState.copy(
                    message = "Booking cancelled successfully"
                )

                loadBookings()

            } catch (e: Exception) {
                uiState = uiState.copy(error = e.message)
            }
        }
    }

    // ---------------- DRIVER TRIPS ----------------
    fun loadMyTrips() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)

            try {
                val trips = repository.getMyCreatedTrips(
                    page = 1,
                    limit = 20
                )

                uiState = uiState.copy(
                    isLoading = false,
                    trips = trips
                )

            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun acceptBooking(id: String) {
        viewModelScope.launch {
            try {
                repository.acceptBooking(id)

                uiState = uiState.copy(
                    message = "Booking approved successfully"
                )

                loadBookings()

            } catch (e: Exception) {
                uiState = uiState.copy(error = e.message)
            }
        }
    }

    fun rejectBooking(id: String) {
        viewModelScope.launch {
            try {
                repository.rejectBooking(id)

                uiState = uiState.copy(
                    message = "Booking rejected"
                )

                loadBookings()

            } catch (e: Exception) {
                uiState = uiState.copy(error = e.message)
            }
        }
    }


    fun loadTripBookings(tripId: String) {
        viewModelScope.launch {

            uiState = uiState.copy(isLoading = true)

            try {
                val bookings = repository.getBookingsForTrip(tripId)

                uiState = uiState.copy(
                    isLoading = false,
                    bookings = bookings
                )

            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
}