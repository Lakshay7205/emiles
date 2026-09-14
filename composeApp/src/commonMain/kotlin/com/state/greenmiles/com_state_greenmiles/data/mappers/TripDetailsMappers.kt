package com.state.greenmiles.com_state_greenmiles.data.mappers

import com.state.greenmiles.com_state_greenmiles.data.remote.dto.trip_details.CarDetailsDto
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.trip_details.CoordinateDto
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.trip_details.TripDetailsData
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.trip_details.TripUserDto
import com.state.greenmiles.com_state_greenmiles.domain.model.trip_details.CarDetails
import com.state.greenmiles.com_state_greenmiles.domain.model.trip_details.Coordinate
import com.state.greenmiles.com_state_greenmiles.domain.model.trip_details.TripDetails
import com.state.greenmiles.com_state_greenmiles.domain.model.trip_details.TripUser


/**
 * Mapper to convert TripDetailsDto to TripDetails domain model
 */


    fun TripDetailsData.toDomain(): TripDetails {
        return TripDetails(
            id = id ?: "",
            userId = userId ?: "",
            selectedPolyline = selectedPolyline ?: "",
            startPointName = startPointName ?: "",
            endPointName = endPointName ?: "",
            startCoordinate = startCoordinate?.toDomain() ?: Coordinate(0.0, 0.0),
            endCoordinate = endCoordinate?.toDomain() ?: Coordinate(0.0, 0.0),
            totalDistanceKm = totalDistanceKm ?: 0.0,
            estimatedDurationSeconds = estimatedDurationSeconds ?: 0,
            tripDate = tripDate ?: "",
            tripTime = tripTime ?: "",
            availableSeats = availableSeats ?: 0,
            bookedSeats = bookedSeats ?: 0,
            costPerSeat = costPerSeat ?: 0.0,
            costPerKm = costPerKm ?: 0.0,
            carDetails = carDetails?.toDomain() ?: CarDetails("", 0, "", "", ""),
            womanAccompany = womanAccompany ?: false,
            status = status ?: "",
            createdAt = createdAt ?: "",
            updatedAt = updatedAt ?: "",
            user = user?.toDomain() ?: TripUser("", "", 0.0, false, ""),
            miscMessage = miscMessage,
            recommendedCost = recommendedCost
        )
    }

    fun CoordinateDto.toDomain(): Coordinate {
        return Coordinate(
            latitude = latitude ?: 0.0,
            longitude = longitude ?: 0.0
        )
    }

    fun CarDetailsDto.toDomain(): CarDetails {
        return CarDetails(
            type = type ?: "",
            year = year ?: 0,
            color = color ?: "",
            model = model ?: "",
            licensePlate = licensePlate ?: ""
        )
    }

    fun TripUserDto.toDomain(): TripUser {
        return TripUser(
            id = id ?: "",
            name = name ?: "",
            rating = rating ?: 0.0,
            isVerified = isVerified ?: false,
            gender = gender ?: ""
        )
    }
