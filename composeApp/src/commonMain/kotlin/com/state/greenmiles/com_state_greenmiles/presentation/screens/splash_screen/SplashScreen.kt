package com.state.greenmiles.com_state_greenmiles.presentation.screens.splash_screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.state.greenmiles.com_state_greenmiles.presentation.navigation.AppScreen
import com_state_greenmiles.composeapp.generated.resources.Res
import com_state_greenmiles.composeapp.generated.resources.logo
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

// ─── Design Tokens ────────────────────────────────────────────────────────────

private val ColorBackground = Color(0xFF0D1A12)
private val ColorSurface    = Color(0xFF142B1C)
private val ColorCard       = Color(0xFF1C3826)
private val ColorAccent     = Color(0xFF4ADE80)
private val ColorAccentDim  = Color(0xFF166534)
private val ColorText       = Color(0xFFF0FDF4)
private val ColorTextMuted  = Color(0xFF86EFAC)

// ─── Screen ───────────────────────────────────────────────────────────────────

@Composable
fun SplashScreen(
    navController: NavHostController,
    splashViewmodel: SplashViewmodel = koinViewModel()
) {
    // ── Entrance animations ──
    val logoScale = remember { Animatable(0.6f) }
    val logoAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val taglineAlpha = remember { Animatable(0f) }

    // Ambient pulse (runs after entrance)
    val pulse = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by pulse.animateFloat(
        initialValue = 0.10f, targetValue = 0.22f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ), label = "pa"
    )
    val pulseScale by pulse.animateFloat(
        initialValue = 1f, targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ), label = "ps"
    )

    LaunchedEffect(Unit) {
        // Staggered entrance
        logoScale.animateTo(1f, animationSpec = spring(dampingRatio = 0.5f, stiffness = 200f))
        logoAlpha.animateTo(1f, animationSpec = tween(400))
        textAlpha.animateTo(1f, animationSpec = tween(500))
        delay(200)
        taglineAlpha.animateTo(1f, animationSpec = tween(500))

        // Hold, then navigate
        delay(1400)
        if (splashViewmodel.checkIfTokenPresent()) {
            navController.navigate(AppScreen.HomeScreen.name) { popUpTo(0) }
        } else {
            navController.navigate(AppScreen.WelcomeScreen.name) { popUpTo(0) }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBackground),
        contentAlignment = Alignment.Center
    ) {
        // Top-right glow orb
        Box(
            modifier = Modifier
                .size(350.dp)
                .offset(x = 100.dp, y = (-120).dp)
                .align(Alignment.TopEnd)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(ColorAccent.copy(alpha = pulseAlpha), Color.Transparent)
                    ),
                    shape = RoundedCornerShape(50)
                )
        )
        // Bottom-left glow orb
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-80).dp, y = 80.dp)
                .align(Alignment.BottomStart)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(ColorAccentDim.copy(alpha = 0.45f), Color.Transparent)
                    ),
                    shape = RoundedCornerShape(50)
                )
        )

        // Center content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo card with glow ring
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .scale(logoScale.value * pulseScale)
                    .alpha(logoAlpha.value),
                contentAlignment = Alignment.Center
            ) {
                // Glow ring behind logo
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(RoundedCornerShape(36.dp))
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    ColorAccent.copy(alpha = 0.18f),
                                    ColorCard
                                )
                            )
                        )
                )
                // Logo
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(ColorCard)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(Res.drawable.logo),
                        contentDescription = "GreenMiles",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            // Brand name
            Text(
                text = "GreenMiles",
                color = ColorText,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp,
                modifier = Modifier.alpha(textAlpha.value)
            )

            Spacer(Modifier.height(8.dp))

            // Tagline
            Text(
                text = "Sustainable rides, brighter miles.",
                color = ColorTextMuted,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 0.2.sp,
                modifier = Modifier.alpha(taglineAlpha.value)
            )
        }

        // Loading dot indicator at bottom
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 56.dp)
                .alpha(taglineAlpha.value),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(3) { index ->
                val dotPulse = rememberInfiniteTransition(label = "dot$index")
                val dotAlpha by dotPulse.animateFloat(
                    initialValue = 0.2f, targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(600, easing = EaseInOutSine),
                        repeatMode = RepeatMode.Reverse,
                        initialStartOffset = StartOffset(index * 180)
                    ), label = "da$index"
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(ColorAccent.copy(alpha = dotAlpha))
                )
            }
        }
    }
}