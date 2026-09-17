package com.state.greenmiles.com_state_greenmiles.domain.model.parcel

data class ParcelBooking(
    val id: String,
    val userId: String,
    val tripId: String,
    val tripType: String,
    val tripOwnerId: String,
    val startPointName: String,
    val endPointName: String,
    val packageSize: String,
    val offeredPrice: Double,
    val status: String,
    val bookingNotes: String,
    val rejectionReason: String? = null,
    val cancellationReason: String? = null,
    val cancelledAt: String? = null,
    val cancelledBy: String? = null,
    val respondedAt: String? = null,
    val respondedBy: String? = null,
    val createdAt: String,
    val updatedAt: String
)
