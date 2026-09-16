package com.state.greenmiles.com_state_greenmiles.presentation.screens.choice

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
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
fun ParcelTypeSelectionScreen(
    onPublicSelected: () -> Unit,
    onCarSelected: () -> Unit,
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
                text = "Parcel Delivery Method",
                color = ColorText,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "How are you travelling today?",
                color = ColorTextMuted,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 48.dp)
            )

            SelectionCard(
                title = "Public Transport",
                description = "I'm travelling via Bus, Train or Flight and can carry a small parcel.",
                icon = Icons.Default.DirectionsBus,
                onClick = onPublicSelected
            )

            Spacer(Modifier.height(20.dp))

            SelectionCard(
                title = "Personal Vehicle",
                description = "I am driving my own car and have extra space for parcels.",
                icon = Icons.Default.DirectionsCar,
                onClick = onCarSelected
            )
        }
    }
}
