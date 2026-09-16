package com.state.greenmiles.com_state_greenmiles.presentation.screens.car_pool__search_ride.RideDetailsScreen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.state.greenmiles.com_state_greenmiles.domain.model.trip_details.CarDetails
import com.state.greenmiles.com_state_greenmiles.domain.model.trip_details.TripDetails
import com.state.greenmiles.com_state_greenmiles.domain.model.trip_details.TripUser
import com.state.greenmiles.com_state_greenmiles.presentation.screens.car_pool__search_ride.SearchRideViewModel
import com.state.greenmiles.com_state_greenmiles.presentation.screens.googlemap.TripRouteMapView
import com.state.greenmiles.com_state_greenmiles.presentation.screens.tripScreen.MyTripsViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.abs
import kotlin.math.round

// ─── Design Tokens ────────────────────────────────────────────────────────────

private val ColorBackground = Color(0xFF0D1A12)
private val ColorSurface    = Color(0xFF142B1C)
private val ColorCard       = Color(0xFF1C3826)
private val ColorBorder     = Color(0xFF2E5C3A)
private val ColorAccent     = Color(0xFF4ADE80)
private val ColorAccentDim  = Color(0xFF166534)
private val ColorText       = Color(0xFFF0FDF4)
private val ColorTextMuted  = Color(0xFF86EFAC)
private val ColorError      = Color(0xFFFF6B6B)
private val ColorAmber      = Color(0xFFFBBF24)
private val ColorBlue       = Color(0xFF60A5FA)
private val ColorPink       = Color(0xFFF472B6)

// ─── Screen ───────────────────────────────────────────────────────────────────

@Composable
fun TripDetailsScreen(
    viewModel: SearchRideViewModel,
    onBackClick: () -> Unit,
    onBookingSuccess: () -> Unit
) {
    val tripDetailsState by viewModel.tripDetailsUiState.collectAsStateWithLifecycle()
    val bookingState     by viewModel.bookingUiState.collectAsStateWithLifecycle()
    val searchUiState    by viewModel.uiState.collectAsStateWithLifecycle()

    // Fetch co-passengers from MyTripsViewModel
    val myTripsViewModel: MyTripsViewModel = koinViewModel()
    val myTripsUiState by remember { derivedStateOf { myTripsViewModel.uiState } }

    LaunchedEffect(bookingState) {
        if (bookingState is BookingUiState.Success) {
            viewModel.resetBookingState()
            onBookingSuccess()
        }
    }
    LaunchedEffect(Unit) {
        viewModel.getTripDetails()
        myTripsViewModel.loadBookings(null)   // load passenger's bookings to get coPassengers
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBackground)
    ) {
        when (val state = tripDetailsState) {
            is TripDetailsUiState.Initial -> {}
            is TripDetailsUiState.Loading -> LoadingState()
            is TripDetailsUiState.Success -> {
                // Find the matching booking for this trip (if any) to extract coPassengers
                val matchingBooking = myTripsUiState.bookings
                    .firstOrNull { it.tripStart == state.tripDetails.startPointName
                            || it.pickupPointName == state.tripDetails.startPointName }
                val coPassengers = matchingBooking?.coPassengers ?: emptyList()

                TripDetailsContent(
                    tripDetails  = state.tripDetails,
                    bookingState = bookingState,
                    initialSeats = searchUiState.peopleCount,
                    coPassengers = coPassengers,
                    onBackClick  = onBackClick,
                    onBookRide   = { details, seats, notes -> viewModel.bookTrip(details, seats, notes) }
                )
            }
            is TripDetailsUiState.Error -> ErrorState(
                message = state.message,
                onRetry = { viewModel.getTripDetails() },
                onBack  = onBackClick
            )
        }
    }
}

// ─── Main Content ─────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TripDetailsContent(
    tripDetails: TripDetails,
    bookingState: BookingUiState,
    initialSeats: Int,
    coPassengers: List<String>,
    onBackClick: () -> Unit,
    onBookRide: (TripDetails, Int, String) -> Unit
) {
    var isMapExpanded    by remember { mutableStateOf(true) }
    var showBookingSheet by remember { mutableStateOf(false) }

    if (showBookingSheet) {
        BookingBottomSheet(
            tripDetails = tripDetails,
            bookingState = bookingState,
            initialSeats = initialSeats,
            onConfirm = { seats, notes -> onBookRide(tripDetails, seats, notes) },
            onDismiss = { if (bookingState !is BookingUiState.Loading) showBookingSheet = false }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Map ──
            AnimatedVisibility(
                visible = isMapExpanded,
                enter = expandVertically() + fadeIn(),
                exit  = shrinkVertically() + fadeOut()
            ) {
                Box(modifier = Modifier.fillMaxWidth().height(280.dp)) {
                    TripRouteMapView(
                        modifier          = Modifier.fillMaxSize(),
                        tripPolyline      = tripDetails.selectedPolyline,
                        passengerStart    = tripDetails.startCoordinate,
                        passengerEnd      = tripDetails.endCoordinate,
                        highlightColor    = ColorAccent
                    )
                    // Back button
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(ColorCard.copy(alpha = 0.9f))
                            .align(Alignment.TopStart),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, null, tint = ColorText, modifier = Modifier.size(20.dp))
                        }
                    }
                    // Collapse map
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(ColorCard.copy(alpha = 0.9f))
                            .align(Alignment.TopEnd),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = { isMapExpanded = false }) {
                            Icon(Icons.Default.ExpandMore, null, tint = ColorText, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }

            // ── Scrollable content ──
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 100.dp)
            ) {
                // Back + Map-expand bar when map is collapsed
                if (!isMapExpanded) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, null, tint = ColorText)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(ColorCard)
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.noRippleClickable { isMapExpanded = true }
                            ) {
                                Icon(Icons.Outlined.Map, null, tint = ColorAccent, modifier = Modifier.size(16.dp))
                                Text("View route", color = ColorAccent, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Icon(Icons.Default.ExpandLess, null, tint = ColorAccent, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                Spacer(Modifier.height(if (isMapExpanded) 0.dp else 0.dp))

                // ── Section: Header ──
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                    Text("Trip Details", color = ColorText, fontSize = 24.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.3).sp)
                    Text("Review before booking", color = ColorTextMuted, fontSize = 14.sp)
                }

                RouteInfoCard(tripDetails)
                Spacer(Modifier.height(12.dp))
                DriverInfoCard(tripDetails.user, tripDetails.carDetails)
                Spacer(Modifier.height(12.dp))
                TripMetricsCard(tripDetails)
                Spacer(Modifier.height(12.dp))
                PricingCard(tripDetails)
                Spacer(Modifier.height(12.dp))

                // ── Co-passengers section (only if list is non-empty) ──
                if (coPassengers.isNotEmpty()) {
                    CoPassengersCard(coPassengers = coPassengers)
                    Spacer(Modifier.height(12.dp))
                }

                PreferencesCard(tripDetails)
            }
        }

        // ── Floating Book Button ──
        BookRideButton(
            tripDetails = tripDetails,
            isLoading   = bookingState is BookingUiState.Loading,
            onClick     = { showBookingSheet = true },
            modifier    = Modifier
                .align(Alignment.BottomCenter)
                .padding(20.dp)
        )
    }
}

// ─── Co-Passengers Card ───────────────────────────────────────────────────────

@Composable
private fun CoPassengersCard(coPassengers: List<String>) {
    GreenCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Group,
                    contentDescription = null,
                    tint = ColorAccent,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Co-Passengers",
                    color = ColorText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                // Count badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(ColorAccent.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${coPassengers.size}",
                        color = ColorAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(ColorBorder))

            // Passenger rows
            coPassengers.forEachIndexed { index, name ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Avatar with initial
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(
                                    listOf(ColorAccentDim, ColorAccent.copy(alpha = 0.4f))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = name.firstOrNull()?.uppercase() ?: "?",
                            color = ColorText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column {
                        Text(
                            text = name,
                            color = ColorText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Passenger ${index + 1}",
                            color = ColorTextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                // Divider between passengers (not after the last one)
                if (index < coPassengers.lastIndex) {
                    Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(ColorBorder))
                }
            }
        }
    }
}

// ─── Route Info ───────────────────────────────────────────────────────────────

@Composable
private fun RouteInfoCard(tripDetails: TripDetails) {
    GreenCard {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                InfoPill(icon = Icons.Outlined.CalendarToday, text = tripDetails.tripDate, color = ColorAccent)
                InfoPill(icon = Icons.Outlined.Schedule,      text = tripDetails.tripTime, color = ColorBlue)
            }
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(ColorBorder))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(ColorAccent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.TripOrigin, null, tint = ColorAccent, modifier = Modifier.size(18.dp))
                }
                Column {
                    Text("Pickup", color = ColorTextMuted, fontSize = 11.sp, letterSpacing = 0.5.sp)
                    Text(tripDetails.startPointName, color = ColorText, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            // Dashed line
            Box(
                modifier = Modifier
                    .padding(start = 17.dp)
                    .width(2.dp)
                    .height(20.dp)
                    .background(
                        brush = Brush.verticalGradient(listOf(ColorAccent, ColorAmber)),
                        shape = RoundedCornerShape(1.dp)
                    )
            )

            // Destination
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(ColorAmber.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.LocationOn, null, tint = ColorAmber, modifier = Modifier.size(18.dp))
                }
                Column {
                    Text("Drop-off", color = ColorTextMuted, fontSize = 11.sp, letterSpacing = 0.5.sp)
                    Text(tripDetails.endPointName, color = ColorText, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// ─── Driver Info ──────────────────────────────────────────────────────────────

@Composable
private fun DriverInfoCard(user: TripUser, carDetails: CarDetails) {
    GreenCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar circle with initial
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(listOf(ColorAccentDim, ColorAccent.copy(alpha = 0.5f)))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.name.first().uppercase(),
                    color = ColorText,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(user.name, color = ColorText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    if (user.isVerified) {
                        Icon(Icons.Default.Verified, null, tint = ColorAccent, modifier = Modifier.size(16.dp))
                    }
                }

                // Rating
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Default.Star, null, tint = ColorAmber, modifier = Modifier.size(14.dp))
                    Text(
                        (round(user.rating * 10) / 10).toString(),
                        color = ColorAmber,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Car info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Outlined.DirectionsCar, null, tint = ColorTextMuted, modifier = Modifier.size(14.dp))
                    Text(
                        "${carDetails.model} • ${carDetails.color}",
                        color = ColorTextMuted,
                        fontSize = 12.sp
                    )
                }
                Text(carDetails.licensePlate, color = ColorBorder, fontSize = 11.sp, letterSpacing = 0.5.sp)
            }
        }
    }
}

// ─── Trip Metrics ─────────────────────────────────────────────────────────────

@Composable
private fun TripMetricsCard(tripDetails: TripDetails) {
    GreenCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MetricItem(Icons.Outlined.Route,     "${round(tripDetails.totalDistanceKm * 10) / 10} km", "Distance", ColorAccent)
            Box(modifier = Modifier.width(1.dp).height(40.dp).background(ColorBorder))
            MetricItem(Icons.Outlined.Timer,     formatDuration(tripDetails.estimatedDurationSeconds), "Duration", ColorBlue)
            Box(modifier = Modifier.width(1.dp).height(40.dp).background(ColorBorder))
            MetricItem(Icons.Outlined.EventSeat, "${tripDetails.availableSeats - tripDetails.bookedSeats}/${tripDetails.availableSeats}", "Seats", ColorAmber)
        }
    }
}

@Composable
private fun MetricItem(icon: ImageVector, value: String, label: String, tint: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(22.dp))
        Text(value, color = ColorText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        Text(label, color = ColorTextMuted, fontSize = 11.sp)
    }
}

// ─── Pricing ──────────────────────────────────────────────────────────────────

@Composable
private fun PricingCard(tripDetails: TripDetails) {
    GreenCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Estimated Pricing", color = ColorText, fontSize = 15.sp, fontWeight = FontWeight.Bold)

            val diffPercent = if (tripDetails.recommendedCost != null && tripDetails.recommendedCost > 0) {
                (abs(tripDetails.costPerSeat - tripDetails.recommendedCost) / tripDetails.recommendedCost) * 100
            } else 0.0

            val priceColor = when {
                tripDetails.recommendedCost == null -> ColorAccent
                diffPercent <= 20.0 -> ColorAccent
                diffPercent <= 50.0 -> ColorError
                else -> ColorError
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        "₹${tripDetails.costPerSeat}",
                        color = priceColor,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        "PER SEAT",
                        color = priceColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(ColorAccentDim.copy(alpha = 0.5f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        "₹${round(tripDetails.costPerKm * 100) / 100}/km",
                        color = ColorTextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

// ─── Preferences ─────────────────────────────────────────────────────────────

@Composable
private fun PreferencesCard(tripDetails: TripDetails) {
    GreenCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Trip Preferences", color = ColorText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            PreferenceRow(Icons.Outlined.AcUnit,      "AC available",    ColorBlue)
            if (tripDetails.womanAccompany)
                PreferenceRow(Icons.Default.Female,   "Woman co-passenger",      ColorPink)
            if (tripDetails.user.isVerified)
                PreferenceRow(Icons.Default.VerifiedUser, "Verified driver", ColorAccent)
            if (!tripDetails.miscMessage.isNullOrBlank())
                PreferenceRow(Icons.Outlined.Notes, tripDetails.miscMessage, ColorTextMuted)
        }
    }
}

@Composable
private fun PreferenceRow(icon: ImageVector, text: String, tint: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(tint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = tint, modifier = Modifier.size(16.dp))
        }
        Text(text, color = ColorText, fontSize = 14.sp)
    }
}

// ─── Book Button ──────────────────────────────────────────────────────────────

@Composable
private fun BookRideButton(
    tripDetails: TripDetails,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isAvailable = tripDetails.availableSeats > tripDetails.bookedSeats

    Button(
        onClick = onClick,
        enabled = isAvailable && !isLoading,
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = if (isAvailable && !isLoading)
                        Brush.horizontalGradient(listOf(ColorAccent, Color(0xFF22C55E)))
                    else Brush.horizontalGradient(listOf(ColorBorder, ColorBorder)),
                    shape = RoundedCornerShape(18.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color(0xFF052E16), modifier = Modifier.size(24.dp), strokeWidth = 2.5.dp)
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        if (isAvailable) Icons.Default.CheckCircle else Icons.Default.Block,
                        null,
                        tint = Color(0xFF052E16),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        if (isAvailable) "Book Ride  •  ₹${tripDetails.costPerSeat}/seat" else "Fully Booked",
                        color = Color(0xFF052E16),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ─── Booking Bottom Sheet ─────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookingBottomSheet(
    tripDetails: TripDetails,
    bookingState: BookingUiState,
    initialSeats: Int,
    onConfirm: (Int, String) -> Unit,
    onDismiss: () -> Unit
) {
    val availableSeats = tripDetails.availableSeats - tripDetails.bookedSeats
    var seatsRequested by remember { mutableIntStateOf(initialSeats.coerceAtMost(availableSeats).coerceAtLeast(1)) }
    var passengerNotes by remember { mutableStateOf("") }
    val totalCost      = round(tripDetails.costPerSeat * seatsRequested * 100.0) / 100.0
    val isLoading      = bookingState is BookingUiState.Loading
    val isError        = bookingState is BookingUiState.Error

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = ColorSurface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 14.dp, bottom = 10.dp)
                    .size(width = 40.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(ColorBorder)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .padding(bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text(
                "Confirm Booking",
                color = ColorText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            // Summary card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(ColorCard)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SummaryRow("From",   tripDetails.startPointName)
                SummaryRow("To",     tripDetails.endPointName)
                SummaryRow("Date",   "${tripDetails.tripDate}  •  ${tripDetails.tripTime}")
                SummaryRow("Driver", tripDetails.user.name)
                SummaryRow("Price",  "₹${tripDetails.costPerSeat} / seat")
            }

            // Seats selector
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Seats", color = ColorText, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text("$availableSeats available", color = ColorTextMuted, fontSize = 12.sp)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        SheetIconBtn(
                            icon = Icons.Default.Remove,
                            enabled = seatsRequested > 1 && !isLoading,
                            onClick = { seatsRequested-- }
                        )
                        Text(
                            seatsRequested.toString(),
                            color = ColorText,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.widthIn(min = 24.dp),
                            textAlign = TextAlign.Center
                        )
                        SheetIconBtn(
                            icon = Icons.Default.Add,
                            enabled = seatsRequested < availableSeats && !isLoading,
                            onClick = { seatsRequested++ }
                        )
                    }
                }
            }

            // Notes
            OutlinedTextField(
                value = passengerNotes,
                onValueChange = { passengerNotes = it },
                label = { Text("Note for driver (optional)", fontSize = 13.sp) },
                placeholder = { Text("e.g. I'll be at the main entrance", color = ColorTextMuted, fontSize = 13.sp) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 3,
                enabled = !isLoading,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor      = ColorAccent,
                    unfocusedBorderColor    = ColorBorder,
                    focusedLabelColor       = ColorAccent,
                    unfocusedLabelColor     = ColorTextMuted,
                    focusedTextColor        = ColorText,
                    unfocusedTextColor      = ColorText,
                    cursorColor             = ColorAccent,
                    focusedContainerColor   = ColorCard,
                    unfocusedContainerColor = ColorCard
                )
            )

            // Error
            AnimatedVisibility(visible = isError) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ColorError.copy(alpha = 0.12f))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Outlined.ErrorOutline, null, tint = ColorError, modifier = Modifier.size(16.dp))
                    Text(
                        (bookingState as? BookingUiState.Error)?.message ?: "",
                        color = ColorError,
                        fontSize = 13.sp
                    )
                }
            }

            // Total + Confirm
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total", color = ColorTextMuted, fontSize = 12.sp)
                    Text("₹$totalCost", color = ColorAccent, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                }

                Button(
                    onClick = { onConfirm(seatsRequested, passengerNotes) },
                    enabled = !isLoading,
                    modifier = Modifier.height(52.dp).widthIn(min = 150.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .widthIn(min = 150.dp)
                            .background(
                                brush = if (!isLoading)
                                    Brush.horizontalGradient(listOf(ColorAccent, Color(0xFF22C55E)))
                                else Brush.horizontalGradient(listOf(ColorBorder, ColorBorder)),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(
                                    color = Color(0xFF052E16),
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp
                                )
                                Text("Booking…", color = Color(0xFF052E16), fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF052E16), modifier = Modifier.size(18.dp))
                                Text("Confirm", color = Color(0xFF052E16), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = ColorTextMuted, fontSize = 12.sp, modifier = Modifier.weight(0.3f))
        Text(value, color = ColorText, fontSize = 12.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(0.7f), textAlign = TextAlign.End)
    }
}

@Composable
private fun SheetIconBtn(icon: ImageVector, enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (enabled) ColorAccentDim else ColorBorder)
            .noRippleClickable(enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, null, tint = if (enabled) ColorAccent else ColorTextMuted, modifier = Modifier.size(16.dp))
    }
}


@Composable
private fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(ColorCard)
                .padding(40.dp)
        ) {
            CircularProgressIndicator(color = ColorAccent, strokeWidth = 3.dp)
            Text("Loading trip details…", color = ColorText, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit, onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(ColorError.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.ErrorOutline, null, tint = ColorError, modifier = Modifier.size(32.dp))
            }
            Text("Something went wrong", color = ColorText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(message, color = ColorTextMuted, fontSize = 14.sp, textAlign = TextAlign.Center)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = onBack,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, ColorBorder),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ColorTextMuted)
                ) { Text("Go back") }

                Button(
                    onClick = onRetry,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ColorAccent)
                ) {
                    Icon(Icons.Default.Refresh, null, tint = Color(0xFF052E16), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Retry", color = Color(0xFF052E16), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ─── Shared helpers ───────────────────────────────────────────────────────────

@Composable
private fun GreenCard(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(ColorSurface)
    ) { content() }
}

@Composable
private fun InfoPill(icon: ImageVector, text: String, color: Color) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(icon, null, tint = color, modifier = Modifier.size(14.dp))
        Text(text, color = color, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

// Extension to make a Box clickable without ripple (for small interactive areas)
@Composable
private fun Modifier.noRippleClickable(enabled: Boolean = true, onClick: () -> Unit): Modifier =
    this.then(
        if (enabled) Modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() },
            onClick = onClick
        ) else Modifier
    )

private fun formatDuration(seconds: Int): String {
    val hours   = seconds / 3600
    val minutes = (seconds % 3600) / 60
    return if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
}