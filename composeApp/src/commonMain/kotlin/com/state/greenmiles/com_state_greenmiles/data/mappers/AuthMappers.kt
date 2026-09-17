package com.state.greenmiles.com_state_greenmiles.data.mappers

import com.state.greenmiles.com_state_greenmiles.data.remote.dto.AuthDto
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.OtpDto
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.TempTokenDto
import com.state.greenmiles.com_state_greenmiles.data.remote.dto.UserDto
import com.state.greenmiles.com_state_greenmiles.domain.model.AuthResult
import com.state.greenmiles.com_state_greenmiles.domain.model.OtpResult
import com.state.greenmiles.com_state_greenmiles.domain.model.TempTokenResult
import com.state.greenmiles.com_state_greenmiles.domain.model.User

fun UserDto.toDomain() = User(
    id = id,
    name = name,
    mobile = mobile,
    email = email,
    isVerified = isVerified,
    createdAt = createdAt
)

fun AuthDto.toDomain() = AuthResult(
    userId = userId ?: "",
    token = token ?: "",
    message = message ?:""
)

fun OtpDto.toDomain() = OtpResult(
    message = message,
    otpToken = otpToken,
    expiresIn = expiresIn
)

fun TempTokenDto.toDomain() = TempTokenResult(
    message = message,
    tempToken = tempToken ?: verifiedToken ?: "",
    expiresIn = expiresIn
)
