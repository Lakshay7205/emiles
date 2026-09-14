package com.state.greenmiles.com_state_greenmiles.domain.model.parcel

data class SearchParcelTripsResult(
    val trips: List<ParcelTrip>,
    val pagination: Pagination?
)

data class Pagination(
    val currentPage: Int,
    val totalCount: Int,
    val itemsPerPage: Int,
    val totalPages: Int,
    val hasNextPage: Boolean,
    val hasPreviousPage: Boolean,
    val nextCursor: String?
)
