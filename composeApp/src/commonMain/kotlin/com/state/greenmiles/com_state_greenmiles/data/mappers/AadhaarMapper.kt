package com.state.greenmiles.com_state_greenmiles.data.mappers

import com.state.greenmiles.com_state_greenmiles.data.remote.dto.AadhaarKycDto
import com.state.greenmiles.com_state_greenmiles.domain.model.AadhaarKyc

fun AadhaarKycDto.toDomain(): AadhaarKyc = AadhaarKyc(
    name = personalInfo?.name ?: "N/A",
    dob = personalInfo?.dateOfBirth ?: "N/A",
    gender = mapGender(personalInfo?.gender),
    address = address?.let { formatAddress(personalInfo?.careOf, it) } ?: "N/A",
    city = address?.district ?: address?.village ?: address?.locality ?: "N/A",
    state = address?.state ?: "N/A",
    pincode = address?.pincode ?: "N/A",
    maskedAadhaar = maskedAadhaar ?: ""
)

private fun mapGender(aadhaarGender: String?): String {
    return when (aadhaarGender?.uppercase()) {
        "M", "MALE" -> "male"
        "F", "FEMALE" -> "female"
        else -> "other"
    }
}

private fun formatAddress(careOf: String?, dto: com.state.greenmiles.com_state_greenmiles.data.remote.dto.AadhaarAddressDto): String {
    val addressParts = listOfNotNull(
        careOf,
        dto.house,
        dto.street,
        dto.locality,
        dto.village,
        dto.district,
        dto.state,
        dto.pincode
    )
    return addressParts.filter { it.isNotBlank() }.joinToString(", ")
}