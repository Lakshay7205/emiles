package com.state.greenmiles.com_state_greenmiles.presentation.screens.home_Screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.state.greenmiles.com_state_greenmiles.presentation.navigation.BottomNavBar
import com.state.greenmiles.com_state_greenmiles.presentation.navigation.NavDestination
import com_state_greenmiles.composeapp.generated.resources.Res
import com_state_greenmiles.composeapp.generated.resources.taxi_logo
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

// ─── Design Tokens ────────────────────────────────────────────────────────────

val ColorBackground = Color(0xFF0D1A12)
val ColorSurface    = Color(0xFF142B1C)
val ColorCard       = Color(0xFF1C3826)
private val ColorBorder     = Color(0xFF2E5C3A)
val ColorAccent     = Color(0xFF4ADE80)
private val ColorAccentDim  = Color(0xFF166534)
val ColorText       = Color(0xFFF0FDF4)
val ColorTextMuted  = Color(0xFF86EFAC)

@OptIn(ExperimentalComposeUiApi::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    userName: String = "",
    onFindRide: () -> Unit = {},
    onPublishRide: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onTripsClick: () -> Unit = {},
    onSearchParcels: () -> Unit = {},
    onPublishParcel: () -> Unit = {},
    onPublishPublicParcel: () -> Unit = {},
    onPublishCarParcel: () -> Unit = {},
    onServiceSelection: () -> Unit = {}
) {
    Scaffold(
        containerColor = ColorBackground,
        bottomBar = {
            BottomNavBar(
                selected = NavDestination.HOME,
                onSelect = { destination ->
                    when (destination) {
                        NavDestination.HOME -> { /* already here */ }
                        NavDestination.TRIPS -> onTripsClick()
                        NavDestination.PROFILE -> onProfileClick()
                    }
                }
            )
        }
    ) { innerPadding ->
        HomeContent(
            userName = userName,
            onFindRide = onFindRide,
            onPublishRide = onPublishRide,
            onSearchParcels = onSearchParcels,
            onPublishParcel = onPublishParcel,
            onPublishPublicParcel = onPublishPublicParcel,
            onPublishCarParcel = onPublishCarParcel,
            onServiceSelection = onServiceSelection,
            bottomPadding = innerPadding.calculateBottomPadding()
        )
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun HomeContent(
    userName: String,
    onFindRide: () -> Unit,
    onPublishRide: () -> Unit,
    onSearchParcels: () -> Unit,
    onPublishParcel: () -> Unit,
    onPublishPublicParcel: () -> Unit,
    onPublishCarParcel: () -> Unit,
    onServiceSelection: () -> Unit,
    bottomPadding: Dp
) {
    val pagerState = androidx.compose.foundation.pager.rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()

    val pulse = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by pulse.animateFloat(
        initialValue = 0.12f, targetValue = 0.24f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ), label = "pa"
    )
    val floatAnim by pulse.animateFloat(
        initialValue = 0f, targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(3400, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ), label = "fl"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBackground)
            .padding(bottom = bottomPadding)
    ) {
        // Background Orbs
        Box(
            modifier = Modifier
                .size(340.dp)
                .offset(x = 90.dp, y = (-70).dp)
                .align(Alignment.TopEnd)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(ColorAccent.copy(alpha = pulseAlpha), Color.Transparent)
                    ),
                    shape = RoundedCornerShape(50)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Spacer(Modifier.height(56.dp))

            // ── Top bar ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Good day,",
                        color = ColorTextMuted,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal
                    )
                    Text(
                        text = userName.ifBlank { "Traveller" },
                        color = ColorText,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Mode Toggle ──
            HomeModeToggle(
                selectedPageIndex = pagerState.currentPage,
                onPageSelected = { index ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )

            Spacer(Modifier.height(24.dp))

            // ── Main Content Pager ──
            androidx.compose.foundation.pager.HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.Top
            ) { pageIndex ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (pageIndex) {
                        0 -> RidePageContent(
                            floatAnim = floatAnim,
                            onFindRide = onFindRide,
                            onPublishRide = onPublishRide,
                            onServiceSelection = onServiceSelection
                        )
                        1 -> ParcelPageContent(
                            floatAnim = floatAnim,
                            onSearchParcels = onSearchParcels,
                            onPublishParcel = onPublishParcel,
                            onServiceSelection = onServiceSelection
                        )
                    }
                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun HomeModeToggle(
    selectedPageIndex: Int,
    onPageSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 28.dp)
            .fillMaxWidth()
            .height(54.dp)
            .clip(RoundedCornerShape(27.dp))
            .background(ColorCard)
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val rideSelected = selectedPageIndex == 0
        
        // Rides Tab
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(23.dp))
                .background(if (rideSelected) ColorAccent else Color.Transparent)
                .clickable { onPageSelected(0) },
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(
                    Icons.Outlined.DirectionsCar, 
                    null, 
                    tint = if (rideSelected) Color(0xFF052E16) else ColorTextMuted,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    "Rides", 
                    color = if (rideSelected) Color(0xFF052E16) else ColorText,
                    fontWeight = if (rideSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
        }

        // Parcels Tab
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(23.dp))
                .background(if (!rideSelected) ColorAccent else Color.Transparent)
                .clickable { onPageSelected(1) },
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(
                    Icons.Outlined.LocalShipping, 
                    null, 
                    tint = if (!rideSelected) Color(0xFF052E16) else ColorTextMuted,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    "Parcels", 
                    color = if (!rideSelected) Color(0xFF052E16) else ColorText,
                    fontWeight = if (!rideSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun RidePageContent(
    floatAnim: Float,
    onFindRide: () -> Unit,
    onPublishRide: () -> Unit,
    onServiceSelection: () -> Unit
) {
    // Illustration card
    ServiceIllustrationCard(
        imageRes = Res.drawable.taxi_logo,
        floatAnim = floatAnim
    )

    Spacer(Modifier.height(32.dp))

    Text(
        text = "Where to?",
        color = ColorText,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
    )

    MainActionButton(
        text = "Find a Ride",
        icon = Icons.Outlined.Search,
        onClick = onFindRide
    )

    Spacer(Modifier.height(16.dp))

    SecondaryActionButton(
        text = "Publish a Ride",
        icon = Icons.Outlined.AddCircleOutline,
        onClick = onPublishRide
    )
}

@Composable
private fun ParcelPageContent(
    floatAnim: Float,
    onSearchParcels: () -> Unit,
    onPublishParcel: () -> Unit,
    onServiceSelection: () -> Unit
) {
    // Illustration card (could use a different one if available)
    ServiceIllustrationCard(
        imageRes = Res.drawable.taxi_logo, // Use parcel specific if available
        floatAnim = floatAnim
    )

    Spacer(Modifier.height(32.dp))

    Text(
        text = "Send a Package?",
        color = ColorText,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
    )

    MainActionButton(
        text = "Search Parcel Trips",
        icon = Icons.Outlined.LocalShipping,
        onClick = onSearchParcels
    )

    Spacer(Modifier.height(16.dp))

    SecondaryActionButton(
        text = "Publish Parcel Trip",
        icon = Icons.Outlined.PostAdd,
        onClick = onPublishParcel
    )
}

@Composable
private fun ServiceIllustrationCard(
    imageRes: org.jetbrains.compose.resources.DrawableResource,
    floatAnim: Float
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(ColorCard, ColorSurface)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(ColorAccent.copy(alpha = 0.08f), Color.Transparent)
                    ),
                    shape = RoundedCornerShape(50)
                )
        )
        Image(
            painter = painterResource(imageRes),
            contentDescription = null,
            modifier = Modifier
                .size(200.dp)
                .offset(y = floatAnim.dp)
        )
    }
}

@Composable
private fun MainActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.horizontalGradient(listOf(ColorAccent, Color(0xFF22C55E))),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, null, tint = Color(0xFF052E16), modifier = Modifier.size(20.dp))
            Text(text, color = Color(0xFF052E16), fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SecondaryActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(ColorSurface)
            .border(BorderStroke(1.dp, ColorBorder), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(icon, null, tint = ColorAccent, modifier = Modifier.size(20.dp))
            Text(text, color = ColorText, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}