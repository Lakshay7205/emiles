package com.state.greenmiles.com_state_greenmiles.data.mappers

import com.state.greenmiles.com_state_greenmiles.data.remote.dto.my_bookings.BookingDto
import com.state.greenmiles.com_state_greenmiles.domain.model.booking.Booking

fun BookingDto.toDomain(): Booking {
    return Booking(
        id = id,
        status = status,
        seatsRequested = seatsRequested,
        totalCost = totalCost,
        pickupPointName = pickupPointName,
        dropPointName = dropPointName,
        tripStart = trip.startPointName,
        tripEnd = trip.endPointName,
        tripOwnerName = tripOwner?.name,
        tripOwnerMobile = if (status == "approved") tripOwner?.mobile else null,
        coPassengers = otherPassengers?.map { it.name ?: "" } ?: emptyList()
    )
}

