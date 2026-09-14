package com.state.greenmiles.com_state_greenmiles.data.mappers

import com.state.greenmiles.com_state_greenmiles.data.remote.dto.ProfileResponseDto
import com.state.greenmiles.com_state_greenmiles.domain.model.UserProfile

fun ProfileResponseDto.toDomain(): UserProfile {
    return UserProfile(
        id = user.id,
        name = user.name,
        mobile = user.mobile,
        email = user.email,
        isVerified = user.isVerified,
        isActive = user.isActive,
        tripsCompleted = user.tripsCompleted,
        travelCompleted = user.travelCompleted,
        parcelPosted = user.parcelPosted,
        parcelDelivered = user.parcelDelivered,
        rating = user.rating.toDoubleOrNull() ?: 0.0,
        totalRatings = user.totalRatings,
        profileImageUrl = user.profileImageUrl,
        gender = user.gender,
        city = user.city,
        state = user.state
    )
}