package com.state.greenmiles.com_state_greenmiles.data.repository

import com.state.greenmiles.com_state_greenmiles.data.remote.api.ParcelApi
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.car_parcel_api.SearchCarParcelTripRequest
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.parcel_booking.ParcelBookingRequest
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.public_parcel_api.SearchPublicParcelTripRequest
import com.state.greenmiles.com_state_greenmiles.domain.model.parcel.*
import com.state.greenmiles.com_state_greenmiles.domain.repository.ParcelRepository
import com.state.greenmiles.com_state_greenmiles.data.mapper.*

class ParcelRepositoryImpl(
    private val api: ParcelApi
) : ParcelRepository {

    override suspend fun createPublicParcelTrip(request: CreatePublicParcelTripRequest): ParcelTrip {
        println("ParcelRepo | createPublicParcelTrip request -> $request")

        val response = api.createPublicParcelTrip(request.toDto())

        println("ParcelRepo | createPublicParcelTrip response -> success=${response.success}, message=${response.message}")

        if (!response.success || response.data == null || response.data.trip == null) {
            println("ParcelRepo | createPublicParcelTrip FAILED -> ${response.message}")
            throw IllegalStateException(response.message ?: "Parcel trip creation failed")
        }

        return response.data.trip.toDomain()
    }

    override suspend fun searchPublicParcelTrips(request: SearchPublicParcelTripDomainRequest): SearchParcelTripsResult {
        println("ParcelRepo | searchPublicParcelTrips request -> $request")

        val response = api.searchPublicParcelTrips(request.toDto())

        println("ParcelRepo | searchPublicParcelTrips response -> success=${response.success}, message=${response.message}")

        if (!response.success || response.data == null) {
            println("ParcelRepo | searchPublicParcelTrips FAILED -> ${response.message}")
            throw IllegalStateException(response.message ?: "Parcel trips search failed")
        }

        val trips = response.data.trips.map { it.toDomain() }
        val pagination = response.data.pagination?.toDomain()

        return SearchParcelTripsResult(
            trips = trips,
            pagination = pagination
        )
    }

    override suspend fun createCarParcelTrip(request: CreateCarParcelTripRequest): ParcelTrip {
        println("ParcelRepo | createCarParcelTrip request -> $request")

        val response = api.createCarParcelTrip(request.toDto())
        
        if (!response.success || response.data == null || response.data.trip == null) {
            throw IllegalStateException(response.message ?: "Car parcel trip creation failed")
        }

        return response.data.trip.toDomain()
    }

    override suspend fun searchCarParcelTrips(request: SearchCarParcelTripDomainRequest): SearchParcelTripsResult {
        println("ParcelRepo | searchCarParcelTrips request -> $request")
        val response = api.searchCarParcelTrips(request.toDto())

        if (!response.success || response.data == null) {
            throw IllegalStateException(response.message ?: "Car parcel trips search failed")
        }

        val trips = response.data.trips.map { it.toDomain() }
        val pagination = response.data.pagination?.toDomain()

        return SearchParcelTripsResult(
            trips = trips,
            pagination = pagination
        )
    }

    override suspend fun bookCarParcel(request: ParcelBookingDomainRequest): ParcelBooking {
        println("ParcelRepo | bookCarParcel request -> $request")
        val response = api.bookCarParcel(request.toDto())

        if (!response.success || response.data == null) {
            throw IllegalStateException(response.message ?: "Car parcel booking failed")
        }

        return response.data.toDomain()
    }

    override suspend fun bookPublicParcel(request: ParcelBookingDomainRequest): ParcelBooking {
        println("ParcelRepo | bookPublicParcel request -> $request")
        val response = api.bookPublicParcel(request.toDto())

        if (!response.success || response.data == null) {
            throw IllegalStateException(response.message ?: "Public parcel booking failed")
        }

        return response.data.toDomain()
    }
}
