package com.state.greenmiles.com_state_greenmiles.data.mapper

import com.state.greenmiles.com_state_greenmiles.data.remote.dto.CoordinateDto
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.car_parcel_api.CarDetailsDto
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.car_parcel_api.CarParcelDetailsDto
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.car_parcel_api.CarParcelTripDto
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.car_parcel_api.CarParcelTripRequest
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.car_parcel_api.SearchCarParcelTripRequest
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.public_parcel_api.ParcelTripDto
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.public_parcel_api.PublicParcelTripRequest
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.public_parcel_api.ParcelTransportDetailsDto
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.public_parcel_api.PaginationDto
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.parcel_booking.ParcelBookingData
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.public_parcel_api.SearchPublicParcelTripRequest
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.public_parcel_api.TransportDetailsDto
import com.state.greenmiles.com_state_greenmiles.domain.model.parcel.*
import com.state.greenmiles.com_state_greenmiles.domain.model.trip.Coordinate

// Public Parcel Trip Mappers
fun CreatePublicParcelTripRequest.toDto() = PublicParcelTripRequest(
    startPointName = startPointName,
    endPointName = endPointName,
    startpointlatitude = startCoordinate.latitude,
    startpointlongitude = startCoordinate.longitude,
    endpointlatitude = endCoordinate.latitude,
    endpointlongitude = endCoordinate.longitude,
    tripDate = tripDate,
    tripTime = tripTime,
    availableSpace = when(availableSpace.lowercase()) {
        "small" -> 1
        "medium" -> 2
        "large" -> 3
        "extra_large" -> 4
        else -> 1
    },
    costPerKg = costPerKg,
    transportDetails = transportDetails.toDto(),
    message = message
)

fun TransportDetails.toDto() = TransportDetailsDto(
    type = type,
    name = name,
    operator = operator,
    route = route.ifEmpty { null },
    vehicleNumber = vehicleNumber
)

fun ParcelTripDto.toDomain() = ParcelTrip(
    id = id,
    userId = userId ?: "",
    startPointName = startPointName ?: "",
    endPointName = endPointName ?: "",
    totalDistanceKm = distanceFromUser ?: totalDistanceKm ?: 0.0,
    estimatedDurationSeconds = estimatedDurationSeconds ?: 0L,
    tripDate = tripDate ?: "",
    tripTime = tripTime ?: "",
    availableSpace = when(availableSpace) {
        1 -> "Small"
        2 -> "Medium"
        3 -> "Large"
        4 -> "Extra Large"
        else -> "Small"
    },
    costPerKg = costPerKg ?: 0.0,
    message = message ?: "",
    transportDetails = transportDetails?.toDomain(),
    status = status
)

fun ParcelTransportDetailsDto.toDomain() = TransportDetails(
    type = type ?: "",
    name = name ?: "",
    operator = operator ?: "",
    route = route ?: "",
    vehicleNumber = vehicleNumber ?: ""
)

// Car Parcel Trip Mappers
fun CreateCarParcelTripRequest.toDto() = CarParcelTripRequest(
    selectedPolyline = selectedPolyline,
    startPointName = startPointName,
    endPointName = endPointName,
    totalDistance = totalDistanceKm,
    tripDate = tripDate,
    tripTime = tripTime,
    availableSpace = when(availableSpace.lowercase()) {
        "small" -> 1
        "medium" -> 2
        "large" -> 3
        "extra_large" -> 4
        else -> 1
    },
    costPerKg = costPerKg,
    message = message,
    carDetails = carDetails.toDto(),
    estimatedDurationSeconds = estimatedDurationSeconds
)

fun CarDetails.toDto() = CarDetailsDto(
    model = model,
    licensePlate = licensePlate,
    color = color,
    year = year,
    type = type
)

fun CarParcelTripDto.toDomain() = ParcelTrip(
    id = id,
    userId = userId ?: "",
    startPointName = startPointName ?: "",
    endPointName = endPointName ?: "",
    totalDistanceKm = totalDistanceKm ?: 0.0,
    estimatedDurationSeconds = estimatedDurationSeconds?.toLong() ?: 0L,
    tripDate = tripDate ?: "",
    tripTime = tripTime ?: "",
    availableSpace = when(availableSpace ?: availableSeats) {
        1 -> "Small"
        2 -> "Medium"
        3 -> "Large"
        4 -> "Extra Large"
        else -> (availableSpace ?: availableSeats)?.toString() ?: "0"
    },
    costPerKg = parcelCostPerKg ?: costPerSeat ?: partialCost ?: 0.0,
    message = miscMessage ?: "",
    carDetails = carDetails?.toDomain(),
    status = status
)

fun CarParcelDetailsDto.toDomain() = CarDetails(
    model = model ?: "",
    licensePlate = licensePlate ?: "",
    color = color ?: "",
    year = year ?: 0,
    type = type ?: ""
)

fun PaginationDto.toDomain() = Pagination(
    currentPage = currentPage ?: 0,
    totalCount = totalCount ?: 0,
    itemsPerPage = itemsPerPage ?: 0,
    totalPages = totalPages ?: 0,
    hasNextPage = hasNextPage ?: false,
    hasPreviousPage = hasPreviousPage ?: false,
    nextCursor = nextCursor
)

// Search & Booking Mappers
fun SearchPublicParcelTripDomainRequest.toDto() = SearchPublicParcelTripRequest(
    startCoordinate = CoordinateDto(startCoordinate.latitude, startCoordinate.longitude),
    endCoordinate = CoordinateDto(endCoordinate.latitude, endCoordinate.longitude),
    startPointName = startPointName,
    endPointName = endPointName,
    tripDate = tripDate,
    packageSize = when(packageSize.lowercase()) {
        "small" -> 1
        "medium" -> 2
        "large" -> 3
        "extra_large" -> 4
        else -> 1
    },
    transportType = transportType,
    page = page,
    limit = limit,
    cursor = cursor
)

fun SearchCarParcelTripDomainRequest.toDto() = SearchCarParcelTripRequest(
    startCoordinate = CoordinateDto(startCoordinate.latitude, startCoordinate.longitude),
    endCoordinate = CoordinateDto(endCoordinate.latitude, endCoordinate.longitude),
    startPointName = startPointName,
    endPointName = endPointName,
    tripDate = tripDate,
    packageSize = when(packageSize.lowercase()) {
        "small" -> 1
        "medium" -> 2
        "large" -> 3
        "extra_large" -> 4
        else -> 1
    },
    page = page,
    limit = limit,
    cursor = cursor
)

fun ParcelBookingDomainRequest.toDto() = com.state.greenmiles.com_state_greenmiles.data.remote.dto.parcel_booking.ParcelBookingRequest(
    tripId = tripId,
    startPointName = startPointName,
    endPointName = endPointName,
    packageSize = packageSize,
    offeredPrice = offeredPrice,
    bookingNotes = bookingNotes
)

fun ParcelBookingData.toDomain() = ParcelBooking(
    id = id,
    userId = userId ?: "",
    tripId = tripId ?: "",
    tripType = tripType ?: "",
    tripOwnerId = tripOwnerId ?: "",
    startPointName = startPointName ?: "",
    endPointName = endPointName ?: "",
    packageSize = packageSize ?: "",
    offeredPrice = offeredPrice ?: 0.0,
    status = status ?: "",
    bookingNotes = bookingNotes ?: "",
    rejectionReason = rejectionReason,
    cancellationReason = cancellationReason,
    cancelledAt = cancelledAt,
    cancelledBy = cancelledBy,
    respondedAt = respondedAt,
    respondedBy = respondedBy,
    createdAt = createdAt ?: "",
    updatedAt = updatedAt ?: ""
)
