package com.state.greenmiles.com_state_greenmiles.presentation.screens.car_pool__search_ride

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.state.greenmiles.com_state_greenmiles.domain.model.trip.Trip
import com.state.greenmiles.com_state_greenmiles.presentation.common.components.LocationField
import com.state.greenmiles.com_state_greenmiles.presentation.common.components.SmallIconBtn
import com.state.greenmiles.com_state_greenmiles.presentation.common.models.ValidationState
import com.state.greenmiles.com_state_greenmiles.presentation.screens.googlemap.PlacePrediction
import com.state.greenmiles.com_state_greenmiles.presentation.screens.googlemap.rememberPlacesClient
import com_state_greenmiles.composeapp.generated.resources.Res
import com_state_greenmiles.composeapp.generated.resources.man
import com_state_greenmiles.composeapp.generated.resources.woman
import kotlinx.coroutines.flow.collectLatest
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import kotlin.time.Clock as StdClock

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

// ─── Screen ───────────────────────────────────────────────────────────────────

@Composable
fun SearchRide(
    viewModel: SearchRideViewModel,
    onNavigateToTripDetails: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is SearchRideEffect.ShowToast -> snackbarHostState.showSnackbar(effect.message)
                is SearchRideEffect.NavigateToTripDetails -> {
                    viewModel.selectTrip(id = effect.tripId)
                    onNavigateToTripDetails(effect.tripId)
                }
                SearchRideEffect.ScrollToTop -> listState.animateScrollToItem(0)
                is SearchRideEffect.OpenMap -> {}
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBackground)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = {
                SnackbarHost(snackbarHostState) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = ColorCard,
                        contentColor = ColorText,
                        actionColor = ColorAccent,
                        shape = RoundedCornerShape(14.dp)
                    )
                }
            },
            topBar = {
                CompactSearchForm(uiState = uiState, onEvent = viewModel::onEvent)
            }
        ) { paddingValues ->
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (uiState.searchResults.isNotEmpty()) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(ColorAccentDim)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${uiState.searchResults.size} rides found",
                                    color = ColorAccent,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    items(items = uiState.searchResults, key = { it.id }) { trip ->
                        CompactTripCard(
                            trip = trip,
                            onClick = {
                                viewModel.selectTrip(trip.id)
                                viewModel.onEvent(SearchRideEvent.SelectTrip(trip.id))
                            }
                        )
                    }

                    if (uiState.hasMoreResults) {
                        item {
                            TextButton(
                                onClick = { viewModel.onEvent(SearchRideEvent.LoadMoreTrips) },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !uiState.isLoadingMore
                            ) {
                                if (uiState.isLoadingMore) {
                                    CircularProgressIndicator(
                                        color = ColorAccent,
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(Modifier.width(8.dp))
                                }
                                Text("Load more", color = ColorTextMuted, fontSize = 14.sp)
                            }
                        }
                    }
                } else if (!uiState.isSearching && uiState.error == null && uiState.canSearch) {
                    item { EmptyState() }
                }

                if (uiState.error != null) {
                    item {
                        ErrorBanner(
                            error = uiState.error ?: "",
                            onRetry = { viewModel.onEvent(SearchRideEvent.RetrySearch) },
                            onDismiss = { viewModel.onEvent(SearchRideEvent.ClearError) }
                        )
                    }
                }
            }

            // Searching overlay
            AnimatedVisibility(
                visible = uiState.isSearching,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ColorBackground.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(ColorCard)
                            .padding(32.dp)
                    ) {
                        CircularProgressIndicator(color = ColorAccent, strokeWidth = 3.dp)
                        Text("Finding rides…", color = ColorText, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }

    if (uiState.showDatePicker) {
        DatePickerDialog(
            onDateSelected = { viewModel.onEvent(SearchRideEvent.UpdateTripDate(it)) },
            onDismiss = { viewModel.onEvent(SearchRideEvent.HideDatePicker) }
        )
    }
}

// ─── Search Form (TopBar) ─────────────────────────────────────────────────────

@Composable
fun CompactSearchForm(
    uiState: SearchRideUiState,
    onEvent: (SearchRideEvent) -> Unit
) {
    val placesClient = rememberPlacesClient()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorBackground)
            .padding(start = 20.dp, end = 20.dp, top = 52.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        Text(
            text = "Find a Ride",
            color = ColorText,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.3).sp
        )

        // Location card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(ColorSurface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box {
                // Connecting line
                Box(
                    modifier = Modifier
                        .offset(x = 11.dp, y = 40.dp)
                        .width(2.dp)
                        .height(36.dp)
                        .background(ColorBorder, RoundedCornerShape(1.dp))
                )
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    LocationField(
                        label = "From — current location",
                        query = uiState.startPlaceQuery,
                        predictions = uiState.startPlacePredictions,
                        showPredictions = uiState.showStartPredictions,
                        isSearching = uiState.isSearchingStartPlaces,
                        validationState = uiState.validation.startLocationValid,
                        onQueryChange = { onEvent(SearchRideEvent.UpdateStartQuery(it, placesClient)) },
                        onPredictionClick = { onEvent(SearchRideEvent.SelectStartPlace(it, placesClient)) },
                        onClear = { onEvent(SearchRideEvent.ClearStartLocation) },
                        icon = Icons.Default.MyLocation,
                        accentColor = ColorAccent
                    )
                    LocationField(
                        label = "To — destination",
                        query = uiState.endPlaceQuery,
                        predictions = uiState.endPlacePredictions,
                        showPredictions = uiState.showEndPredictions,
                        isSearching = uiState.isSearchingEndPlaces,
                        validationState = uiState.validation.endLocationValid,
                        onQueryChange = { onEvent(SearchRideEvent.UpdateEndQuery(it, placesClient)) },
                        onPredictionClick = { onEvent(SearchRideEvent.SelectEndPlace(it, placesClient)) },
                        onClear = { onEvent(SearchRideEvent.ClearEndLocation) },
                        icon = Icons.Default.LocationOn,
                        accentColor = ColorAmber
                    )
                }
            }
        }

        // Date + Seats + Search row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Date chip
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(ColorCard)
                    .clickable { onEvent(SearchRideEvent.ShowDatePicker) }
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Outlined.CalendarToday,
                        contentDescription = null,
                        tint = ColorAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = uiState.tripDate.ifEmpty { "Date" },
                        color = if (uiState.tripDate.isEmpty()) ColorTextMuted else ColorText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Seats counter
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(ColorCard)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    Icons.Outlined.Person,
                    null,
                    tint = ColorAccent,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = uiState.peopleCount.toString(),
                    color = ColorText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    SmallIconBtn(
                        icon = Icons.Default.Remove,
                        enabled = uiState.peopleCount > 1,
                        onClick = { onEvent(SearchRideEvent.DecrementPeopleCount) }
                    )
                    SmallIconBtn(
                        icon = Icons.Default.Add,
                        enabled = true,
                        onClick = { onEvent(SearchRideEvent.IncrementPeopleCount) }
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // Search button
            Button(
                onClick = { onEvent(SearchRideEvent.SearchTrips) },
                enabled = uiState.canSearch,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.height(40.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .background(
                            brush = if (uiState.canSearch)
                                Brush.horizontalGradient(listOf(ColorAccent, Color(0xFF22C55E)))
                            else Brush.horizontalGradient(listOf(ColorBorder, ColorBorder)),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.Search,
                            null,
                            tint = Color(0xFF052E16),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            "Search",
                            color = Color(0xFF052E16),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// LocationField and SmallIconBtn removed, now using shared versions in TripComponents.kt

// ─── Trip Card ────────────────────────────────────────────────────────────────

@Composable
fun CompactTripCard(trip: Trip, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(ColorSurface)
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

            // Driver row + price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(ColorCard),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(
                                if (trip.gender == "man") Res.drawable.man else Res.drawable.woman
                            ),
                            contentDescription = null,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            print("TripCard | User: ${trip.userName}")
                            Text(
                                text = trip.userName,
                                color = ColorText,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (trip.isUserVerified) {
                                Icon(
                                    Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = ColorAccent,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                        RatingChip(rating = trip.rating)
                    }
                }

                val diffPercent = if (trip.recommendedCost != null && trip.recommendedCost > 0) {
                    (kotlin.math.abs(trip.price - trip.recommendedCost) / trip.recommendedCost) * 100
                } else 0.0

                val priceColor = when {
                    trip.recommendedCost == null -> ColorAccent
                    diffPercent <= 20.0 -> ColorAccent
                    diffPercent <= 50.0 -> ColorError
                    else -> ColorError
                }
                
                val priceBgBrush = when {
                    trip.recommendedCost == null || diffPercent <= 20.0 -> Brush.horizontalGradient(
                        listOf(ColorAccent.copy(alpha = 0.15f), ColorAccentDim.copy(alpha = 0.3f))
                    )
                    else -> Brush.horizontalGradient(
                        listOf(ColorError.copy(alpha = 0.15f), ColorError.copy(alpha = 0.3f))
                    )
                }

                // Price badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(brush = priceBgBrush)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "₹${trip.price.toInt()}",
                        color = priceColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            // Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(ColorBorder)
            )

            // Chips row
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TripChip(icon = Icons.Outlined.Schedule, text = trip.time)
                TripChip(icon = Icons.Outlined.Person, text = "${trip.seats} seats")
            }
        }
    }
}

@Composable
fun TripChip(icon: ImageVector, text: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(ColorCard)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(icon, null, tint = ColorTextMuted, modifier = Modifier.size(14.dp))
        Text(text, color = ColorTextMuted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun RatingChip(rating: Double) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(ColorAmber.copy(alpha = 0.12f))
            .padding(horizontal = 7.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Icon(Icons.Default.Star, null, tint = ColorAmber, modifier = Modifier.size(12.dp))
        Text("$rating", color = ColorAmber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

// ─── Empty / Error ────────────────────────────────────────────────────────────

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 56.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(ColorCard),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.SearchOff,
                null,
                tint = ColorTextMuted,
                modifier = Modifier.size(32.dp)
            )
        }
        Text("No rides found", color = ColorText, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        Text("Try adjusting your search", color = ColorTextMuted, fontSize = 13.sp)
    }
}

@Composable
fun ErrorBanner(error: String, onRetry: () -> Unit, onDismiss: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(ColorError.copy(alpha = 0.12f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(Icons.Outlined.ErrorOutline, null, tint = ColorError, modifier = Modifier.size(18.dp))
        Text(error, color = ColorError, fontSize = 13.sp, modifier = Modifier.weight(1f))
        IconButton(onClick = onRetry, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Refresh, null, tint = ColorTextMuted, modifier = Modifier.size(18.dp))
        }
        IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Close, null, tint = ColorTextMuted, modifier = Modifier.size(18.dp))
        }
    }
}

// SmallIconBtn removed, now using shared version in TripComponents.kt

// ─── Date Picker ─────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = StdClock.System.now().toEpochMilliseconds()
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { millis ->
                    val instant = Instant.fromEpochMilliseconds(millis)
                    val date = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
                    onDateSelected(date.toString())
                }
            }) { Text("OK", color = ColorAccent) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = ColorTextMuted) }
        },
        colors = DatePickerDefaults.colors(
            containerColor = ColorSurface,
            titleContentColor = ColorText,
            headlineContentColor = ColorText,
            weekdayContentColor = ColorTextMuted,
            subheadContentColor = ColorTextMuted,
            dayContentColor = ColorText,
            selectedDayContainerColor = ColorAccent,
            selectedDayContentColor = Color(0xFF052E16),
            todayContentColor = ColorAccent,
            todayDateBorderColor = ColorAccent
        )
    ) {
        DatePicker(state = datePickerState)
    }
}