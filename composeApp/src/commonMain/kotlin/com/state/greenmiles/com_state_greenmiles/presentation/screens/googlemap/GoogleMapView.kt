package com.state.greenmiles.com_state_greenmiles.presentation.screens.googlemap

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.state.greenmiles.com_state_greenmiles.domain.model.trip.Coordinate

import com.state.greenmiles.com_state_greenmiles.domain.model.trip.Route

@Composable
expect fun GoogleMapView(
    modifier: Modifier = Modifier,
    lat: Double,
    lng: Double,
    zoom: Float = 14f,
    startLocation: Coordinate? = null,
    endLocation: Coordinate? = null,
    routes: List<Route> = emptyList(),
    selectedRoute: Route? = null
)

@Composable
expect fun TripRouteMapView(
    modifier: Modifier = Modifier,
    tripPolyline: String,
    passengerStart: com.state.greenmiles.com_state_greenmiles.domain.model.trip_details.Coordinate,
    passengerEnd: com.state.greenmiles.com_state_greenmiles.domain.model.trip_details.Coordinate,
    highlightColor: Color = Color.Blue
)
