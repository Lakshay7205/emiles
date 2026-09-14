package com.state.greenmiles.com_state_greenmiles.data.remote.dto.search_trip_apis

import com.state.greenmiles.com_state_greenmiles.data.remote.dto.Create_trip_API.TripDto
import kotlinx.serialization.Serializable

@Serializable
data class SearchTripsDto(
    val trips: List<TripDto>,
    val pagination: PaginationDto
)


@Serializable
data class PaginationDto(
    val currentPage: Int,
    val totalPages: Int,
    val totalCount: Int,
    val limit: Int,
    val hasNextPage: Boolean,
    val hasPreviousPage: Boolean,
    val nextCursor: String? = null
)
