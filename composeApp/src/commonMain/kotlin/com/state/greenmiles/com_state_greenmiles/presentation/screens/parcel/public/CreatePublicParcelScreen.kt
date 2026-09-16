package com.state.greenmiles.com_state_greenmiles.presentation.screens.parcel.public

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.state.greenmiles.com_state_greenmiles.domain.model.trip.Coordinate
import com.state.greenmiles.com_state_greenmiles.presentation.common.components.*
import com.state.greenmiles.com_state_greenmiles.presentation.screens.parcel.*
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePublicParcelScreen(
    viewModel: CreateParcelTripViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
    onTripCreated: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showStartLocationSearch by remember { mutableStateOf(false) }
    var showEndLocationSearch   by remember { mutableStateOf(false) }

    // Ensure we are in public mode
    LaunchedEffect(Unit) {
        viewModel.onEvent(CreateParcelTripEvent.SelectParcelType(false))
    }

    LaunchedEffect(uiState.tripCreated) {
        if (uiState.tripCreated != null) {
            onTripCreated()
            viewModel.onEvent(CreateParcelTripEvent.ResetForm)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Publish Public Parcel", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (uiState.currentStep == CreateParcelStep.LOCATION_SELECTION) onNavigateBack()
                        else viewModel.onEvent(CreateParcelTripEvent.PreviousStep)
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ColorSurface)
            )
        },
        containerColor = ColorBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            PublicParcelProgressBar(uiState.currentStep)

            // Use weight(1f) to give bounded height to scrollable sub-composables
            Box(modifier = Modifier.weight(1f)) {
                when (uiState.currentStep) {
                    CreateParcelStep.LOCATION_SELECTION -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text("Where are you going?", color = ColorText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            LocationPickerButton(
                                label = "Starting point",
                                value = uiState.startPointName.ifEmpty { "Select origin" },
                                icon = Icons.Default.TripOrigin,
                                iconTint = ColorAccent,
                                onClick = { showStartLocationSearch = true }
                            )
                            LocationPickerButton(
                                label = "Destination",
                                value = uiState.endPointName.ifEmpty { "Select destination" },
                                icon = Icons.Default.LocationOn,
                                iconTint = ColorAmber,
                                onClick = { showEndLocationSearch = true }
                            )
                            Spacer(Modifier.height(10.dp))
                            GreenButton(
                                text = "Continue",
                                enabled = uiState.startCoordinate != null && uiState.endCoordinate != null,
                                onClick = { viewModel.onEvent(CreateParcelTripEvent.NextStep) }
                            )
                        }
                    }
                    
                    CreateParcelStep.TRIP_DETAILS -> {
                        ParcelDetailsForm(
                            uiState = uiState,
                            paddingValues = PaddingValues(0.dp),
                            onEvent = { viewModel.onEvent(it) },
                            onContinue = { viewModel.onEvent(CreateParcelTripEvent.NextStep) }
                        )
                    }

                    CreateParcelStep.REVIEW -> {
                        ParcelReviewScreen(
                            uiState = uiState,
                            paddingValues = PaddingValues(0.dp),
                            onCreateTrip = { viewModel.onEvent(CreateParcelTripEvent.CreateTrip) }
                        )
                    }
                    else -> { /* Handle unexpected steps */ }
                }
            }
        }
    }

    if (showStartLocationSearch) {
        LocationSearchDialog(
            onDismiss = { showStartLocationSearch = false },
            onLocationSelected = { name, coord ->
                viewModel.onEvent(CreateParcelTripEvent.StartLocationSelected(name, coord))
                showStartLocationSearch = false
            }
        )
    }
    if (showEndLocationSearch) {
        LocationSearchDialog(
            onDismiss = { showEndLocationSearch = false },
            onLocationSelected = { name, coord ->
                viewModel.onEvent(CreateParcelTripEvent.EndLocationSelected(name, coord))
                showEndLocationSearch = false
            }
        )
    }
}

@Composable
fun PublicParcelProgressBar(currentStep: CreateParcelStep) {
    val steps = listOf(CreateParcelStep.LOCATION_SELECTION, CreateParcelStep.TRIP_DETAILS, CreateParcelStep.REVIEW)
    val currentIndex = steps.indexOf(currentStep)

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, step ->
            val isDone = index < currentIndex
            val isCurrent = index == currentIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .background(if (isDone || isCurrent) ColorAccent else ColorBorder)
            )
        }
    }
}
