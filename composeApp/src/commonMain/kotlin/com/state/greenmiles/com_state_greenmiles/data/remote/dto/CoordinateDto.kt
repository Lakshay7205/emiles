package com.state.greenmiles.com_state_greenmiles.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable

data class CoordinateDto(
    val latitude: Double,
    val longitude: Double
)
