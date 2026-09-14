package com.state.greenmiles.com_state_greenmiles.data.remote.dto.route_api.requests

import com.state.greenmiles.com_state_greenmiles.data.remote.dto.CoordinateDto
import kotlinx.serialization.Serializable

@Serializable
data class GetRoutesRequest(
    val startCoordinate: CoordinateDto,
    val endCoordinate: CoordinateDto,
    val waypoints: List<CoordinateDto>,
    val mode: String
)
