package com.state.greenmiles.com_state_greenmiles.data.remote.dto

import kotlinx.serialization.Serializable
@Serializable
data class UserDto(
    val id: String = "",
    val name: String = "",
    val mobile: String = "",
    val email: String = "",
    val isVerified: Boolean = false,
    val isActive: Boolean = false,
    val tripsCompleted: Int = 0,
    val travelCompleted: Int = 0,
    val parcelPosted: Int = 0,
    val parcelDelivered: Int = 0,
    val rating: String = "0.0",
    val totalRatings: Int = 0,
    val tripCancelCountAfterApproval: Int = 0,
    val travelerCancelCountAfterApproval: Int = 0,
    val aadharImageUrl: String? = null,
    val drivingLicenceImageUrl: String? = null,
    val profileImageUrl: String? = null,
    val aadharVerified: Boolean = false,
    val licenceVerified: Boolean = false,
    val dateOfBirth: String? = null,
    val gender: String? = null,
    val address: String? = null,
    val city: String? = null,
    val state: String? = null,
    val pincode: String? = null,
    val lastLoginAt: String? = null,
    val otpVerifiedAt: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)