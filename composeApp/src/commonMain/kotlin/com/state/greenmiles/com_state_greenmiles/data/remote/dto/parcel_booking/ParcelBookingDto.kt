package com.state.greenmiles.com_state_greenmiles.data.remote.dto.parcel_booking

import kotlinx.serialization.Serializable

@Serializable
data class ParcelBookingRequest(
    val tripId: String,
    val startPointName: String,
    val endPointName: String,
    val packageSize: String,
    val offeredPrice: Double,
    val bookingNotes: String
)

@Serializable
data class ParcelBookingResponse(
    val success: Boolean,
    val message: String,
    val data: ParcelBookingData? = null
)

@Serializable
data class ParcelBookingData(
    val id: String,
    val userId: String? = null,
    val tripId: String? = null,
    val tripType: String? = null,
    val tripOwnerId: String? = null,
    val startPointName: String? = null,
    val endPointName: String? = null,
    val packageSize: String? = null,
    val offeredPrice: Double? = null,
    val status: String? = null,
    val bookingNotes: String? = null,
    val rejectionReason: String? = null,
    val cancellationReason: String? = null,
    val cancelledAt: String? = null,
    val cancelledBy: String? = null,
    val respondedAt: String? = null,
    val respondedBy: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
