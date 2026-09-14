package com.state.greenmiles.com_state_greenmiles.data.mappers

import com.state.greenmiles.com_state_greenmiles.data.remote.dto.CoordinateDto
import com.state.greenmiles.com_state_greenmiles.domain.model.trip.Coordinate

fun Coordinate.toDto() = CoordinateDto(latitude, longitude)
