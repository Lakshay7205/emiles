package com.state.greenmiles.com_state_greenmiles.presentation.screens.parcel

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextOverflow
import com.state.greenmiles.com_state_greenmiles.domain.model.parcel.ParcelTrip
import com.state.greenmiles.com_state_greenmiles.presentation.common.components.*
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ParcelDetailsScreen(
    viewModel: ParcelTripsViewModel,
    onBackClick: () -> Unit,
    onBookingSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val trip = uiState.searchResults.find { it.id == uiState.selectedTripId }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            if (effect is ParcelTripsEffect.ShowToast) {
                snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    if (trip == null) {
        Box(modifier = Modifier.fillMaxSize().background(ColorBackground), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Trip not found", color = ColorText)
                TextButton(onClick = onBackClick) { Text("Go Back", color = ColorAccent) }
            }
        }
        return
    }

    LaunchedEffect(uiState.bookingSuccess) {
        if (uiState.bookingSuccess) {
            onBookingSuccess()
        }
    }

    Scaffold(
        containerColor = ColorBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 52.dp)
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(ColorSurface)
                ) {
                    Icon(Icons.Default.ArrowBack, null, tint = ColorText)
                }
                Text(
                    text = "Trip Details",
                    color = ColorText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        },
        bottomBar = {
            ParcelBookingBar(
                trip = trip,
                packageSize = uiState.packageSize,
                isBooking = uiState.isBooking,
                onBookClick = { 
                    viewModel.onEvent(ParcelTripsEvent.BookTrip)
                }
            )
        }
    )
 { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Transport Info Header
            TransportHeader(trip)

            // Route Card
            RouteSection(trip)

            // Details Card
            DetailsSection(trip)

            // Price Summary
            PriceSummarySection(trip, uiState.packageSize)
            
            Spacer(Modifier.height(80.dp))
        }
    }
}

@Composable
private fun TransportHeader(trip: ParcelTrip) {
    FormCard {
        Row(
            modifier = Modifier.fillMaxWidth().padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(ColorSurface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when(trip.transportDetails?.type?.lowercase() ?: (if (trip.carDetails != null) "car" else "bus")) {
                        "train" -> Icons.Outlined.Train
                        "flight" -> Icons.Outlined.Flight
                        "car" -> Icons.Outlined.DirectionsCar
                        else -> Icons.Outlined.DirectionsBus
                    },
                    contentDescription = null,
                    tint = ColorAccent,
                    modifier = Modifier.size(32.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (trip.carDetails != null) trip.carDetails.model else trip.transportDetails?.name ?: "Public Trip",
                    color = ColorText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (trip.carDetails != null) {
                        "${trip.carDetails.color} ${trip.carDetails.type} • ${trip.carDetails.licensePlate}"
                    } else {
                        "${trip.transportDetails?.operator ?: "Provider"} • ${trip.transportDetails?.vehicleNumber ?: ""}"
                    },
                    color = ColorTextMuted,
                    fontSize = 14.sp
                )
            }
            
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(ColorAccent.copy(alpha = 0.1f))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = trip.status?.replaceFirstChar { it.uppercase() } ?: "Active",
                    color = ColorAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun RouteSection(trip: ParcelTrip) {
    FormCard {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(Icons.Default.TripOrigin, null, tint = ColorAccent, modifier = Modifier.size(20.dp))
                Column {
                    Text("Pickup Location", color = ColorTextMuted, fontSize = 12.sp)
                    Text(trip.startPointName, color = ColorText, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            
            Box(modifier = Modifier.padding(start = 9.dp).width(2.dp).height(24.dp).background(ColorBorder))
            
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(Icons.Default.LocationOn, null, tint = ColorAmber, modifier = Modifier.size(20.dp))
                Column {
                    Text("Drop-off Location", color = ColorTextMuted, fontSize = 12.sp)
                    Text(trip.endPointName, color = ColorText, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun DetailsSection(trip: ParcelTrip) {
    FormCard {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Trip Details", color = ColorText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            
            Row(modifier = Modifier.fillMaxWidth()) {
                DetailItem(Icons.Outlined.CalendarToday, "Date", trip.tripDate, Modifier.weight(1f))
                DetailItem(Icons.Outlined.Schedule, "Time", trip.tripTime, Modifier.weight(1f))
            }
            
            HorizontalDivider(color = ColorBorder, thickness = 1.dp)
            
            Row(modifier = Modifier.fillMaxWidth()) {
                DetailItem(Icons.Outlined.Inventory2, "Available Space", trip.availableSpace, Modifier.weight(1f))
                DetailItem(Icons.Outlined.Route, "Distance", "${trip.totalDistanceKm} Km", Modifier.weight(1f))
            }
            
            if (trip.message.isNotBlank()) {
                HorizontalDivider(color = ColorBorder, thickness = 1.dp)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Message from Provider", color = ColorTextMuted, fontSize = 12.sp)
                    Text(trip.message, color = ColorText, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
private fun DetailItem(icon: ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(
            modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(ColorSurface),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = ColorAccent, modifier = Modifier.size(18.dp))
        }
        Column {
            Text(label, color = ColorTextMuted, fontSize = 11.sp)
            Text(value, color = ColorText, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun PriceSummarySection(trip: ParcelTrip, selectedSize: String) {
    val weight = when(selectedSize.lowercase()) {
        "small" -> 2
        "medium" -> 5
        "large" -> 15
        else -> 2
    }
    val total = trip.costPerKg * weight
    FormCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Price Summary", color = ColorText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Price per Kg", color = ColorTextMuted, fontSize = 14.sp)
                Text("₹${trip.costPerKg}", color = ColorText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Requested Space", color = ColorTextMuted, fontSize = 14.sp)
                Text("${selectedSize.replaceFirstChar { it.uppercase() }} (approx. $weight Kg)", color = ColorText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            
            HorizontalDivider(color = ColorBorder, thickness = 1.dp)
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total Estimated Cost", color = ColorText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("₹$total", color = ColorAccent, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
private fun ParcelBookingBar(trip: ParcelTrip, packageSize: String, isBooking: Boolean, onBookClick: () -> Unit) {
    val weight = when(packageSize.lowercase()) {
        "small" -> 2
        "medium" -> 5
        "large" -> 15
        else -> 2
    }
    Surface(
        color = ColorSurface,
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 24.dp)
                .padding(bottom = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.weight(0.4f)) {
                Text("Estimated Total", color = ColorTextMuted, fontSize = 12.sp)
                Text("₹${trip.costPerKg * weight}", color = ColorText, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
            }
            
            Button(
                onClick = onBookClick,
                enabled = !isBooking,
                modifier = Modifier.weight(0.6f).height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ColorAccent)
            ) {
                if (isBooking) {
                    CircularProgressIndicator(color = Color(0xFF052E16), modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text("Book Now", color = Color(0xFF052E16), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
