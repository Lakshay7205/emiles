package com.state.greenmiles.com_state_greenmiles.data.remote.dto.route_api.responses

import com.state.greenmiles.com_state_greenmiles.data.remote.dto.CoordinateDto
import kotlinx.serialization.Serializable

@Serializable

data class RoutesDto(
    val routes: List<RouteDto>,
    val metadata: RouteMetadataDto
)

@Serializable

data class RouteDto(
    val alternativeIndex: Int,
    val polyline: String,
    val distanceKm: Double,
    val durationSeconds: Long,
    val provider: String,
    val estimatedCost: Double,
    val fuelEstimate: Double,
    val summary: String,
    val trafficLevel: String,
    val recommendedCost: Double? = null
)

@Serializable

data class RouteMetadataDto(
    val requestedMode: String,
    val totalAlternatives: Int,
    val calculatedAt: String,
    val startLocation: CoordinateDto,
    val endLocation: CoordinateDto,
    val waypointCount: Int
)
