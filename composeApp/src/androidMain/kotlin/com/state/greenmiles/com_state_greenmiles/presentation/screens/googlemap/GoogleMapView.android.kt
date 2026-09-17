package com.state.greenmiles.com_state_greenmiles.presentation.screens.googlemap

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.state.greenmiles.com_state_greenmiles.domain.model.trip.Coordinate
import com.state.greenmiles.com_state_greenmiles.domain.model.trip.Route

// androidMain
// androidMain

import androidx.compose.runtime.LaunchedEffect

import androidx.compose.ui.graphics.Color

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory

import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.Circle

import com.google.maps.android.compose.Polyline


@Composable
actual fun GoogleMapView(
    modifier: Modifier,
    lat: Double,
    lng: Double,
    zoom: Float,
    startLocation: Coordinate?,
    endLocation: Coordinate?,
    routes: List<Route>,
    selectedRoute: Route?
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(lat, lng), zoom)
    }

    // Update camera to fit both markers or routes
    LaunchedEffect(startLocation, endLocation, routes) {
        if (routes.isNotEmpty()) {
            // If routes exist, fit to show all routes
            val boundsBuilder = LatLngBounds.Builder()

            routes.forEach { route ->
                val points = decodePolyline(route.polyline)
                points.forEach { point ->
                    boundsBuilder.include(point)
                }
            }

            try {
                val bounds = boundsBuilder.build()
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngBounds(bounds, 100)
                )
            } catch (e: Exception) {
                // Fallback if bounds are invalid
            }
        } else if (startLocation != null && endLocation != null) {
            // If only markers, fit to show both
            val boundsBuilder = LatLngBounds.Builder()
            boundsBuilder.include(LatLng(startLocation.latitude, startLocation.longitude))
            boundsBuilder.include(LatLng(endLocation.latitude, endLocation.longitude))

            try {
                val bounds = boundsBuilder.build()
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngBounds(bounds, 100)
                )
            } catch (e: Exception) {
                // Fallback if bounds are invalid
            }
        }
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState
    ) {
        // Start Location Marker (Green)
        startLocation?.let { start ->
            Marker(
                state = MarkerState(position = LatLng(start.latitude, start.longitude)),
                title = "Pickup",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
            )
        }

        // End Location Marker (Red)
        endLocation?.let { end ->
            Marker(
                state = MarkerState(position = LatLng(end.latitude, end.longitude)),
                title = "Drop-off",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
            )
        }

        // Draw all routes
        routes.forEachIndexed { index, route ->
            val isSelected = selectedRoute?.index == route.index
            val points = decodePolyline(route.polyline)

            if (points.isNotEmpty()) {
                Polyline(
                    points = points,
                    color = if (isSelected) {
                        getRouteColor(route.index)
                    } else {
                        getRouteColor(route.index).copy(alpha = 0.5f)
                    },
                    width = if (isSelected) 15f else 10f,
                    zIndex = if (isSelected) 2f else 1f
                )
            }
        }
    }
}

// Decode Google Maps encoded polyline
fun decodePolyline(encoded: String): List<LatLng> {
    val poly = mutableListOf<LatLng>()
    var index = 0
    val len = encoded.length
    var lat = 0
    var lng = 0

    while (index < len) {
        var b: Int
        var shift = 0
        var result = 0
        do {
            b = encoded[index++].code - 63
            result = result or (b and 0x1f shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lat += dlat

        shift = 0
        result = 0
        do {
            b = encoded[index++].code - 63
            result = result or (b and 0x1f shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lng += dlng

        val latLng = LatLng(
            lat.toDouble() / 1E5,
            lng.toDouble() / 1E5
        )
        poly.add(latLng)
    }

    return poly
}

fun getRouteColor(index: Int): Color {
    val colors = listOf(
        Color(0xFF4285F4), // Blue
        Color(0xFF34A853), // Green
        Color(0xFFEA4335), // Red
        Color(0xFFFBBC04), // Yellow
        Color(0xFF9C27B0), // Purple
    )
    return colors[index % colors.size]
}

@Composable
actual fun TripRouteMapView(
    modifier: Modifier,
    tripPolyline: String,
    passengerStart: com.state.greenmiles.com_state_greenmiles.domain.model.trip_details.Coordinate,
    passengerEnd: com.state.greenmiles.com_state_greenmiles.domain.model.trip_details.Coordinate,
    highlightColor: Color
) {
    val fullRoutePoints = decodePolyline(tripPolyline)

    val tripEndPoint = fullRoutePoints.lastOrNull()

    val isPassengerNotFinalStop =
        tripEndPoint != null &&
                (
                        tripEndPoint.latitude != passengerEnd.latitude ||
                                tripEndPoint.longitude != passengerEnd.longitude
                        )

    // Extract passenger segment from full route
    val passengerSegmentPoints = extractSegment(
        fullRoutePoints,
        passengerStart,
        passengerEnd
    )

    val cameraPositionState = rememberCameraPositionState()

    // Auto zoom to passenger segment
    LaunchedEffect(passengerSegmentPoints) {
        if (passengerSegmentPoints.isNotEmpty()) {
            val boundsBuilder = LatLngBounds.Builder()

            passengerSegmentPoints.forEach {
                boundsBuilder.include(it)
            }

            try {
                val bounds = boundsBuilder.build()
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngBounds(bounds, 150)
                )
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState
    ) {

        // ✅ Full Trip Route (Driver) - Gray Background
        if (fullRoutePoints.isNotEmpty()) {
            Polyline(
                points = fullRoutePoints,
                color = Color.Gray,
                width = 10f,
                zIndex = 1f
            )
        }

        // ✅ Passenger Segment Highlight (Bold)
        if (passengerSegmentPoints.isNotEmpty()) {
            Polyline(
                points = passengerSegmentPoints,
                color = highlightColor,
                width = 16f,
                zIndex = 2f
            )
        }

        // ✅ Passenger Pickup Marker (Green)
        Marker(
            state = MarkerState(
                position = LatLng(
                    passengerStart.latitude,
                    passengerStart.longitude
                )
            ),
            title = "Your Pickup",
            icon = BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_GREEN
            )
        )

        // ✅ Passenger Drop Marker (Red)
        Marker(
            state = MarkerState(
                position = LatLng(
                    passengerEnd.latitude,
                    passengerEnd.longitude
                )
            ),
            title = "Your Drop",
            icon = BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_RED
            )
        )

            Circle(
                center = LatLng(
                    passengerEnd.latitude,
                    passengerEnd.longitude
                ),
                radius = 200.0, // meters
                fillColor = highlightColor.copy(alpha = 0.25f),
                strokeColor = highlightColor,
                strokeWidth = 6f,
                zIndex = 3f
            )




    }
}
fun extractSegment(
    routePoints: List<LatLng>,
    start: com.state.greenmiles.com_state_greenmiles.domain.model.trip_details.Coordinate,
    end: com.state.greenmiles.com_state_greenmiles.domain.model.trip_details.Coordinate
): List<LatLng> {

    if (routePoints.isEmpty()) return emptyList()

    val startLatLng = LatLng(start.latitude, start.longitude)
    val endLatLng = LatLng(end.latitude, end.longitude)

    // Find nearest polyline index to passenger start
    val startIndex = routePoints.minByOrNull {
        distanceBetween(it, startLatLng)
    }?.let { routePoints.indexOf(it) } ?: 0

    // Find nearest polyline index to passenger end
    val endIndex = routePoints.minByOrNull {
        distanceBetween(it, endLatLng)
    }?.let { routePoints.indexOf(it) } ?: routePoints.lastIndex

    // Ensure correct ordering
    val from = minOf(startIndex, endIndex)
    val to = maxOf(startIndex, endIndex)

    return routePoints.subList(from, to + 1)
}


fun distanceBetween(p1: LatLng, p2: LatLng): Double {
    val dx = p1.latitude - p2.latitude
    val dy = p1.longitude - p2.longitude
    return dx * dx + dy * dy
}

