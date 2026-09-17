package com.state.greenmiles.com_state_greenmiles.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AadhaarKyc(
    val name: String,
    val dob: String,
    val gender: String,
    val address: String,
    val city: String,
    val state: String,
    val pincode: String,
    val maskedAadhaar: String
)
