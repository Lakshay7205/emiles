package com.state.greenmiles.com_state_greenmiles.presentation.screens.googlemap

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.readValue

import cocoapods.GoogleMaps.GMSCameraPosition
import cocoapods.GoogleMaps.GMSCoordinateBounds
import cocoapods.GoogleMaps.GMSMapView
import cocoapods.GoogleMaps.GMSMarker
import cocoapods.GoogleMaps.GMSMutablePath
import cocoapods.GoogleMaps.GMSPolyline
import cocoapods.GoogleMaps.GMSCameraUpdate
import com.state.greenmiles.com_state_greenmiles.domain.model.trip.Coordinate
import com.state.greenmiles.com_state_greenmiles.domain.model.trip.Route
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGRectZero
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.UIKit.UIColor

@OptIn(ExperimentalForeignApi::class)
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
    val mapView = remember {
        val camera = GMSCameraPosition.cameraWithLatitude(
            latitude = lat,
            longitude = lng,
            zoom = zoom
        )

        GMSMapView.mapWithFrame(
            frame = CGRectZero.readValue(),
            camera = camera
        )
    }

    DisposableEffect(startLocation, endLocation, routes, selectedRoute) {
        mapView.clear()

        // Start Marker
        startLocation?.let { start ->
            GMSMarker().apply {
                position = CLLocationCoordinate2DMake(start.latitude, start.longitude)
                title = "Pickup"
                icon = GMSMarker.markerImageWithColor(UIColor.greenColor)
                map = mapView
            }
        }

        // End Marker
        endLocation?.let { end ->
            GMSMarker().apply {
                position = CLLocationCoordinate2DMake(end.latitude, end.longitude)
                title = "Drop-off"
                icon = GMSMarker.markerImageWithColor(UIColor.redColor)
                map = mapView
            }
        }

        // Draw routes
        routes.forEach { route ->
            val isSelected = selectedRoute?.index == route.index
            val path = decodePolylineIOS(route.polyline)

            val polyline = GMSPolyline.polylineWithPath(path)
            polyline.strokeColor = getRouteUIColor(route.index, isSelected)
            polyline.strokeWidth = if (isSelected) 6.0 else 4.0
            polyline.map = mapView
        }

        // Camera fitting
        if (routes.isNotEmpty()) {
            val firstRoute = routes.first()
            val firstPath = decodePolylineIOS(firstRoute.polyline)
            val firstCoord = firstPath.coordinateAtIndex(0u)

            var bounds = GMSCoordinateBounds(firstCoord, firstCoord)

            routes.forEach { route ->
                val path = decodePolylineIOS(route.polyline)
                val pathBounds = GMSCoordinateBounds(path.coordinateAtIndex(0u), path.coordinateAtIndex(path.count() - 1u))
                bounds = bounds.includingBounds(pathBounds)
            }

            val update = GMSCameraUpdate.fitBounds(bounds, withPadding = 100.0)
            mapView.moveCamera(update)

        } else if (startLocation != null && endLocation != null) {
            var bounds = GMSCoordinateBounds()
            bounds = bounds.includingCoordinate(
                CLLocationCoordinate2DMake(startLocation.latitude, startLocation.longitude)
            )
            bounds = bounds.includingCoordinate(
                CLLocationCoordinate2DMake(endLocation.latitude, endLocation.longitude)
            )

            val update = GMSCameraUpdate.fitBounds(bounds, withPadding = 100.0)
            mapView.moveCamera(update)
        }

        onDispose {
            mapView.clear()
        }
    }

    UIKitView(
        modifier = modifier,
        factory = { mapView },
        update = {}
    )
}
@OptIn(ExperimentalForeignApi::class)
fun decodePolylineIOS(encoded: String): GMSMutablePath {
    val path = GMSMutablePath()
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
            result = result or ((b and 0x1f) shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlat = if ((result and 1) != 0) (result shr 1).inv() else result shr 1
        lat += dlat

        shift = 0
        result = 0
        do {
            b = encoded[index++].code - 63
            result = result or ((b and 0x1f) shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlng = if ((result and 1) != 0) (result shr 1).inv() else result shr 1
        lng += dlng

        path.addLatitude(
            lat.toDouble() / 1E5,
            longitude = lng.toDouble() / 1E5
        )
    }

    return path
}
fun getRouteUIColor(index: Int, isSelected: Boolean): UIColor {
    val alpha = if (isSelected) 1.0 else 0.5

    return when (index % 5) {
        0 -> UIColor(red = 0.26, green = 0.52, blue = 0.96, alpha = alpha) // Blue
        1 -> UIColor(red = 0.20, green = 0.66, blue = 0.33, alpha = alpha) // Green
        2 -> UIColor(red = 0.92, green = 0.26, blue = 0.21, alpha = alpha) // Red
        3 -> UIColor(red = 0.98, green = 0.74, blue = 0.02, alpha = alpha) // Yellow
        else -> UIColor(red = 0.61, green = 0.15, blue = 0.69, alpha = alpha) // Purple
    }
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun TripRouteMapView(
    modifier: Modifier,
    tripPolyline: String,
    passengerStart: com.state.greenmiles.com_state_greenmiles.domain.model.trip_details.Coordinate,
    passengerEnd: com.state.greenmiles.com_state_greenmiles.domain.model.trip_details.Coordinate,
    highlightColor: Color
) {
    val mapView = remember {
        val camera = GMSCameraPosition.cameraWithLatitude(
            latitude = passengerStart.latitude,
            longitude = passengerStart.longitude,
            zoom = 13f
        )

        GMSMapView.mapWithFrame(
            frame = CGRectZero.readValue(),
            camera = camera
        )
    }

    DisposableEffect(tripPolyline, passengerStart, passengerEnd) {
        mapView.clear()

        // ✅ Decode full route path
        val fullPath = decodePolylineIOS(tripPolyline)

        // ✅ Extract passenger segment path
        val passengerPath = extractPassengerSegmentIOS(
            fullPath,
            passengerStart,
            passengerEnd
        )

        // ✅ Draw full trip route (Gray background)
        val fullPolyline = GMSPolyline.polylineWithPath(fullPath)
        fullPolyline.strokeColor = UIColor.grayColor.colorWithAlphaComponent(0.4)
        fullPolyline.strokeWidth = 5.0
        fullPolyline.map = mapView

        // ✅ Draw passenger highlighted segment
        val highlightPolyline = GMSPolyline.polylineWithPath(passengerPath)
        highlightPolyline.strokeColor = highlightColor.toUIColor()
        highlightPolyline.strokeWidth = 8.0
        highlightPolyline.map = mapView

        // ✅ Passenger Pickup Marker (Green)
        GMSMarker().apply {
            position = CLLocationCoordinate2DMake(
                passengerStart.latitude,
                passengerStart.longitude
            )
            title = "Your Pickup"
            icon = GMSMarker.markerImageWithColor(UIColor.greenColor)
            map = mapView
        }

        // ✅ Passenger Drop Marker (Red)
        GMSMarker().apply {
            position = CLLocationCoordinate2DMake(
                passengerEnd.latitude,
                passengerEnd.longitude
            )
            title = "Your Drop"
            icon = GMSMarker.markerImageWithColor(UIColor.redColor)
            map = mapView
        }

        // ✅ Camera fit passenger segment
        if (passengerPath.count() > 1u) {
            val startCoord = passengerPath.coordinateAtIndex(0u)
            val endCoord = passengerPath.coordinateAtIndex(passengerPath.count() - 1u)

            var bounds = GMSCoordinateBounds(startCoord, endCoord)

            val update = GMSCameraUpdate.fitBounds(bounds, withPadding = 120.0)
            mapView.moveCamera(update)
        }

        onDispose {
            mapView.clear()
        }
    }

    UIKitView(
        modifier = modifier,
        factory = { mapView },
        update = {}
    )
}


@OptIn(ExperimentalForeignApi::class)
fun extractPassengerSegmentIOS(
    fullPath: GMSMutablePath,
    start: Coordinate,
    end: Coordinate
): GMSMutablePath {

    val passengerPath = GMSMutablePath()

    val startLatLng = CLLocationCoordinate2DMake(start.latitude, start.longitude)
    val endLatLng = CLLocationCoordinate2DMake(end.latitude, end.longitude)

    // Find nearest indexes
    val startIndex = findNearestIndexIOS(fullPath, startLatLng)
    val endIndex = findNearestIndexIOS(fullPath, endLatLng)

    val from = minOf(startIndex, endIndex)
    val to = maxOf(startIndex, endIndex)

    for (i in from..to) {
        val coord = fullPath.coordinateAtIndex(i.toULong()) // Changed to toULong()
        passengerPath.addCoordinate(coord)
    }

    return passengerPath
}

@OptIn(ExperimentalForeignApi::class)
fun findNearestIndexIOS(
    path: GMSMutablePath,
    target: CLLocationCoordinate2D // Removed platform.CoreLocation prefix
): Int {

    var nearestIndex = 0
    var minDistance = Double.MAX_VALUE

    val count = path.count().toInt()

    for (i in 0 until count) {
        val coord = path.coordinateAtIndex(i.toULong()) // Changed to toULong()

        // Access the coordinate properties using useContents
        memScoped {
            val point = coord.ptr.pointed
            val dx = point.latitude - target.latitude
            val dy = point.longitude - target.longitude
            val dist = dx * dx + dy * dy

            if (dist < minDistance) {
                minDistance = dist
                nearestIndex = i
            }
        }
    }

    return nearestIndex
}

fun Color.toUIColor(): UIColor {
    return UIColor(
        red = this.red.toDouble(),
        green = this.green.toDouble(),
        blue = this.blue.toDouble(),
        alpha = this.alpha.toDouble()
    )
}

