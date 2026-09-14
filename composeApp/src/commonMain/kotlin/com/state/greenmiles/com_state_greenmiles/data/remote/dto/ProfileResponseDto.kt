package com.state.greenmiles.com_state_greenmiles.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProfileResponseDto(
    val user: UserDto,
    val rating: RatingDto
)