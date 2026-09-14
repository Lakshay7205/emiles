package com.state.greenmiles.com_state_greenmiles.data.remote.dto.search_trip_apis

import com.state.greenmiles.com_state_greenmiles.data.remote.dto.CoordinateDto
import kotlinx.serialization.Serializable

@Serializable
data class SearchTripsRequest(
    val startCoordinate: CoordinateDto,
    val endCoordinate: CoordinateDto,
    val startPointName: String,
    val endPointName: String,
    val tripDate: String,
    val peopleCount: Int,
    val page: Int,
    val limit: Int,
    val cursor: String?
)
