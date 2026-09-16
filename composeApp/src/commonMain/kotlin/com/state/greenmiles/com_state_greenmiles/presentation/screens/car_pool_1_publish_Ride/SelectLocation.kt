package com.state.greenmiles.com_state_greenmiles.presentation.screens.car_pool_1_publish_Ride

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.state.greenmiles.com_state_greenmiles.domain.model.trip.Coordinate
import com.state.greenmiles.com_state_greenmiles.domain.model.trip.Route
import com.state.greenmiles.com_state_greenmiles.presentation.screens.googlemap.GoogleMapView
import com.state.greenmiles.com_state_greenmiles.presentation.screens.googlemap.PlacePrediction
import com.state.greenmiles.com_state_greenmiles.presentation.screens.googlemap.getPlaceDetails
import com.state.greenmiles.com_state_greenmiles.presentation.screens.googlemap.rememberPlacesClient
import com.state.greenmiles.com_state_greenmiles.presentation.screens.googlemap.searchPlaces
import com.state.greenmiles.com_state_greenmiles.presentation.common.components.*
import kotlinx.coroutines.delay
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.time.Clock as StdClock

// Design tokens are now imported from TripComponents.kt

// ─── Step Progress ────────────────────────────────────────────────────────────

private val stepTitles = mapOf(
    CreateTripStep.LOCATION_SELECTION to "Locations",
    CreateTripStep.ROUTE_SELECTION    to "Route",
    CreateTripStep.TRIP_DETAILS       to "Trip Info",
    CreateTripStep.CAR_DETAILS        to "Car Details",
    CreateTripStep.REVIEW             to "Review"
)

@Composable
private fun StepProgressBar(currentStep: CreateTripStep) {
    val steps = CreateTripStep.entries
    val currentIndex = steps.indexOf(currentStep)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, step ->
            val isDone    = index < currentIndex
            val isCurrent = index == currentIndex

            // Dot
            Box(
                modifier = Modifier
                    .size(if (isCurrent) 10.dp else 8.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isDone    -> ColorAccent
                            isCurrent -> ColorAccent
                            else      -> ColorBorder
                        }
                    )
            )
            // Connecting line (except after last)
            if (index < steps.lastIndex) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .background(if (isDone) ColorAccent else ColorBorder)
                )
            }
        }
    }
}

// ─── Root Screen ─────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectLocation(
    createTripViewModel: CreateTripViewModel = koinViewModel(),
    onNavigateBack: () -> Unit = {},
    onTripCreated: () -> Unit = {}
) {
    val uiState by createTripViewModel.uiState.collectAsState()
    var showStartLocationSearch by remember { mutableStateOf(false) }
    var showEndLocationSearch   by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.tripCreated) {
        if (uiState.tripCreated != null) {
            onTripCreated()
            createTripViewModel.onEvent(CreateTripEvent.ResetForm)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBackground)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Column(modifier = Modifier.background(ColorBackground)) {
                    TopAppBar(
                        title = {
                            Text(
                                text = stepTitles[uiState.currentStep] ?: "",
                                color = ColorText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                if (uiState.currentStep == CreateTripStep.LOCATION_SELECTION)
                                    onNavigateBack()
                                else
                                    createTripViewModel.onEvent(CreateTripEvent.PreviousStep)
                            }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    null,
                                    tint = ColorText
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                    )
                    StepProgressBar(currentStep = uiState.currentStep)
                }
            }
        ) { paddingValues ->
            when (uiState.currentStep) {
                CreateTripStep.LOCATION_SELECTION -> LocationSelectionScreen(
                    uiState = uiState,
                    paddingValues = paddingValues,
                    onShowStartLocationSearch = { showStartLocationSearch = true },
                    onShowEndLocationSearch   = { showEndLocationSearch = true },
                    onFetchRoutes = { createTripViewModel.onEvent(CreateTripEvent.FetchRoutes) }
                )
                CreateTripStep.ROUTE_SELECTION -> RouteSelectionScreen(
                    uiState = uiState,
                    paddingValues = paddingValues,
                    onRouteSelected = { createTripViewModel.onEvent(CreateTripEvent.RouteSelected(it)) },
                    onContinue = { createTripViewModel.onEvent(CreateTripEvent.NextStep) }
                )
                CreateTripStep.TRIP_DETAILS -> TripDetailsScreen(
                    uiState = uiState,
                    paddingValues = paddingValues,
                    onEvent = { createTripViewModel.onEvent(it) },
                    onContinue = { createTripViewModel.onEvent(CreateTripEvent.NextStep) }
                )
                CreateTripStep.CAR_DETAILS -> CarDetailsScreen(
                    uiState = uiState,
                    paddingValues = paddingValues,
                    onEvent = { createTripViewModel.onEvent(it) },
                    onContinue = { createTripViewModel.onEvent(CreateTripEvent.NextStep) }
                )
                CreateTripStep.REVIEW -> ReviewScreen(
                    uiState = uiState,
                    paddingValues = paddingValues,
                    onCreateTrip = { createTripViewModel.onEvent(CreateTripEvent.CreateTrip) },
                    onEditStep = { step ->
                        while (uiState.currentStep != step)
                            createTripViewModel.onEvent(CreateTripEvent.PreviousStep)
                    }
                )
            }
        }
    }

    if (showStartLocationSearch) {
        LocationSearchDialog(
            onDismiss = { showStartLocationSearch = false },
            onLocationSelected = { name, coord ->
                createTripViewModel.onEvent(CreateTripEvent.StartLocationSelected(name, coord))
                showStartLocationSearch = false
            }
        )
    }
    if (showEndLocationSearch) {
        LocationSearchDialog(
            onDismiss = { showEndLocationSearch = false },
            onLocationSelected = { name, coord ->
                createTripViewModel.onEvent(CreateTripEvent.EndLocationSelected(name, coord))
                showEndLocationSearch = false
            }
        )
    }
}

// ─── Step 1 — Location Selection ─────────────────────────────────────────────

@Composable
fun LocationSelectionScreen(
    uiState: CreateTripUiState,
    paddingValues: PaddingValues,
    onShowStartLocationSearch: () -> Unit,
    onShowEndLocationSearch: () -> Unit,
    onFetchRoutes: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        // Map
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            GoogleMapView(
                modifier     = Modifier.fillMaxSize(),
                lat          = uiState.startCoordinate?.latitude ?: 15.3647,
                lng          = uiState.startCoordinate?.longitude ?: 75.1240,
                zoom         = 12f,
                startLocation = uiState.startCoordinate,
                endLocation   = uiState.endCoordinate,
                routes        = emptyList(),
                selectedRoute = null
            )
        }

        // Bottom panel
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(ColorSurface)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Set your route", color = ColorText, fontSize = 16.sp, fontWeight = FontWeight.Bold)

            LocationPickerButton(
                label = "Pickup location",
                value = uiState.startPointName,
                icon  = Icons.Default.TripOrigin,
                iconTint = ColorAccent,
                onClick = onShowStartLocationSearch
            )
            LocationPickerButton(
                label = "Drop-off location",
                value = uiState.endPointName,
                icon  = Icons.Default.LocationOn,
                iconTint = ColorAmber,
                onClick = onShowEndLocationSearch
            )

            uiState.routesError?.let {
                ErrorRow(it)
            }

            GreenButton(
                text = if (uiState.isLoadingRoutes) "Finding routes…" else "Find Routes",
                isLoading = uiState.isLoadingRoutes,
                enabled = uiState.startCoordinate != null && uiState.endCoordinate != null,
                onClick = onFetchRoutes
            )
        }
    }
}

// ─── Step 2 — Route Selection ─────────────────────────────────────────────────

@Composable
fun RouteSelectionScreen(
    uiState: CreateTripUiState,
    paddingValues: PaddingValues,
    onRouteSelected: (Route) -> Unit,
    onContinue: () -> Unit
) {
    var selectedIndex by remember { mutableStateOf(uiState.selectedRoute?.index ?: 0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            GoogleMapView(
                modifier      = Modifier.fillMaxSize(),
                lat           = uiState.startCoordinate?.latitude ?: 15.3647,
                lng           = uiState.startCoordinate?.longitude ?: 75.1240,
                zoom          = 12f,
                startLocation = uiState.startCoordinate,
                endLocation   = uiState.endCoordinate,
                routes        = uiState.routes,
                selectedRoute = uiState.routes.getOrNull(selectedIndex)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(ColorSurface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Available routes", color = ColorText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(ColorAccentDim)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("${uiState.routes.size} found", color = ColorAccent, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(ColorBorder))

            LazyColumn(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentPadding = PaddingValues(vertical = 6.dp)
            ) {
                itemsIndexed(uiState.routes) { index, route ->
                    RouteOptionCard(
                        route = route,
                        isSelected = selectedIndex == index,
                        onClick = { selectedIndex = index; onRouteSelected(route) }
                    )
                }
            }

            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(ColorBorder))

            Box(modifier = Modifier.padding(16.dp)) {
                GreenButton(
                    text = "Continue",
                    enabled = uiState.selectedRoute != null,
                    onClick = onContinue
                )
            }
        }
    }
}

@Composable
fun RouteOptionCard(route: Route, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(if (isSelected) ColorAccentDim.copy(alpha = 0.4f) else Color.Transparent)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Color index badge
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(getRouteColor(route.index).copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "${route.index + 1}",
                color = getRouteColor(route.index),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                route.summary.ifEmpty { "Route ${route.index + 1}" },
                color = ColorText,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    Icon(Icons.Outlined.Route, null, tint = ColorTextMuted, modifier = Modifier.size(12.dp))
                    Text("${route.distanceKm.format1Decimal()} km", color = ColorTextMuted, fontSize = 12.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    Icon(Icons.Outlined.Schedule, null, tint = ColorTextMuted, modifier = Modifier.size(12.dp))
                    Text(formatDuration(route.durationSeconds), color = ColorTextMuted, fontSize = 12.sp)
                }
            }
        }

        Icon(
            imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (isSelected) ColorAccent else ColorBorder,
            modifier = Modifier.size(22.dp)
        )
    }
}

// ─── Step 3 — Trip Details ────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailsScreen(
    uiState: CreateTripUiState,
    paddingValues: PaddingValues,
    onEvent: (CreateTripEvent) -> Unit,
    onContinue: () -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        RouteSummaryBanner(uiState)

        FormCard {
            FormSection(title = "When?") {
                // Date
                PickerRow(
                    icon = Icons.Outlined.CalendarToday,
                    label = "Trip Date",
                    value = uiState.tripDate.ifEmpty { "Select date" },
                    hasValue = uiState.tripDate.isNotEmpty(),
                    onClick = { showDatePicker = true }
                )
                Divider(color = ColorBorder, thickness = 1.dp)
                // Time
                PickerRow(
                    icon = Icons.Outlined.Schedule,
                    label = "Departure Time",
                    value = uiState.tripTime.ifEmpty { "Select time" },
                    hasValue = uiState.tripTime.isNotEmpty(),
                    onClick = { showTimePicker = true }
                )
            }
        }

        FormCard {
            FormSection(title = "Capacity & Pricing") {
                // Seats
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Outlined.EventSeat, null, tint = ColorAccent, modifier = Modifier.size(18.dp))
                        Column {
                            Text("Available Seats", color = ColorText, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text("Max 8 seats", color = ColorTextMuted, fontSize = 12.sp)
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StepperBtn(Icons.Default.Remove, uiState.availableSeats > 1) {
                            onEvent(CreateTripEvent.AvailableSeatsChanged(uiState.availableSeats - 1))
                        }
                        Text(
                            "${uiState.availableSeats}",
                            color = ColorText,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.widthIn(min = 20.dp),
                            textAlign = TextAlign.Center
                        )
                        StepperBtn(Icons.Default.Add, uiState.availableSeats < 8) {
                            onEvent(CreateTripEvent.AvailableSeatsChanged(uiState.availableSeats + 1))
                        }
                    }
                }

                Divider(color = ColorBorder, thickness = 1.dp)

                // Cost per seat
                var costInput by remember { mutableStateOf(if (uiState.costPerSeat > 0) uiState.costPerSeat.toInt().toString() else "") }
                
                LaunchedEffect(uiState.costPerSeat) {
                    val currentDouble = costInput.toDoubleOrNull() ?: 0.0
                    if (currentDouble != uiState.costPerSeat) {
                        costInput = if (uiState.costPerSeat > 0) uiState.costPerSeat.toInt().toString() else ""
                    }
                }

                OutlinedTextField(
                    value = costInput,
                    onValueChange = { v ->
                        // Allow digits only (if needed) or just general text parsing
                        val filtered = v.filter { it.isDigit() || it == '.' }
                        costInput = filtered
                        filtered.toDoubleOrNull()?.let { onEvent(CreateTripEvent.CostPerSeatChanged(it)) }
                        if (filtered.isEmpty()) onEvent(CreateTripEvent.CostPerSeatChanged(0.0))
                    },
                    label = { Text("Cost per Seat (₹)", fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(Icons.Outlined.CurrencyRupee, null, tint = ColorAccent, modifier = Modifier.size(18.dp))
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = greenTextFieldColors()
                )

                uiState.recommendedCost?.let { recommended ->
                    val diff = kotlin.math.abs(uiState.costPerSeat - recommended)
                    val diffPercent = if (recommended > 0) (diff / recommended) * 100 else 0.0
                    
                    val color = when {
                        diffPercent <= 20.0 -> ColorAccent
                        else -> ColorError
                    }
                    
                    Text(
                        text = "Recommended Cost: ₹${recommended.toInt()}",
                        color = color,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                    )
                }
            }
        }

        FormCard {
            FormSection(title = "Preferences") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp))
                                .background(ColorPink.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Female, null, tint = ColorPink, modifier = Modifier.size(18.dp))
                        }
                        Column {
                            Text("Woman co-passenger", color = ColorText, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text("Women passangers are preferred", color = ColorTextMuted, fontSize = 12.sp)

                        }
                    }
                    Switch(
                        checked = uiState.womanAccompany,
                        onCheckedChange = { onEvent(CreateTripEvent.WomanAccompanyChanged(it)) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF052E16),
                            checkedTrackColor = ColorAccent,
                            uncheckedThumbColor = ColorTextMuted,
                            uncheckedTrackColor = ColorBorder
                        )
                    )
                }
                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = uiState.miscMessage,
                    onValueChange = { if (it.length <= 200) onEvent(CreateTripEvent.MiscMessageChanged(it)) },
                    label = { Text("Instructions & Preferences (Optional)", fontSize = 13.sp) },
                    placeholder = { Text("e.g. No smoking, meet near Gate 2…", color = ColorTextMuted, fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Outlined.Notes, null, tint = ColorAccent, modifier = Modifier.size(18.dp)) },
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = greenTextFieldColors()
                )
            }
        }

        uiState.createTripError?.let { ErrorRow(it) }

        GreenButton(
            text = "Continue to Car Details",
            enabled = uiState.tripDate.isNotEmpty() && uiState.tripTime.isNotEmpty() &&
                    uiState.availableSeats >= 1 && uiState.costPerSeat > 0,
            onClick = onContinue
        )
        Spacer(Modifier.height(20.dp))
    }

    if (showDatePicker) {
        val dpState = rememberDatePickerState(initialSelectedDateMillis = Clock.System.now().toEpochMilliseconds())
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    dpState.selectedDateMillis?.let { millis ->
                        val instant = Instant.fromEpochMilliseconds(millis)
                        val date = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
                        onEvent(CreateTripEvent.TripDateChanged(date.toString()))
                    }
                    showDatePicker = false
                }) { Text("OK", color = ColorAccent) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel", color = ColorTextMuted) }
            },
            colors = DatePickerDefaults.colors(
                containerColor = ColorSurface,
                titleContentColor = ColorText,
                headlineContentColor = ColorText,
                weekdayContentColor = ColorTextMuted,
                dayContentColor = ColorText,
                selectedDayContainerColor = ColorAccent,
                selectedDayContentColor = Color(0xFF052E16),
                todayContentColor = ColorAccent,
                todayDateBorderColor = ColorAccent
            )
        ) { DatePicker(state = dpState) }
    }

    if (showTimePicker) {
        val tpState = rememberTimePickerState()
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            containerColor = ColorSurface,
            titleContentColor = ColorText,
            textContentColor = ColorText,
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancel", color = ColorTextMuted) }
            },
            confirmButton = {
                TextButton(onClick = {
                    val h = tpState.hour; val m = tpState.minute
                    onEvent(CreateTripEvent.TripTimeChanged(
                        buildString {
                            append(if (h < 10) "0$h" else h.toString())
                            append(":")
                            append(if (m < 10) "0$m" else m.toString())
                        }
                    ))
                    showTimePicker = false
                }) { Text("OK", color = ColorAccent) }
            },
            text = { TimePicker(state = tpState) }
        )
    }
}

// ─── Step 4 — Car Details ─────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarDetailsScreen(
    uiState: CreateTripUiState,
    paddingValues: PaddingValues,
    onEvent: (CreateTripEvent) -> Unit,
    onContinue: () -> Unit
) {
    var showCarTypePicker by remember { mutableStateOf(false) }
    var showYearPicker    by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        RouteSummaryBanner(uiState)

        if (uiState.savedCars.isNotEmpty()) {
            Text("Saved Vehicles", color = ColorTextMuted, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 4.dp, bottom = 2.dp))
            androidx.compose.foundation.lazy.LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(uiState.savedCars.size) { index ->
                    val car = uiState.savedCars[index]
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(ColorSurface)
                            .clickable { onEvent(CreateTripEvent.LoadSavedCarDetails(car)) }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Outlined.DirectionsCar, contentDescription = null, tint = ColorAccent, modifier = Modifier.size(20.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(car.model ?: "Car", color = ColorAccent, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(car.licensePlate ?: "", color = ColorTextMuted, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        FormCard {
            FormSection(title = "Vehicle Info") {
                GreenOutlinedField("Car Model", uiState.carModel, Icons.Outlined.DirectionsCar, "e.g. Honda City",
                    KeyboardOptions(capitalization = KeyboardCapitalization.Words)) {
                    onEvent(CreateTripEvent.CarModelChanged(it))
                }
                Spacer(Modifier.height(10.dp))
                GreenOutlinedField("License Plate", uiState.licensePlate, Icons.Outlined.Badge, "e.g. KA01AB1234",
                    KeyboardOptions(capitalization = KeyboardCapitalization.Characters)) {
                    onEvent(CreateTripEvent.LicensePlateChanged(it.uppercase()))
                }
                Spacer(Modifier.height(10.dp))
                GreenOutlinedField("Color", uiState.carColor, Icons.Outlined.Palette, "e.g. White, Black",
                    KeyboardOptions(capitalization = KeyboardCapitalization.Words)) {
                    onEvent(CreateTripEvent.CarColorChanged(it))
                }
                Spacer(Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    PickerRow(
                        modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).background(ColorCard).padding(12.dp),
                        icon = Icons.Outlined.CalendarToday,
                        label = "Year",
                        value = uiState.carYear.toString(),
                        hasValue = true,
                        onClick = { showYearPicker = true }
                    )
                    PickerRow(
                        modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).background(ColorCard).padding(12.dp),
                        icon = Icons.Outlined.Category,
                        label = "Type",
                        value = uiState.carType.ifEmpty { "Select" },
                        hasValue = uiState.carType.isNotEmpty(),
                        onClick = { showCarTypePicker = true }
                    )
                }
            }
        }

        // Info note
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(ColorBlue.copy(alpha = 0.1f))
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(Icons.Outlined.Info, null, tint = ColorBlue, modifier = Modifier.size(16.dp))
            Text(
                "Your car details will be visible to passengers for verification.",
                color = ColorBlue,
                fontSize = 12.sp
            )
        }

        uiState.createTripError?.let { ErrorRow(it) }

        GreenButton(
            text = "Continue to Review",
            enabled = uiState.carModel.isNotEmpty() && uiState.licensePlate.isNotEmpty() &&
                    uiState.carColor.isNotEmpty() && uiState.carType.isNotEmpty(),
            onClick = onContinue
        )
        Spacer(Modifier.height(20.dp))
    }

    if (showCarTypePicker) {
        CarTypePickerDialog(
            currentType = uiState.carType,
            onDismiss = { showCarTypePicker = false },
            onTypeSelected = { onEvent(CreateTripEvent.CarTypeChanged(it)); showCarTypePicker = false }
        )
    }
    if (showYearPicker) {
        YearPickerDialog(
            currentYear = uiState.carYear,
            onDismiss = { showYearPicker = false },
            onYearSelected = { onEvent(CreateTripEvent.CarYearChanged(it)); showYearPicker = false }
        )
    }
}

// ─── Step 5 — Review ─────────────────────────────────────────────────────────

@Composable
fun ReviewScreen(
    uiState: CreateTripUiState,
    paddingValues: PaddingValues,
    onCreateTrip: () -> Unit,
    onEditStep: (CreateTripStep) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(
                    brush = Brush.linearGradient(listOf(ColorAccentDim, ColorCard))
                )
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).background(ColorAccent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.CheckCircle, null, tint = ColorAccent, modifier = Modifier.size(28.dp))
            }
            Column {
                Text("Almost there!", color = ColorText, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Text("Review and publish your trip", color = ColorTextMuted, fontSize = 13.sp)
            }
        }

        // Route section
        ReviewSection("Route Details", Icons.Outlined.Route, { onEditStep(CreateTripStep.ROUTE_SELECTION) }) {
            ReviewRow(Icons.Default.TripOrigin, "From", uiState.startPointName, ColorAccent)
            ReviewRow(Icons.Default.LocationOn, "To", uiState.endPointName, ColorAmber)
            uiState.selectedRoute?.let { route ->
                ReviewRow(Icons.Outlined.Straighten, "Distance", "${route.distanceKm.format1Decimal()} km", ColorBlue)
                ReviewRow(Icons.Outlined.Timer, "Duration", formatDuration(route.durationSeconds), ColorBlue)
            }
        }

        // Trip details section
        ReviewSection("Trip Details", Icons.Outlined.CalendarToday, { onEditStep(CreateTripStep.TRIP_DETAILS) }) {
            ReviewRow(Icons.Outlined.DateRange, "Date", uiState.tripDate, ColorAccent)
            ReviewRow(Icons.Outlined.Schedule, "Time", uiState.tripTime, ColorAccent)
            ReviewRow(Icons.Outlined.EventSeat, "Seats", "${uiState.availableSeats} available", ColorBlue)
            ReviewRow(Icons.Outlined.CurrencyRupee, "Per seat", "₹${uiState.costPerSeat}", ColorAmber)
            if (uiState.womanAccompany)
                ReviewRow(Icons.Default.Female, "Woman co-passenger", "Yes", ColorPink)
            if (uiState.miscMessage.isNotBlank())
                ReviewRow(Icons.Outlined.Notes, "Notes", uiState.miscMessage, ColorTextMuted)
        }

        // Car details section
        ReviewSection("Car Details", Icons.Outlined.DirectionsCar, { onEditStep(CreateTripStep.CAR_DETAILS) }) {
            ReviewRow(Icons.Outlined.DirectionsCar, "Model", uiState.carModel, ColorTextMuted)
            ReviewRow(Icons.Outlined.Badge, "Plate", uiState.licensePlate, ColorTextMuted)
            ReviewRow(Icons.Outlined.Palette, "Color", uiState.carColor, ColorTextMuted)
            ReviewRow(Icons.Outlined.CalendarToday, "Year", uiState.carYear.toString(), ColorTextMuted)
            ReviewRow(Icons.Outlined.Category, "Type", uiState.carType, ColorTextMuted)
        }

        // Earnings card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(ColorAmber.copy(alpha = 0.1f))
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(Icons.Default.MonetizationOn, null, tint = ColorAmber, modifier = Modifier.size(28.dp))
            Column {
                Text("Potential earnings", color = ColorAmber, fontSize = 12.sp)
                Text(
                    "₹${(uiState.costPerSeat * uiState.availableSeats).toInt()}",
                    color = ColorText,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text("If all ${uiState.availableSeats} seats are booked", color = ColorTextMuted, fontSize = 12.sp)
            }
        }

        uiState.createTripError?.let { ErrorRow(it) }

        // Publish button
        GreenButton(
            text = if (uiState.isCreatingTrip) "Publishing…" else "Publish Trip",
            isLoading = uiState.isCreatingTrip,
            enabled = !uiState.isCreatingTrip,
            onClick = onCreateTrip,
            icon = Icons.Default.Check
        )

        // Terms note
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(ColorCard)
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(Icons.Outlined.Info, null, tint = ColorTextMuted, modifier = Modifier.size(14.dp))
            Text(
                "By publishing this trip, you agree to our terms and commit to completing it as scheduled.",
                color = ColorTextMuted,
                fontSize = 12.sp
            )
        }

        Spacer(Modifier.height(20.dp))
    }
}

// ─── Location Search Dialog ───────────────────────────────────────────────────

@Composable
fun LocationSearchDialog(
    onDismiss: () -> Unit,
    onLocationSelected: (String, Coordinate) -> Unit
) {
    var searchQuery   by remember { mutableStateOf("") }
    var predictions   by remember { mutableStateOf<List<PlacePrediction>>(emptyList()) }
    var isSearching   by remember { mutableStateOf(false) }
    val placesClient  = rememberPlacesClient()

    LaunchedEffect(searchQuery) {
        if (searchQuery.length >= 3) {
            isSearching = true
            delay(500)
            predictions = searchPlaces(placesClient, searchQuery)
            isSearching = false
        } else predictions = emptyList()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.8f)
                .clip(RoundedCornerShape(20.dp))
                .background(ColorSurface)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Search bar
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = ColorText)
                    }
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.weight(1f).height(52.dp),
                        placeholder = { Text("Search location…", color = ColorTextMuted, fontSize = 14.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = greenTextFieldColors()
                    )
                }

                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(ColorBorder))

                when {
                    isSearching -> Box(
                        modifier = Modifier.fillMaxWidth().padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator(color = ColorAccent, strokeWidth = 2.5.dp) }

                    predictions.isEmpty() && searchQuery.length >= 3 -> Box(
                        modifier = Modifier.fillMaxWidth().padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) { Text("No locations found", color = ColorTextMuted) }

                    else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(predictions) { prediction ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        getPlaceDetails(
                                            placesClient = placesClient,
                                            placeId = prediction.placeId,
                                            onResult = { name, coord -> onLocationSelected(name, coord) }
                                        )
                                    }
                                    .padding(horizontal = 20.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(Icons.Outlined.LocationOn, null, tint = ColorTextMuted, modifier = Modifier.size(18.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(prediction.primaryText, color = ColorText, fontSize = 14.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(prediction.secondaryText, color = ColorTextMuted, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                            Box(modifier = Modifier.fillMaxWidth().padding(start = 50.dp).height(1.dp).background(ColorBorder))
                        }
                    }
                }
            }
        }
    }
}

// ─── Car Type / Year pickers ──────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarTypePickerDialog(currentType: String, onDismiss: () -> Unit, onTypeSelected: (String) -> Unit) {
    val types = listOf("Hatchback", "SUV", "Sedan", "Coupe", "Convertible", "Wagon")
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = ColorSurface,
        dragHandle = {
            Box(modifier = Modifier.padding(top = 14.dp, bottom = 6.dp).size(width = 40.dp, height = 4.dp).clip(CircleShape).background(ColorBorder))
        }
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)) {
            Text("Car Type", color = ColorText, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp))
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(ColorBorder))
            types.forEach { type ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (type == currentType) ColorAccentDim.copy(alpha = 0.3f) else Color.Transparent)
                        .clickable { onTypeSelected(type) }
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Outlined.DirectionsCar, null, tint = if (type == currentType) ColorAccent else ColorTextMuted, modifier = Modifier.size(18.dp))
                    Text(type, color = if (type == currentType) ColorAccent else ColorText, fontSize = 15.sp, fontWeight = if (type == currentType) FontWeight.Bold else FontWeight.Normal, modifier = Modifier.weight(1f))
                    if (type == currentType) Icon(Icons.Default.CheckCircle, null, tint = ColorAccent, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YearPickerDialog(currentYear: Int, onDismiss: () -> Unit, onYearSelected: (Int) -> Unit) {
    val currentCalYear = remember { StdClock.System.now().toLocalDateTime(TimeZone.UTC).year }
    val years = remember { (currentCalYear downTo 2000).toList() }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = ColorSurface,
        dragHandle = {
            Box(modifier = Modifier.padding(top = 14.dp, bottom = 6.dp).size(width = 40.dp, height = 4.dp).clip(CircleShape).background(ColorBorder))
        }
    ) {
        Column(modifier = Modifier.fillMaxWidth().heightIn(max = 420.dp).padding(bottom = 32.dp)) {
            Text("Manufacturing Year", color = ColorText, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp))
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(ColorBorder))
            LazyColumn {
                items(years) { year ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (year == currentYear) ColorAccentDim.copy(alpha = 0.3f) else Color.Transparent)
                            .clickable { onYearSelected(year) }
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(year.toString(), color = if (year == currentYear) ColorAccent else ColorText, fontSize = 15.sp, fontWeight = if (year == currentYear) FontWeight.Bold else FontWeight.Normal)
                        if (year == currentYear) Icon(Icons.Default.CheckCircle, null, tint = ColorAccent, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

// Components are now imported from TripComponents.kt

@Composable
fun RouteSummaryBanner(uiState: CreateTripUiState) {
    if (uiState.startPointName.isEmpty() || uiState.endPointName.isEmpty()) return

    FormCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Default.RadioButtonChecked, null, tint = ColorAccent, modifier = Modifier.size(16.dp))
                    Text(uiState.startPointName, color = ColorText, fontSize = 14.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Box(modifier = Modifier.padding(start = 7.dp).width(2.dp).height(16.dp).background(ColorBorder))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Default.LocationOn, null, tint = ColorError, modifier = Modifier.size(16.dp))
                    Text(uiState.endPointName, color = ColorText, fontSize = 14.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            
            uiState.selectedRoute?.let { route ->
                Column(horizontalAlignment = Alignment.End) {
                    Text("${route.distanceKm.format1Decimal()} km", color = ColorText, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    Text(formatDuration(route.durationSeconds), color = ColorTextMuted, fontSize = 12.sp)
                }
            }
        }
    }
}

// ─── Helpers ──────────────────────────────────────────────────────────────────

// Helpers are now used from TripComponents.kt

fun getRouteColor(index: Int): Color {
    val colors = listOf(
        Color(0xFF4ADE80), Color(0xFF60A5FA), Color(0xFFFBBF24),
        Color(0xFFF472B6), Color(0xFFA78BFA)
    )
    return colors[index % colors.size]
}

fun Double.format1Decimal(): String = (kotlin.math.round(this * 10) / 10.0).toString()