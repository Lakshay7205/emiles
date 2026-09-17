package com.state.greenmiles.com_state_greenmiles.data.repository
import co.touchlab.kermit.Logger
import com.state.greenmiles.com_state_greenmiles.data.mappers.toDomain
import com.state.greenmiles.com_state_greenmiles.data.mappers.toDto
import com.state.greenmiles.com_state_greenmiles.data.remote.api.TripsApi
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.ApiResponse
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.Create_trip_API.CreateTripRequest
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.route_api.requests.GetRoutesRequest
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.search_trip_apis.SearchTripsRequest
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.trip_details.TripDetailsData
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.trip_details.TripDetailsResponse
import com.state.greenmiles.com_state_greenmiles.data.remote.model.BookingRequest
import com.state.greenmiles.com_state_greenmiles.domain.model.UserProfile
import com.state.greenmiles.com_state_greenmiles.domain.model.booking.Booking
import com.state.greenmiles.com_state_greenmiles.domain.model.trip.Coordinate
import com.state.greenmiles.com_state_greenmiles.domain.model.trip.RoutesResult
import com.state.greenmiles.com_state_greenmiles.domain.model.trip.SearchTripsResultPage
import com.state.greenmiles.com_state_greenmiles.domain.model.trip.Trip
import com.state.greenmiles.com_state_greenmiles.domain.model.trip.Trips
import com.state.greenmiles.com_state_greenmiles.domain.model.trip_details.TripDetails
import com.state.greenmiles.com_state_greenmiles.domain.repository.TripsRepository
import com.state.greenmiles.com_state_greenmiles.utils.LocalStorage
import kotlinx.serialization.json.Json

class TripsRepositoryImpl(
    private val api: TripsApi,
    localStorage: LocalStorage
) : TripsRepository {
    val logger = Logger.withTag("acceptstatus")


    override suspend fun getRoutes(
        start: Coordinate,
        end: Coordinate,
        mode: String
    ): RoutesResult {

        println("TripsRepo | getRoutes called -> start=$start end=$end mode=$mode")

        val request = GetRoutesRequest(
            startCoordinate = start.toDto(),
            endCoordinate = end.toDto(),
            waypoints = emptyList(),
            mode = mode
        )

        println("TripsRepo | getRoutes request -> $request")

        val response = api.getRoutes(request)

        println("TripsRepo | getRoutes response -> success=${response.success}, message=${response.message}")

        if (!response.success || response.data == null) {
            println("TripsRepo | getRoutes FAILED -> ${response.message}")
            throw IllegalStateException(response.message ?: "Failed to get routes")
        }

        return response.data.toDomain()
    }

    override suspend fun createTrip(request: CreateTripRequest): Trip {
        println(
            "TripsRepo | createTrip request JSON = ${
                Json { prettyPrint = true }.encodeToString(request)
            }"
        )


        val response = api.createTrip(request)

        println("TripsRepo | createTrip response -> success=${response.success}, message=${response.message}")

        if (!response.success || response.data == null) {
            println("TripsRepo | createTrip FAILED -> ${response.message}")
            throw IllegalStateException(response.message ?: "Trip creation failed")
        }

        return response.data.toDomain()
    }

    override suspend fun searchTrips(request: SearchTripsRequest): SearchTripsResultPage {
        println("TripsRepo | searchTrips request -> $request")

        val response = api.searchTrips(request)

        println("TripsRepo | searchTrips response -> success=${response.success}, message=${response.data}")

        if (!response.success || response.data == null) {
            println("TripsRepo | searchTrips FAILED -> ${response.message}")
            throw IllegalStateException(response.message ?: "Trip search failed")
        }

        return response.data.toDomain()
    }

    override suspend fun getMyTrips(status: String, page: Int, limit: Int): List<Trip> {
        println("TripsRepo | getMyTrips -> status=$status page=$page limit=$limit")

        val response = api.getMyTrips(status, page, limit)

        println("TripsRepo | getMyTrips response -> success=${response.success}, message=${response.message}")

        if (!response.success || response.data == null) {
            println("TripsRepo | getMyTrips FAILED -> ${response.message}")
            throw IllegalStateException(response.message ?: "Fetching trips failed")
        }

        return response.data.trips.map { it.toDomain() }
    }


    override suspend fun getTripDetails(tripId: String): ApiResponse<TripDetails> {
        println("🔍 getTripDetails() - Starting for tripId: $tripId")

        val response: ApiResponse<TripDetailsData> = api.getTripDetails(tripId)  // Changed type

        println("📦 API Response: success=${response.success}, data=${response.data}")

        return if (response.success && response.data != null) {
            println("✅ Mapping to domain model")
            val domainModel = response.data.toDomain()  // Direct mapping, no .data.data
            println("🎯 Domain model: $domainModel")

            ApiResponse(
                success = true,
                data = domainModel,
                message = response.message
            )
        } else {
            println("❌ API call failed: ${response.message}")
            ApiResponse(
                success = false,
                data = null,
                message = response.message ?: "Something went wrong"
            )
        }
    }
    override suspend fun getUserProfile(userId: String): UserProfile {


        val response = api.getUserProfile(userId)

        if (!response.success || response.data == null) {
            println("ProfileRepo FAILED -> ${response.message}")
            throw IllegalStateException(response.message ?: "Failed to fetch profile")
        }

        println("ProfileRepo SUCCESS")

        return response.data.toDomain()
    }

    override suspend fun sendMobileOtp(newMobile: String) : String {

        val response = api.sendMobileOtp(newMobile)

        if (!response.success) {
            throw IllegalStateException(
                response.message ?: "Failed to send OTP"
            )
        }
        return response.data?.otpToken ?: throw IllegalStateException("OTP token missing")

    }

    override suspend fun verifyAndUpdateMobile(
        newMobile: String,
        otp: String,
        otpToken: String
    ) {

        val response = api.verifyAndUpdateMobile(
            newMobile,
            otp,
            otpToken
        )

        if (!response.success) {
            throw IllegalStateException(
                response.message ?: "Mobile update failed"
            )
        }
    }

    override suspend fun updateEmail(email: String) {

        val response = api.updateEmail(email)

        if (!response.success) {
            throw IllegalStateException(
                response.message ?: "Email update failed"
            )
        }
    }

    override suspend fun getMyBookings(
        page: Int,
        limit: Int,
        status: String?
    ): List<Booking> {

        val response = api.getMyBookings(page, limit, status)
logger.d { "Response = $response" }
        if (!response.success || response.data == null) {
            throw IllegalStateException(response.message ?: "Failed to fetch bookings")
        }

        return response.data.bookings.map { it.toDomain() }
    }

    override suspend fun getBookingDetails(tripId: String): Booking {

        val response = api.getBookingDetails(tripId)

        if (!response.success || response.data == null) {
            throw IllegalStateException(response.message ?: "Failed to fetch booking")
        }

        return response.data.toDomain()
    }

    override suspend fun cancelBooking(bookingId: String) {

        val response = api.cancelBooking(bookingId)

        if (!response.success) {
            throw IllegalStateException(response.message ?: "Cancel failed")
        }
    }

    override suspend fun bookTrip(request: BookingRequest): Boolean {
        println("TripsRepo | bookTrip request -> $request")

        return try {
            val success = api.bookTrip(request)

            println("TripsRepo | bookTrip result -> success=$success")

            if (!success) {
                println("TripsRepo | bookTrip FAILED")
                throw IllegalStateException("Trip booking failed")
            }

            true
        } catch (e: Exception) {
            println("TripsRepo | bookTrip EXCEPTION -> ${e.message}")
            throw e
        }
    }
    override suspend fun getMyCreatedTrips(
        page: Int,
        limit: Int
    ): List<Trips> {

        val response = api.getMyCreatedTrips(page, limit)

        if (!response.success || response.data == null) {
            throw IllegalStateException(
                response.message ?: "Failed to fetch trips"
            )
        }

        return response.data.map { it.toDomain() }
    }

    override suspend fun acceptBooking(bookingId: String) {

        val response = api.acceptBooking(bookingId)
        logger.d { "succes-$response" }

        if (!response.success) {
            throw IllegalStateException(
                response.message ?: "Failed to accept booking"
            )
        }
    }

    override suspend fun rejectBooking(bookingId: String) {

        val response = api.rejectBooking(bookingId)

        if (!response.success) {
            throw IllegalStateException(
                response.message ?: "Failed to reject booking"
            )
        }
    }

    override suspend fun getBookingsForTrip(
        tripId: String
    ): List<Booking> {

        println("TripsRepo | getBookingsForTrip -> tripId=$tripId")

        val response = api.getBookingsForTrip(tripId)

        if (!response.success || response.data == null) {
            println("TripsRepo FAILED -> ${response.message}")
            throw IllegalStateException(
                response.message ?: "Failed to fetch trip bookings"
            )
        }

        val bookings = response.data.bookings

        return bookings.map { it.toDomain() }
    }



}
