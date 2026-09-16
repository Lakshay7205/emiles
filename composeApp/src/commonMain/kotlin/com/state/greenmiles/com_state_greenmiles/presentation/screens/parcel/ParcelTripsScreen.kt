package com.state.greenmiles.com_state_greenmiles.presentation.screens.parcel

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.state.greenmiles.com_state_greenmiles.domain.model.parcel.ParcelTrip
import com.state.greenmiles.com_state_greenmiles.presentation.common.components.*
import com.state.greenmiles.com_state_greenmiles.presentation.common.models.ValidationState
import com.state.greenmiles.com_state_greenmiles.presentation.screens.car_pool__search_ride.DatePickerDialog
import com.state.greenmiles.com_state_greenmiles.presentation.screens.googlemap.PlacePrediction
import com.state.greenmiles.com_state_greenmiles.presentation.screens.googlemap.rememberPlacesClient
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

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

@Composable
fun ParcelTripsScreen(
    viewModel: ParcelTripsViewModel = koinViewModel(),
    onNavigateToTripDetails: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is ParcelTripsEffect.ShowToast -> snackbarHostState.showSnackbar(effect.message)
                is ParcelTripsEffect.NavigateToTripDetails -> {
                    onNavigateToTripDetails(effect.tripId)
                }
                ParcelTripsEffect.ScrollToTop -> listState.animateScrollToItem(0)
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
                CompactParcelSearchForm(uiState = uiState, onEvent = viewModel::onEvent)
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
                                    text = "${uiState.searchResults.size} parcel trips found",
                                    color = ColorAccent,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    items(items = uiState.searchResults, key = { it.id }) { trip ->
                        PremiumParcelTripCard(
                            trip = trip,
                            onClick = {
                                viewModel.onEvent(ParcelTripsEvent.SelectTrip(trip.id))
                            }
                        )
                    }

                    if (uiState.hasMoreResults) {
                        item {
                            TextButton(
                                onClick = { viewModel.onEvent(ParcelTripsEvent.LoadMoreTrips) },
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
                    item { ParcelEmptyState() }
                }

                if (uiState.error != null) {
                    item {
                        ParcelErrorBanner(
                            error = uiState.error ?: "",
                            onRetry = { viewModel.onEvent(ParcelTripsEvent.RetrySearch) },
                            onDismiss = { viewModel.onEvent(ParcelTripsEvent.ClearError) }
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
                        Text("Finding parcel trips…", color = ColorText, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }

    if (uiState.showDatePicker) {
        DatePickerDialog(
            onDateSelected = { viewModel.onEvent(ParcelTripsEvent.UpdateTripDate(it)) },
            onDismiss = { viewModel.onEvent(ParcelTripsEvent.HideDatePicker) }
        )
    }

    }


@Composable
fun CompactParcelSearchForm(
    uiState: ParcelTripsUiState,
    onEvent: (ParcelTripsEvent) -> Unit
) {
    val placesClient = rememberPlacesClient()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorBackground)
            .padding(start = 20.dp, end = 20.dp, top = 52.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Find a Parcel",
                color = ColorText,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp
            )
            IconButton(
                onClick = { onEvent(ParcelTripsEvent.ClearSearch) },
                modifier = Modifier.size(32.dp).clip(CircleShape).background(ColorSurface)
            ) {
                Icon(Icons.Default.Refresh, null, tint = ColorAccent, modifier = Modifier.size(16.dp))
            }
        }

        // Search Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(ColorSurface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    LocationField(
                        label = "From — current location",
                        query = uiState.startPlaceQuery,
                        predictions = uiState.startPlacePredictions,
                        showPredictions = uiState.showStartPredictions,
                        isSearching = uiState.isSearchingStartPlaces,
                        validationState = uiState.validation.startLocationValid,
                        onQueryChange = { onEvent(ParcelTripsEvent.UpdateStartQuery(it, placesClient)) },
                        onPredictionClick = { onEvent(ParcelTripsEvent.SelectStartPlace(it, placesClient)) },
                        onClear = { onEvent(ParcelTripsEvent.ClearStartLocation) },
                        icon = Icons.Default.TripOrigin,
                        accentColor = ColorAccent
                    )
                    LocationField(
                        label = "To — destination",
                        query = uiState.endPlaceQuery,
                        predictions = uiState.endPlacePredictions,
                        showPredictions = uiState.showEndPredictions,
                        isSearching = uiState.isSearchingEndPlaces,
                        validationState = uiState.validation.endLocationValid,
                        onQueryChange = { onEvent(ParcelTripsEvent.UpdateEndQuery(it, placesClient)) },
                        onPredictionClick = { onEvent(ParcelTripsEvent.SelectEndPlace(it, placesClient)) },
                        onClear = { onEvent(ParcelTripsEvent.ClearEndLocation) },
                        icon = Icons.Default.LocationOn,
                        accentColor = ColorAmber
                    )
                }
            }

            // Row 1: Transport Type and Package Size
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    TransportTypeSelector(
                        selectedType = uiState.transportType,
                        onTypeSelected = { onEvent(ParcelTripsEvent.UpdateTransportType(it)) }
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    PackageSizeSelector(
                        selectedSize = uiState.packageSize,
                        onSizeSelected = { onEvent(ParcelTripsEvent.UpdatePackageSize(it)) }
                    )
                }
            }

            // Row 2: Date and Search Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Date chip (taking half width)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ColorCard)
                        .clickable { onEvent(ParcelTripsEvent.ShowDatePicker) }
                        .padding(horizontal = 12.dp, vertical = 13.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Outlined.CalendarToday, null, tint = ColorAccent, modifier = Modifier.size(16.dp))
                        Text(
                            text = uiState.tripDate.ifEmpty { "Trip Date" },
                            color = if (uiState.tripDate.isEmpty()) ColorTextMuted else ColorText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Search button (taking half width)
                Button(
                    onClick = { onEvent(ParcelTripsEvent.SearchTrips) },
                    enabled = uiState.canSearch,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.weight(1f).height(46.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = if (uiState.canSearch)
                                    Brush.horizontalGradient(listOf(ColorAccent, Color(0xFF22C55E)))
                                else Brush.horizontalGradient(listOf(ColorBorder, ColorBorder)),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Search, null, tint = Color(0xFF052E16), modifier = Modifier.size(18.dp))
                            Text(
                                text = if (uiState.isSearching) "Searching..." else "Search",
                                color = Color(0xFF052E16),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PremiumParcelTripCard(trip: ParcelTrip, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(ColorSurface)
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(ColorCard),
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
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = if (trip.carDetails != null) trip.carDetails.model else trip.transportDetails?.name ?: "Parcel Trip",
                            color = ColorText,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (trip.carDetails != null) "${trip.carDetails.color} ${trip.carDetails.type}" else trip.transportDetails?.operator ?: "Public Transport",
                            color = ColorTextMuted,
                            fontSize = 12.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(brush = Brush.horizontalGradient(listOf(ColorAccent.copy(alpha = 0.15f), ColorAccentDim.copy(alpha = 0.3f))))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "₹${trip.costPerKg.toInt()}/kg",
                        color = ColorAccent,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(ColorBorder))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Default.TripOrigin, null, tint = ColorAccent, modifier = Modifier.size(16.dp))
                    Text(trip.startPointName, color = ColorText, fontSize = 14.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Default.LocationOn, null, tint = ColorAmber, modifier = Modifier.size(16.dp))
                    Text(trip.endPointName, color = ColorText, fontSize = 14.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                TripChip(icon = Icons.Outlined.Schedule, text = trip.tripTime)
                TripChip(icon = Icons.Outlined.Inventory2, text = "${trip.availableSpace} kg")
                Spacer(Modifier.weight(1f))
                Icon(Icons.Default.ChevronRight, null, tint = ColorBorder, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun TripChip(icon: ImageVector, text: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(ColorCard)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(icon, null, tint = ColorTextMuted, modifier = Modifier.size(14.dp))
        Text(text, color = ColorTextMuted, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun TransportTypeSelector(selectedType: String, onTypeSelected: (String) -> Unit, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    val types = listOf("bus", "train", "flight", "car")

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Transport Type", color = ColorTextMuted, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(ColorCard)
                .clickable { expanded = true }
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = when(selectedType) {
                            "train" -> Icons.Outlined.Train
                            "flight" -> Icons.Outlined.Flight
                            "car" -> Icons.Outlined.DirectionsCar
                            else -> Icons.Outlined.DirectionsBus
                        },
                        contentDescription = null,
                        tint = ColorAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(selectedType.replaceFirstChar { it.uppercase() }, color = ColorText, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
                Icon(Icons.Default.ArrowDropDown, null, tint = ColorTextMuted, modifier = Modifier.size(16.dp))
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.background(ColorCard)) {
            types.forEach { tType ->
                DropdownMenuItem(
                    text = { Text(tType.replaceFirstChar { it.uppercase() }, color = ColorText) },
                    onClick = { onTypeSelected(tType); expanded = false }
                )
            }
        }
    }
}


@Composable
fun ParcelEmptyState() {
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
                .clip(RoundedCornerShape(24.dp))
                .background(ColorCard),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.LocalShipping, null, tint = ColorTextMuted, modifier = Modifier.size(32.dp))
        }
        Text("No parcels found", color = ColorText, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        Text("Try adjusting your search criteria", color = ColorTextMuted, fontSize = 13.sp)
    }
}

@Composable
fun ParcelErrorBanner(error: String, onRetry: () -> Unit, onDismiss: () -> Unit) {
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

