package com.state.greenmiles.com_state_greenmiles.presentation.screens.tripScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.state.greenmiles.com_state_greenmiles.domain.model.booking.Booking
import com.state.greenmiles.com_state_greenmiles.domain.model.trip.Trips
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.material.icons.outlined.Chat

// ─── Design Tokens ────────────────────────────────────────────────────────────

private val ColorBackground = Color(0xFF0D1A12)
private val ColorSurface    = Color(0xFF142B1C)
private val ColorCard       = Color(0xFF1C3826)
private val ColorBorder     = Color(0xFF2E5C3A)
private val ColorAccent     = Color(0xFF4ADE80)
private val ColorText       = Color(0xFFF0FDF4)
private val ColorTextMuted  = Color(0xFF86EFAC)
private val ColorError      = Color(0xFFF87171)
private val ColorAmber      = Color(0xFFFBBF24)

// ─── Helpers ──────────────────────────────────────────────────────────────────

private fun statusColor(status: String) = when (status.lowercase()) {
    "confirmed", "accepted", "active", "approved" -> Color(0xFF4ADE80)
    "completed"                                    -> Color(0xFF60A5FA)
    "cancelled", "rejected"                        -> Color(0xFFF87171)
    else                                           -> Color(0xFFFBBF24)
}

private fun statusLabel(status: String) = status.replaceFirstChar { it.uppercase() }

// ─── Tab ──────────────────────────────────────────────────────────────────────

private enum class ScreenTab(val label: String) {
    BOOKINGS("My Bookings"),
    TRIPS("My Created Trips")
}

// ─── Screen ───────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTripsScreen(onBack: () -> Unit) {
    val viewModel: MyTripsViewModel = koinViewModel()
    val uiState by remember { derivedStateOf { viewModel.uiState } }
    var selectedTab by remember { mutableStateOf(ScreenTab.BOOKINGS) }

    // When non-null, show TripBookingsScreen for that trip
    var selectedTripForBookings by remember { mutableStateOf<Trips?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadBookings(null)
        viewModel.loadMyTrips()
    }

    // ── Navigate to trip bookings screen ──
    selectedTripForBookings?.let { trip ->
        TripBookingsScreen(
            trip      = trip,
            viewModel = viewModel,
            onBack    = { selectedTripForBookings = null }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBackground)
    ) {
        // ── Top Bar ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 52.dp, bottom = 16.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ColorCard)
            ) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = "Back", tint = ColorAccent)
            }
            Text(
                text = "My Trips",
                color = ColorText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // ── Two Tabs ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(ColorSurface)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ScreenTab.entries.forEach { tab ->
                val isSelected = selectedTab == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected)
                                Brush.linearGradient(listOf(ColorAccent, Color(0xFF22C55E)))
                            else
                                Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { selectedTab = tab }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab.label,
                        color = if (isSelected) Color(0xFF052E16) else ColorTextMuted,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Content ──
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = {
                when (selectedTab) {
                    ScreenTab.BOOKINGS -> viewModel.loadBookings(null)
                    ScreenTab.TRIPS    -> viewModel.loadMyTrips()
                }
            },
            modifier = Modifier.fillMaxSize()
        ) {
            when {
                uiState.isLoading -> LoadingState()
                uiState.error != null -> ErrorState(
                    message = uiState.error!!,
                    onRetry = {
                        when (selectedTab) {
                            ScreenTab.BOOKINGS -> viewModel.loadBookings(null)
                            ScreenTab.TRIPS    -> viewModel.loadMyTrips()
                        }
                    }
                )
                else -> when (selectedTab) {
                    ScreenTab.BOOKINGS -> {
                        if (uiState.bookings.isEmpty())
                            EmptyState("No bookings yet", Icons.Outlined.BookmarkBorder)
                        else
                            BookingsList(bookings = uiState.bookings, onCancel = { viewModel.cancelBooking(it) })
                    }
                    ScreenTab.TRIPS -> {
                        if (uiState.trips.isEmpty())
                            EmptyState("No created trips yet", Icons.Outlined.DirectionsCar)
                        else
                            TripsList(
                                trips = uiState.trips,
                                onPendingClick = { trip -> selectedTripForBookings = trip }
                            )
                    }
                }
            }
        }
    }
}

// ─── Trip Bookings Screen ─────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TripBookingsScreen(
    trip: Trips,
    viewModel: MyTripsViewModel,
    onBack: () -> Unit
) {
    val uiState by remember { derivedStateOf { viewModel.uiState } }

    LaunchedEffect(trip.id) {
        viewModel.loadTripBookings(trip.id)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBackground)
    ) {
        // ── Top Bar ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 52.dp, bottom = 8.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ColorCard)
            ) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = "Back", tint = ColorAccent)
            }
            Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Booking Requests", color = ColorText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = "${trip.startPointName} → ${trip.endPointName}",
                    color = ColorTextMuted,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // ── Trip summary pill row ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SummaryPill(icon = Icons.Outlined.CalendarToday, text = trip.tripDate)
            SummaryPill(icon = Icons.Outlined.AccessTime,    text = trip.tripTime)
            SummaryPill(
                icon = Icons.Outlined.HourglassTop,
                text = "${trip.pendingBookings} pending",
                color = ColorAmber
            )
            SummaryPill(
                icon = Icons.Outlined.CheckCircle,
                text = "${trip.approvedBookings} approved",
                color = ColorAccent
            )
        }

        Spacer(Modifier.height(8.dp))

        // ── Bookings content ──
        when {
            uiState.isLoading -> LoadingState()
            uiState.error != null -> ErrorState(
                message = uiState.error!!,
                onRetry = { viewModel.loadTripBookings(trip.id) }
            )
            uiState.bookings.isEmpty() -> EmptyState(
                message = "No booking requests yet",
                icon = Icons.Outlined.PersonOff
            )
            else -> LazyColumn(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.bookings, key = { it.id }) { booking ->
                    PassengerBookingCard(
                        booking  = booking,
                        onAccept = { viewModel.acceptBooking(booking.id) },
                        onReject = { viewModel.rejectBooking(booking.id) }
                    )
                }
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}

// ─── Passenger Booking Card (for driver to accept/reject) ─────────────────────

@Composable
private fun PassengerBookingCard(
    booking: Booking,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    val sColor = statusColor(booking.status)
    val isPending = booking.status.lowercase() == "pending"


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(ColorCard)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ── Passenger avatar + name + status ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Avatar with initial
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                listOf(Color(0xFF166534), ColorAccent.copy(alpha = 0.5f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (booking.tripOwnerName ?: "P").take(1).uppercase(),
                        color = ColorText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column {
                    Text(
                        text = booking.tripOwnerName ?: "Passenger",
                        color = ColorText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (!booking.tripOwnerMobile.isNullOrBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Outlined.Phone, contentDescription = null, tint = ColorTextMuted, modifier = Modifier.size(12.dp))
                            Text(booking.tripOwnerMobile, color = ColorTextMuted, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Status pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(sColor.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(statusLabel(booking.status), color = sColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        HorizontalDivider(color = ColorBorder, thickness = 0.5.dp)

        // ── Pickup → Drop ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Icon(Icons.Outlined.RadioButtonChecked, contentDescription = null, tint = ColorAccent, modifier = Modifier.size(13.dp))
                repeat(3) { Box(modifier = Modifier.size(width = 1.5.dp, height = 4.dp).background(ColorBorder)) }
                Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = ColorError, modifier = Modifier.size(13.dp))
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.weight(1f)) {
                Text(booking.pickupPointName, color = ColorText, fontSize = 13.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(booking.dropPointName,   color = ColorText, fontSize = 13.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }

            // Seats + Cost
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("₹${booking.totalCost}", color = ColorAccent, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text("${booking.seatsRequested} seat${if (booking.seatsRequested > 1) "s" else ""}", color = ColorTextMuted, fontSize = 12.sp)
            }
        }

        // ── Accept / Reject buttons (only for pending) ──
        if (isPending) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reject button (Minimal Icon)
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(ColorError.copy(alpha = 0.12f))
                        .clickable { onReject() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.Close,
                        contentDescription = "Reject",
                        tint = ColorError,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Approve button (Prominent Full-Width)
                Button(
                    onClick = onAccept,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp),
                    elevation = null
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.horizontalGradient(listOf(ColorAccent, Color(0xFF22C55E))),
                                shape = RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Approve",
                            color = Color(0xFF052E16),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// ─── Summary Pill ─────────────────────────────────────────────────────────────

@Composable
private fun SummaryPill(
    icon: ImageVector,
    text: String,
    color: Color = ColorTextMuted
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(12.dp))
        Text(text, color = color, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

// ─── Bookings List ────────────────────────────────────────────────────────────

@Composable
private fun BookingsList(bookings: List<Booking>, onCancel: (String) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(bookings, key = { it.id }) { booking ->
            BookingCard(booking = booking, onCancel = onCancel)
        }
        item { Spacer(Modifier.height(16.dp)) }
    }
}

// ─── Booking Card ─────────────────────────────────────────────────────────────

@Composable
private fun BookingCard(booking: Booking, onCancel: (String) -> Unit) {
    val sColor = statusColor(booking.status)
    val cancellable = booking.status.lowercase() in listOf("pending", "confirmed")
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(ColorCard)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(sColor.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    statusLabel(booking.status),
                    color = sColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // WhatsApp / chat icon when approved
            if (booking.status.lowercase() == "approved") {

                IconButton(
                    onClick = {
                        val phone = booking.tripOwnerMobile?.replace("+", "")
                        uriHandler.openUri("https://wa.me/$phone")
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Chat,
                        contentDescription = "Chat with driver",
                        tint = Color(0xFF25D366),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Text(
            "₹${booking.totalCost}",
            color = ColorAccent,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Icon(Icons.Outlined.RadioButtonChecked, contentDescription = null, tint = ColorAccent, modifier = Modifier.size(14.dp))
                repeat(3) { Box(modifier = Modifier.size(width = 1.5.dp, height = 5.dp).background(ColorBorder)) }
                Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = ColorError, modifier = Modifier.size(14.dp))
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                Text(booking.pickupPointName, color = ColorText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(booking.dropPointName,   color = ColorText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }

        HorizontalDivider(color = ColorBorder, thickness = 0.5.dp)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InfoChip(Icons.Outlined.AccessTime, booking.tripStart)
            InfoChip(Icons.Outlined.Flag,       booking.tripEnd)
            InfoChip(Icons.Outlined.EventSeat,  "${booking.seatsRequested} seat${if (booking.seatsRequested > 1) "s" else ""}")
        }

        if (!booking.tripOwnerName.isNullOrBlank()) {
            HorizontalDivider(color = ColorBorder, thickness = 0.5.dp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(ColorSurface), contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.Person, contentDescription = null, tint = ColorAccent, modifier = Modifier.size(16.dp))
                    }
                    Column {
                        Text("Driver", color = ColorTextMuted, fontSize = 11.sp)
                        Text(booking.tripOwnerName, color = ColorText, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                }
                if (!booking.tripOwnerMobile.isNullOrBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Outlined.Phone, contentDescription = null, tint = ColorTextMuted, modifier = Modifier.size(13.dp))
                        Text(booking.tripOwnerMobile, color = ColorTextMuted, fontSize = 12.sp)
                    }
                }
            }
        }

        if (cancellable) {
            Button(
                onClick = { onCancel(booking.id) },
                modifier = Modifier.fillMaxWidth().height(40.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ColorError.copy(alpha = 0.12f)),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("Cancel Booking", color = ColorError, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// ─── Trips List ───────────────────────────────────────────────────────────────

@Composable
private fun TripsList(trips: List<Trips>, onPendingClick: (Trips) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(trips, key = { it.id }) { trip ->
            TripCard(trip = trip, onPendingClick = { onPendingClick(trip) })
        }
        item { Spacer(Modifier.height(16.dp)) }
    }
}

// ─── Trip Card ────────────────────────────────────────────────────────────────

@Composable
private fun TripCard(trip: Trips, onPendingClick: () -> Unit) {
    val sColor = statusColor(trip.status)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(ColorCard)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(sColor.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(statusLabel(trip.status), color = sColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            Text("₹${trip.costPerSeat}/seat", color = ColorAccent, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Icon(Icons.Outlined.RadioButtonChecked, contentDescription = null, tint = ColorAccent, modifier = Modifier.size(14.dp))
                repeat(3) { Box(modifier = Modifier.size(width = 1.5.dp, height = 5.dp).background(ColorBorder)) }
                Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = ColorError, modifier = Modifier.size(14.dp))
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                Text(trip.startPointName, color = ColorText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(trip.endPointName,   color = ColorText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }

        HorizontalDivider(color = ColorBorder, thickness = 0.5.dp)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InfoChip(Icons.Outlined.CalendarToday, trip.tripDate)
            InfoChip(Icons.Outlined.AccessTime,    trip.tripTime)
            InfoChip(Icons.Outlined.Route,         "${trip.totalDistanceKm} km")
        }

        HorizontalDivider(color = ColorBorder, thickness = 0.5.dp)

        // ── Seats + Bookings row — pending is clickable ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InfoChip(Icons.Outlined.EventSeat, "${trip.availableSeats} seats left")

            // Clickable pending badge
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (trip.pendingBookings > 0)
                            ColorAmber.copy(alpha = 0.15f)
                        else
                            ColorBorder.copy(alpha = 0.3f)
                    )
                    .clickable(
                        enabled = trip.pendingBookings > 0,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onPendingClick
                    )
                    .padding(horizontal = 8.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(Icons.Outlined.HourglassTop, contentDescription = null, tint = if (trip.pendingBookings > 0) ColorAmber else ColorTextMuted, modifier = Modifier.size(13.dp))
                Text(
                    text = "${trip.pendingBookings} pending",
                    color = if (trip.pendingBookings > 0) ColorAmber else ColorTextMuted,
                    fontSize = 12.sp,
                    fontWeight = if (trip.pendingBookings > 0) FontWeight.SemiBold else FontWeight.Normal
                )
                if (trip.pendingBookings > 0) {
                    Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = ColorAmber, modifier = Modifier.size(13.dp))
                }
            }

            InfoChip(Icons.Outlined.CheckCircle, "${trip.approvedBookings} approved")
        }
    }
}

// ─── Shared Components ────────────────────────────────────────────────────────

@Composable
private fun InfoChip(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(icon, contentDescription = null, tint = ColorTextMuted, modifier = Modifier.size(13.dp))
        Text(text, color = ColorTextMuted, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = ColorAccent, strokeWidth = 2.dp)
    }
}

@Composable
private fun EmptyState(message: String, icon: ImageVector) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(icon, contentDescription = null, tint = ColorBorder, modifier = Modifier.size(56.dp))
            Text(message, color = ColorTextMuted, fontSize = 15.sp)
        }
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(Icons.Outlined.ErrorOutline, contentDescription = null, tint = ColorError, modifier = Modifier.size(48.dp))
            Text(message, color = ColorTextMuted, fontSize = 14.sp)
            Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = ColorCard), shape = RoundedCornerShape(12.dp)) {
                Text("Retry", color = ColorAccent)
            }
        }
    }
}