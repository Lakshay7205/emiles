package com.state.greenmiles.com_state_greenmiles.domain.model.booking

data class Booking(
    val id: String,
    val status: String,
    val seatsRequested: Int,
    val totalCost: String,
    val pickupPointName: String,
    val dropPointName: String,
    val tripStart: String,
    val tripEnd: String,
    val tripOwnerName: String?,
    val tripOwnerMobile: String?,
    val coPassengers:List<String>
)