package com.state.greenmiles.com_state_greenmiles.presentation.screens.parcel.car

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.state.greenmiles.com_state_greenmiles.presentation.common.components.ColorBackground
import com.state.greenmiles.com_state_greenmiles.presentation.common.components.ColorSurface
import com.state.greenmiles.com_state_greenmiles.presentation.common.components.LocationSearchDialog
import com.state.greenmiles.com_state_greenmiles.presentation.screens.parcel.*
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCarParcelScreen(
    viewModel: CreateParcelTripViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
    onTripCreated: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showStartLocationSearch by remember { mutableStateOf(false) }
    var showEndLocationSearch   by remember { mutableStateOf(false) }

    // Ensure we are in car mode
    LaunchedEffect(Unit) {
        viewModel.onEvent(CreateParcelTripEvent.SelectParcelType(true))
    }

    LaunchedEffect(uiState.tripCreated) {
        if (uiState.tripCreated != null) {
            onTripCreated()
            viewModel.onEvent(CreateParcelTripEvent.ResetForm)
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Publish Car Parcel", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
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
                ParcelStepProgressBar(currentStep = uiState.currentStep, isPersonal = true)
            }
        },
        containerColor = ColorBackground
    ) { paddingValues ->
        when (uiState.currentStep) {
            CreateParcelStep.LOCATION_SELECTION -> LocationSelectionScreen(
                startPointName = uiState.startPointName,
                endPointName = uiState.endPointName,
                startCoordinate = uiState.startCoordinate,
                endCoordinate = uiState.endCoordinate,
                isLoadingRoutes = uiState.isLoadingRoutes,
                routesError = uiState.routesError,
                paddingValues = paddingValues,
                onShowStartLocationSearch = { showStartLocationSearch = true },
                onShowEndLocationSearch = { showEndLocationSearch = true },
                onFetchRoutes = { viewModel.onEvent(CreateParcelTripEvent.FetchRoutes) }
            )
            CreateParcelStep.ROUTE_SELECTION -> ParcelRouteSelectionScreen(
                uiState = uiState,
                paddingValues = paddingValues,
                onRouteSelected = { viewModel.onEvent(CreateParcelTripEvent.RouteSelected(it)) },
                onContinue = { viewModel.onEvent(CreateParcelTripEvent.NextStep) }
            )
            CreateParcelStep.TRIP_DETAILS -> ParcelDetailsForm(
                uiState = uiState,
                paddingValues = paddingValues,
                onEvent = { viewModel.onEvent(it) },
                onContinue = { viewModel.onEvent(CreateParcelTripEvent.NextStep) }
            )
            CreateParcelStep.CAR_DETAILS -> ParcelCarDetailsScreen(
                uiState = uiState,
                paddingValues = paddingValues,
                onEvent = { viewModel.onEvent(it) },
                onContinue = { viewModel.onEvent(CreateParcelTripEvent.NextStep) }
            )
            CreateParcelStep.REVIEW -> ParcelReviewScreen(
                uiState = uiState,
                paddingValues = paddingValues,
                onCreateTrip = { viewModel.onEvent(CreateParcelTripEvent.CreateTrip) }
            )
            else -> {}
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
