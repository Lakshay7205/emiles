package com.state.greenmiles.com_state_greenmiles.domain.repository

import com.state.greenmiles.com_state_greenmiles.domain.model.parcel.*
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.public_parcel_api.SearchPublicParcelTripRequest
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.car_parcel_api.SearchCarParcelTripRequest
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.parcel_booking.ParcelBookingRequest

interface ParcelRepository {
    suspend fun createPublicParcelTrip(request: CreatePublicParcelTripRequest): ParcelTrip
    suspend fun searchPublicParcelTrips(request: SearchPublicParcelTripDomainRequest): SearchParcelTripsResult
    
    suspend fun createCarParcelTrip(request: CreateCarParcelTripRequest): ParcelTrip
    suspend fun searchCarParcelTrips(request: SearchCarParcelTripDomainRequest): SearchParcelTripsResult

    suspend fun bookCarParcel(request: ParcelBookingDomainRequest): ParcelBooking
    suspend fun bookPublicParcel(request: ParcelBookingDomainRequest): ParcelBooking
}
