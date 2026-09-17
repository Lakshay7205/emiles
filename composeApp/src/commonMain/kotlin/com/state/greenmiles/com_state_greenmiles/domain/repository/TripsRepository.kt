package com.state.greenmiles.com_state_greenmiles.domain.repository

import com.state.greenmiles.com_state_greenmiles.data.remote.dto.ApiResponse
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.Create_trip_API.CreateTripRequest
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.search_trip_apis.SearchTripsRequest
import com.state.greenmiles.com_state_greenmiles.data.remote.model.BookingRequest
import com.state.greenmiles.com_state_greenmiles.domain.model.UserProfile
import com.state.greenmiles.com_state_greenmiles.domain.model.booking.Booking
import com.state.greenmiles.com_state_greenmiles.domain.model.trip.Coordinate
import com.state.greenmiles.com_state_greenmiles.domain.model.trip.RoutesResult
import com.state.greenmiles.com_state_greenmiles.domain.model.trip.SearchTripsResultPage
import com.state.greenmiles.com_state_greenmiles.domain.model.trip.Trip
import com.state.greenmiles.com_state_greenmiles.domain.model.trip.Trips
import com.state.greenmiles.com_state_greenmiles.domain.model.trip_details.TripDetails

interface TripsRepository {

    suspend fun getRoutes(
        start: Coordinate,
        end: Coordinate,
        mode: String
    ): RoutesResult
    suspend fun createTrip(request: CreateTripRequest): Trip

    suspend fun searchTrips(request: SearchTripsRequest): SearchTripsResultPage

    suspend fun getMyTrips(
        status: String,
        page: Int,
        limit: Int
    ): List<Trip>
    suspend fun getUserProfile(userId: String): UserProfile

    suspend fun sendMobileOtp(
        newMobile: String
    ): String
    suspend fun verifyAndUpdateMobile(
        newMobile: String,
        otp: String,
        otpToken: String
    )
    suspend fun updateEmail(
        email: String
    )

    suspend fun getMyBookings(
        page: Int,
        limit: Int,
        status: String? = null
    ): List<Booking>

    suspend fun getBookingDetails(tripId: String): Booking

    suspend fun cancelBooking(bookingId: String)
    suspend fun getMyCreatedTrips(
        page: Int,
        limit: Int
    ): List<Trips>
    /**
     * Get trip details by trip ID
     * @param tripId The unique identifier of the trip
     * @return ApiResponse containing TripDetails domain model
     */
    suspend fun acceptBooking(bookingId: String)

    suspend fun rejectBooking(bookingId: String)

    suspend fun getBookingsForTrip(tripId: String): List<Booking>

    suspend fun getTripDetails(tripId: String): ApiResponse<TripDetails>

    suspend fun bookTrip(request: BookingRequest): Boolean


}
