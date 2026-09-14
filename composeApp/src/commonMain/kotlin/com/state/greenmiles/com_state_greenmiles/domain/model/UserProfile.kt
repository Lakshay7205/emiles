package com.state.greenmiles.com_state_greenmiles.domain.model

data class UserProfile(
    val id: String,
    val name: String,
    val mobile: String,
    val email: String,
    val isVerified: Boolean,
    val isActive: Boolean,
    val tripsCompleted: Int,
    val travelCompleted: Int,
    val parcelPosted: Int,
    val parcelDelivered: Int,
    val rating: Double,
    val totalRatings: Int,
    val profileImageUrl: String?,
    val gender: String?,
    val city: String?,
    val state: String?
)