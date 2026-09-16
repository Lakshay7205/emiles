package com.state.greenmiles.com_state_greenmiles.presentation.screens.choice

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── Design Tokens ────────────────────────────────────────────────────────────

private val ColorBackground = Color(0xFF0D1A12)
private val ColorSurface    = Color(0xFF142B1C)
private val ColorCard       = Color(0xFF1C3826)
private val ColorBorder     = Color(0xFF2E5C3A)
private val ColorAccent     = Color(0xFF4ADE80)
private val ColorText       = Color(0xFFF0FDF4)
private val ColorTextMuted  = Color(0xFF86EFAC)

@Composable
fun ServiceSelectionScreen(
    onRideSelected: () -> Unit,
    onParcelSelected: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        containerColor = ColorBackground,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 52.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(ColorSurface)
                ) {
                    Icon(Icons.Default.ArrowBack, null, tint = ColorText)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "What would you like to do?",
                color = ColorText,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Choose a service to continue",
                color = ColorTextMuted,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 48.dp)
            )

            SelectionCard(
                title = "Transport People",
                description = "Offer or find a ride for your daily commute or long trips.",
                icon = Icons.Default.DirectionsCar,
                onClick = onRideSelected
            )

            Spacer(Modifier.height(20.dp))

            SelectionCard(
                title = "Deliver Parcels",
                description = "Carry a parcel on your trip or find someone to deliver yours.",
                icon = Icons.Default.LocalShipping,
                onClick = onParcelSelected
            )
        }
    }
}

@Composable
fun SelectionCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(ColorSurface)
            .border(1.dp, ColorBorder, RoundedCornerShape(32.dp))
            .clickable { onClick() }
            .padding(24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(ColorCard),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ColorAccent,
                    modifier = Modifier.size(32.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = ColorText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    color = ColorTextMuted,
                    fontSize = 14.sp
                )
            }
        }
    }
}
