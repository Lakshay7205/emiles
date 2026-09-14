package com.state.greenmiles.com_state_greenmiles.data.mappers

import com.state.greenmiles.com_state_greenmiles.data.remote.dto.Create_trip_API.TripDto
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.my_created_trips.MyTripDto
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.route_api.responses.RoutesDto
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.search_trip_apis.SearchTripsDto
import com.state.greenmiles.com_state_greenmiles.domain.model.trip.MyTrip
import com.state.greenmiles.com_state_greenmiles.domain.model.trip.Route
import com.state.greenmiles.com_state_greenmiles.domain.model.trip.RoutesResult
import com.state.greenmiles.com_state_greenmiles.domain.model.trip.SearchTripsResultPage
import com.state.greenmiles.com_state_greenmiles.domain.model.trip.Trip
import com.state.greenmiles.com_state_greenmiles.domain.model.trip.Trips

fun RoutesDto.toDomain(): RoutesResult =
    RoutesResult(
        routes = routes.map {
            Route(
                index = it.alternativeIndex,
                polyline = it.polyline,
                distanceKm = it.distanceKm,
                durationSeconds = it.durationSeconds,
                estimatedCost = it.estimatedCost,
                summary = it.summary,
                recommendedCost = it.recommendedCost
            )
        }
    )

fun TripDto.toDomain(): Trip =
    Trip(
        id = id ?: "",
        price = estimatedTotalCost ?: 0.0,
        seats = availableSeats ?: 0,
        time = tripTime ?: "N/A",
        carType = carDetails?.type ?: "N/A",
        carName = carDetails?.model ?: "N/A",
        isUserVerified = userIsVerified ?: false,
        rating = userRating ?: 0.0,
        userName = userName ?: "N/A",
        gender = userGender ?: "",
        estimatedDistanceKm = estimatedRideDistance,
        miscMessage = miscMessage,
        recommendedCost = recommendedCost
    )

fun SearchTripsDto.toDomain(): SearchTripsResultPage =
    SearchTripsResultPage(
        trips = trips.map { it.toDomain() },
        nextCursor = pagination.nextCursor
    )
fun MyTripDto.toDomain(): Trips {
    return Trips(
        id = id,
        startPointName = startPointName,
        endPointName = endPointName,
        tripDate = tripDate,
        tripTime = tripTime,
        totalDistanceKm = totalDistanceKm,
        availableSeats = availableSeats,
        costPerSeat = costPerSeat,
        status = status,
        pendingBookings = bookingCounts.pending,
        approvedBookings = bookingCounts.approved
    )
}