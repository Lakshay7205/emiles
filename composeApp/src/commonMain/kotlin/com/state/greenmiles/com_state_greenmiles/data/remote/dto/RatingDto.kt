package com.state.greenmiles.com_state_greenmiles.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RatingDto(
    val userId: String = "",
    val rating: Int = 0,
    val totalRatings: Int = 0,
    val updatedAt: String? = null
)