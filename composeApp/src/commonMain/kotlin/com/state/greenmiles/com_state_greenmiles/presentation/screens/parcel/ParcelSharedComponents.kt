package com.state.greenmiles.com_state_greenmiles.presentation.screens.parcel

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.state.greenmiles.com_state_greenmiles.domain.model.trip.Coordinate
import com.state.greenmiles.com_state_greenmiles.domain.model.trip.Route
import com.state.greenmiles.com_state_greenmiles.presentation.common.components.*
import com.state.greenmiles.com_state_greenmiles.presentation.screens.googlemap.GoogleMapView
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

// ─── Design Tokens (shared with parcel screens) ──────────────────────────────

// Design tokens are imported from TripComponents.kt via common.components.*


// ─── Shared Selectors ──────────────────────────────────────────────────────────

@Composable
fun PackageSizeSelector(
    selectedSize: String,
    onSizeSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val sizes = listOf("small", "medium", "large", "extra_large")
    val currentLabel = selectedSize.replace("_", " ").replaceFirstChar { it.uppercase() }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Package Size", color = ColorTextMuted, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(ColorCard)
                .border(1.dp, ColorBorder, RoundedCornerShape(12.dp))
                .clickable { expanded = true }
                .padding(horizontal = 12.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Inventory2, null, tint = ColorAccent, modifier = Modifier.size(18.dp))
                    Text(
                        text = if (selectedSize == "extra_large") "Extra Large (XL)" else currentLabel,
                        color = ColorText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Icon(Icons.Default.ArrowDropDown, null, tint = ColorTextMuted, modifier = Modifier.size(20.dp))
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .background(ColorCard)
                    .border(1.dp, ColorBorder, RoundedCornerShape(8.dp))
            ) {
                sizes.forEach { size ->
                    val label = size.replace("_", " ").replaceFirstChar { it.uppercase() }
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = if (size == "extra_large") "Extra Large (XL)" else label,
                                color = if (size == selectedSize) ColorAccent else ColorText
                            )
                        },
                        onClick = {
                            onSizeSelected(size)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

// ─── Progress Bar ─────────────────────────────────────────────────────────────

@Composable
fun ParcelStepProgressBar(currentStep: CreateParcelStep, isPersonal: Boolean) {
    val steps = CreateParcelStep.values().filter { isPersonal || it != CreateParcelStep.CAR_DETAILS }
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

            Box(
                modifier = Modifier
                    .size(if (isCurrent) 10.dp else 8.dp)
                    .clip(CircleShape)
                    .background(if (isDone || isCurrent) ColorAccent else ColorBorder)
            )
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

// ─── Type Selection ───────────────────────────────────────────────────────────

@Composable
fun ParcelTypeSelection(
    uiState: CreateParcelTripUiState,
    paddingValues: PaddingValues,
    onSelect: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text("How are you travelling?", color = ColorText, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Select the mode of transport to offer parcel delivery service.", color = ColorTextMuted, fontSize = 14.sp)

        TypeCard(
            title = "Personal Car",
            desc = "I am driving my own car and have extra space for parcels.",
            icon = Icons.Default.DirectionsCar,
            isSelected = uiState.isPersonalCar,
            onClick = { onSelect(true) }
        )

        TypeCard(
            title = "Public Transport",
            desc = "I'm travelling via Bus, Train or Flight and can carry a small parcel.",
            icon = Icons.Default.DirectionsBus,
            isSelected = !uiState.isPersonalCar && uiState.currentStep != CreateParcelStep.PARCEL_TYPE,
            onClick = { onSelect(false) }
        )
    }
}

@Composable
fun TypeCard(title: String, desc: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) ColorAccentDim.copy(alpha = 0.3f) else ColorSurface)
            .border(2.dp, if (isSelected) ColorAccent else ColorBorder, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(
                modifier = Modifier.size(56.dp).clip(CircleShape).background(ColorCard),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = ColorAccent, modifier = Modifier.size(28.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = ColorText, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(desc, color = ColorTextMuted, fontSize = 13.sp)
            }
            Icon(
                if (isSelected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                null,
                tint = if (isSelected) ColorAccent else ColorBorder
            )
        }
    }
}

// ─── Location Selection ───────────────────────────────────────────────────────

@Composable
fun LocationSelectionScreen(
    startPointName: String,
    endPointName: String,
    startCoordinate: Coordinate?,
    endCoordinate: Coordinate?,
    isLoadingRoutes: Boolean,
    routesError: String?,
    paddingValues: PaddingValues,
    onShowStartLocationSearch: () -> Unit,
    onShowEndLocationSearch: () -> Unit,
    onFetchRoutes: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            GoogleMapView(
                modifier = Modifier.fillMaxSize(),
                lat = startCoordinate?.latitude ?: 15.3647,
                lng = startCoordinate?.longitude ?: 75.1240,
                zoom = 12f,
                startLocation = startCoordinate,
                endLocation = endCoordinate,
                routes = emptyList(),
                selectedRoute = null
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(ColorSurface).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Set your route", color = ColorText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            LocationPickerButton(label = "Parcel origin", value = startPointName, icon = Icons.Default.TripOrigin, iconTint = ColorAccent, onClick = onShowStartLocationSearch)
            LocationPickerButton(label = "Parcel destination", value = endPointName, icon = Icons.Default.LocationOn, iconTint = ColorAmber, onClick = onShowEndLocationSearch)
            routesError?.let { ErrorRow(it) }
            GreenButton(text = if (isLoadingRoutes) "Finding routes…" else "Find Routes", isLoading = isLoadingRoutes, enabled = startCoordinate != null && endCoordinate != null, onClick = onFetchRoutes)
        }
    }
}

// ─── Route Selection ──────────────────────────────────────────────────────────

@Composable
fun ParcelRouteSelectionScreen(
    uiState: CreateParcelTripUiState,
    paddingValues: PaddingValues,
    onRouteSelected: (Route) -> Unit,
    onContinue: () -> Unit
) {
    var selectedIndex by remember { mutableStateOf(uiState.selectedRoute?.index ?: 0) }

    Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            GoogleMapView(
                modifier = Modifier.fillMaxSize(),
                lat = uiState.startCoordinate?.latitude ?: 15.3647,
                lng = uiState.startCoordinate?.longitude ?: 75.1240,
                zoom = 12f,
                startLocation = uiState.startCoordinate,
                endLocation = uiState.endCoordinate,
                routes = uiState.routes,
                selectedRoute = uiState.routes.getOrNull(selectedIndex)
            )
        }
        Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)).background(ColorSurface)) {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Available routes", color = ColorText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Box(modifier = Modifier.clip(RoundedCornerShape(50)).background(ColorAccentDim).padding(horizontal = 10.dp, vertical = 4.dp)) {
                    Text("${uiState.routes.size} found", color = ColorAccent, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(ColorBorder))
            LazyColumn(modifier = Modifier.fillMaxWidth().height(200.dp), contentPadding = PaddingValues(vertical = 6.dp)) {
                itemsIndexed(uiState.routes) { index, route ->
                    ParcelRouteSharedCard(route = route, isSelected = selectedIndex == index, onClick = { selectedIndex = index; onRouteSelected(route) })
                }
            }
            Box(modifier = Modifier.padding(16.dp)) {
                GreenButton(text = "Continue", enabled = uiState.selectedRoute != null, onClick = onContinue)
            }
        }
    }
}

@Composable
fun ParcelRouteSharedCard(route: Route, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(if (isSelected) ColorAccentDim.copy(alpha = 0.4f) else Color.Transparent)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
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

// ─── Trip Details Form ────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParcelDetailsForm(
    uiState: CreateParcelTripUiState,
    paddingValues: PaddingValues,
    onEvent: (CreateParcelTripEvent) -> Unit,
    onContinue: () -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth().padding(paddingValues).verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (!uiState.isPersonalCar) {
            FormCard {
                FormSection(title = "Transport Details") {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TransportOption(Icons.Default.DirectionsBus, "Bus", uiState.transportType == "bus", modifier = Modifier.weight(1f)) { onEvent(CreateParcelTripEvent.UpdateTransportType("bus")) }
                        TransportOption(Icons.Default.Train, "Train", uiState.transportType == "train", modifier = Modifier.weight(1f)) { onEvent(CreateParcelTripEvent.UpdateTransportType("train")) }
                        TransportOption(Icons.Default.Flight, "Flight", uiState.transportType == "flight", modifier = Modifier.weight(1f)) { onEvent(CreateParcelTripEvent.UpdateTransportType("flight")) }
                    }
                    Spacer(Modifier.height(12.dp))
                    GreenOutlinedField("Operator Name", uiState.operatorName, Icons.Outlined.Business, "e.g. KSRTC, Indigo", KeyboardOptions(capitalization = KeyboardCapitalization.Words)) { onEvent(CreateParcelTripEvent.OperatorNameChanged(it)) }
                    Spacer(Modifier.height(8.dp))
                    GreenOutlinedField("Vehicle/Trip No", uiState.vehicleNumber, Icons.Outlined.Numbers, "e.g. MH01-1234, 6E-213", KeyboardOptions(capitalization = KeyboardCapitalization.Characters)) { onEvent(CreateParcelTripEvent.VehicleNumberChanged(it)) }
                }
            }
        }

        FormCard {
            FormSection(title = "When?") {
                PickerRow(icon = Icons.Outlined.CalendarToday, label = "Trip Date", value = uiState.tripDate.ifEmpty { "Select date" }, hasValue = uiState.tripDate.isNotEmpty(), onClick = { showDatePicker = true })
                Divider(color = ColorBorder, thickness = 1.dp)
                PickerRow(icon = Icons.Outlined.Schedule, label = "Departure Time", value = uiState.tripTime.ifEmpty { "Select time" }, hasValue = uiState.tripTime.isNotEmpty(), onClick = { showTimePicker = true })
            }
        }

        FormCard {
            FormSection(title = "Package Size") {
                PackageSizeSelector(
                    selectedSize = uiState.availableSpace,
                    onSizeSelected = { onEvent(CreateParcelTripEvent.AvailableSpaceChanged(it)) }
                )
            }
            Divider(color = ColorBorder, thickness = 1.dp)
            GreenOutlinedField("Cost per Kg (₹)", if(uiState.costPerKg > 0) uiState.costPerKg.toString() else "", Icons.Outlined.CurrencyRupee, "0.0", KeyboardOptions(keyboardType = KeyboardType.Decimal)) { val cost = it.toDoubleOrNull() ?: 0.0; onEvent(CreateParcelTripEvent.CostPerKgChanged(cost)) }
        }

        FormCard {
            FormSection(title = "Instructions & Preferences") {
                GreenOutlinedField("Special Notes (Optional)", uiState.message, Icons.Outlined.Notes, "e.g. No fragile items, meet at Gate 1...", KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)) { onEvent(CreateParcelTripEvent.MessageChanged(it)) }
            }
        }

        uiState.createTripError?.let { ErrorRow(it) }
        GreenButton(text = if (uiState.isPersonalCar) "Continue to Car Details" else "Review Trip", enabled = uiState.tripDate.isNotEmpty() && uiState.tripTime.isNotEmpty() && uiState.costPerKg > 0, onClick = onContinue)
    }

    if (showDatePicker) {
        val dpState = rememberDatePickerState(initialSelectedDateMillis = Clock.System.now().toEpochMilliseconds())
        DatePickerDialog(onDismissRequest = { showDatePicker = false }, confirmButton = { TextButton(onClick = { dpState.selectedDateMillis?.let { onEvent(CreateParcelTripEvent.TripDateChanged(Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.currentSystemDefault()).date.toString())) }; showDatePicker = false }) { Text("OK", color = ColorAccent) } }) { DatePicker(state = dpState) }
    }
    if (showTimePicker) {
        val tpState = rememberTimePickerState()
        AlertDialog(onDismissRequest = { showTimePicker = false }, confirmButton = { TextButton(onClick = { onEvent(CreateParcelTripEvent.TripTimeChanged("${if(tpState.hour<10)"0${tpState.hour}" else tpState.hour}:${if(tpState.minute<10)"0${tpState.minute}" else tpState.minute}")); showTimePicker = false }) { Text("OK", color = ColorAccent) } }, text = { TimePicker(state = tpState) }, containerColor = ColorSurface)
    }
}

@Composable
fun TransportOption(icon: ImageVector, label: String, isSelected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier.clip(RoundedCornerShape(12.dp)).background(if (isSelected) ColorAccentDim.copy(alpha = 0.3f) else ColorCard)
            .border(1.dp, if (isSelected) ColorAccent else ColorBorder, RoundedCornerShape(12.dp)).clickable { onClick() }.padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(icon, null, tint = if (isSelected) ColorAccent else ColorTextMuted, modifier = Modifier.size(20.dp))
            Text(label, color = if (isSelected) ColorText else ColorTextMuted, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium)
        }
    }
}

// ─── Car Details ──────────────────────────────────────────────────────────────

@Composable
fun ParcelCarDetailsScreen(
    uiState: CreateParcelTripUiState,
    paddingValues: PaddingValues,
    onEvent: (CreateParcelTripEvent) -> Unit,
    onContinue: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(paddingValues).verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        if (uiState.savedCars.isNotEmpty()) {
            Text("Saved Vehicles", color = ColorTextMuted, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                items(uiState.savedCars.size) { index ->
                    val car = uiState.savedCars[index]
                    Row(modifier = Modifier.clip(RoundedCornerShape(14.dp)).background(ColorSurface).clickable { onEvent(CreateParcelTripEvent.LoadSavedCarDetails(car)) }.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(Icons.Outlined.DirectionsCar, null, tint = ColorAccent, modifier = Modifier.size(20.dp))
                        Column {
                            Text(car.model, color = ColorAccent, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(car.licensePlate, color = ColorTextMuted, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
        FormCard {
            FormSection(title = "Vehicle Info") {
                GreenOutlinedField("Car Model", uiState.carModel, Icons.Outlined.DirectionsCar, "e.g. Honda City", KeyboardOptions(capitalization = KeyboardCapitalization.Words)) { onEvent(CreateParcelTripEvent.CarModelChanged(it)) }
                Spacer(Modifier.height(10.dp))
                GreenOutlinedField("License Plate", uiState.licensePlate, Icons.Outlined.Badge, "e.g. KA01AB1234", KeyboardOptions(capitalization = KeyboardCapitalization.Characters)) { onEvent(CreateParcelTripEvent.LicensePlateChanged(it.uppercase())) }
                Spacer(Modifier.height(10.dp))
                GreenOutlinedField("Color", uiState.carColor, Icons.Outlined.Palette, "e.g. White", KeyboardOptions(capitalization = KeyboardCapitalization.Words)) { onEvent(CreateParcelTripEvent.CarColorChanged(it)) }
                Spacer(Modifier.height(10.dp))
                var showCarTypePicker by remember { mutableStateOf(false) }
                PickerRow(
                    icon = Icons.Outlined.Category,
                    label = "Car Type",
                    value = uiState.carType.ifEmpty { "Select type" },
                    hasValue = uiState.carType.isNotEmpty(),
                    onClick = { showCarTypePicker = true }
                )

                if (showCarTypePicker) {
                    CarTypePickerDialog(
                        currentType = uiState.carType,
                        onDismiss = { showCarTypePicker = false },
                        onTypeSelected = { 
                            onEvent(CreateParcelTripEvent.CarTypeChanged(it.lowercase()))
                            showCarTypePicker = false 
                        }
                    )
                }
            }
        }
        GreenButton(text = "Continue to Review", enabled = uiState.carModel.isNotEmpty() && uiState.licensePlate.isNotEmpty() && uiState.carColor.isNotEmpty() && uiState.carType.isNotEmpty(), onClick = onContinue)
    }
}

// ─── Review Screen ────────────────────────────────────────────────────────────

@Composable
fun ParcelReviewScreen(
    uiState: CreateParcelTripUiState,
    paddingValues: PaddingValues,
    onCreateTrip: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(paddingValues).verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Header banner
        Row(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp))
                .background(brush = androidx.compose.ui.graphics.Brush.linearGradient(listOf(ColorAccentDim, ColorCard)))
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(ColorAccent.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.CheckCircle, null, tint = ColorAccent, modifier = Modifier.size(28.dp))
            }
            Column {
                Text("Ready to Ship!", color = ColorText, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Text("Review your parcel trip details", color = ColorTextMuted, fontSize = 13.sp)
            }
        }

        // Review Sections
        ReviewSection("Route Details", Icons.Outlined.Route, {}) {
            ReviewRow(Icons.Default.TripOrigin, "From", uiState.startPointName, ColorAccent)
            ReviewRow(Icons.Default.LocationOn, "To", uiState.endPointName, ColorAmber)
        }

        ReviewSection("Trip & Space", Icons.Outlined.CalendarToday, {}) {
            ReviewRow(Icons.Outlined.DateRange, "Date", uiState.tripDate, ColorAccent)
            ReviewRow(Icons.Outlined.Schedule, "Time", uiState.tripTime, ColorAccent)
            val sizeDisplay = uiState.availableSpace.replace("_", " ").replaceFirstChar { it.uppercase() }
            ReviewRow(Icons.Outlined.Inventory2, "Size", sizeDisplay, ColorBlue)
            ReviewRow(Icons.Outlined.CurrencyRupee, "Price", "₹${uiState.costPerKg}/Kg", ColorAmber)
        }

        if (uiState.isPersonalCar) {
            ReviewSection("Vehicle Info", Icons.Outlined.DirectionsCar, {}) {
                ReviewRow(Icons.Outlined.DirectionsCar, "Model", uiState.carModel, ColorTextMuted)
                ReviewRow(Icons.Outlined.Badge, "Plate", uiState.licensePlate, ColorTextMuted)
                ReviewRow(Icons.Outlined.Palette, "Color", uiState.carColor, ColorTextMuted)
                ReviewRow(Icons.Outlined.Category, "Type", uiState.carType.replaceFirstChar { it.uppercase() }, ColorTextMuted)
            }
        } else {
            ReviewSection("Transport Info", Icons.Outlined.Business, {}) {
                ReviewRow(Icons.Outlined.Business, "Operator", uiState.operatorName, ColorTextMuted)
                ReviewRow(Icons.Outlined.Category, "Type", uiState.transportType.uppercase(), ColorTextMuted)
            }
        }

        Spacer(Modifier.height(16.dp))
        
        GreenButton(
            text = if (uiState.isCreatingTrip) "Publishing..." else "Publish Parcel Trip",
            isLoading = uiState.isCreatingTrip,
            icon = Icons.Default.Check,
            onClick = onCreateTrip
        )
        
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(ColorCard).padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(Icons.Outlined.Info, null, tint = ColorTextMuted, modifier = Modifier.size(14.dp))
            Text("By publishing, you commit to carrying the parcel securely as per the trip details.", color = ColorTextMuted, fontSize = 11.sp)
        }
    }
}
