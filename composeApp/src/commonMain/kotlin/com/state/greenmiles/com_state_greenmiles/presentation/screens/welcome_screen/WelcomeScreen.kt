package com.state.greenmiles.com_state_greenmiles.presentation.screens.welcome_screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.state.greenmiles.com_state_greenmiles.presentation.navigation.AppScreen
import com.state.greenmiles.com_state_greenmiles.presentation.common.AuthViewModel
import com_state_greenmiles.composeapp.generated.resources.Res
import com_state_greenmiles.composeapp.generated.resources.taxi_logo
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

// ─── Design Tokens ────────────────────────────────────────────────────────────

private val ColorBackground = Color(0xFF0D1A12)
private val ColorSurface    = Color(0xFF142B1C)
private val ColorCard       = Color(0xFF1C3826)
private val ColorBorder     = Color(0xFF2E5C3A)
private val ColorAccent     = Color(0xFF4ADE80)
private val ColorAccentDim  = Color(0xFF166534)
private val ColorText       = Color(0xFFF0FDF4)
private val ColorTextMuted  = Color(0xFF86EFAC)

// ─── Screen ───────────────────────────────────────────────────────────────────

@Preview
@Composable
fun WelcomeScreen(
    navController: NavHostController,
    viewModel: AuthViewModel
) {
    // Ambient glow pulse
    val pulse = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by pulse.animateFloat(
        initialValue = 0.12f, targetValue = 0.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ), label = "pa"
    )

    // Subtle logo float
    val floatAnim by pulse.animateFloat(
        initialValue = 0f, targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ), label = "float"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBackground)
    ) {
        // Top-right glow orb
        Box(
            modifier = Modifier
                .size(320.dp)
                .offset(x = 80.dp, y = (-80).dp)
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
                .size(280.dp)
                .offset(x = (-60).dp, y = 60.dp)
                .align(Alignment.BottomStart)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(ColorAccentDim.copy(alpha = 0.45f), Color.Transparent)
                    ),
                    shape = RoundedCornerShape(50)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ── Logo container ──
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .offset(y = floatAnim.dp)
                    .clip(RoundedCornerShape(40.dp))
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(ColorCard, ColorSurface)
                        )
                    )
                    .padding(28.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(Res.drawable.taxi_logo),
                    contentDescription = "GreenMiles logo",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(Modifier.height(40.dp))

            // ── Brand name ──
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(ColorCard)
                    .padding(horizontal = 16.dp, vertical = 7.dp)
            ) {
                Text(
                    text = "🌿  GreenMiles",
                    color = ColorAccent,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.8.sp
                )
            }

            Spacer(Modifier.height(20.dp))

            // ── Tagline ──
            Text(
                text = "Let's travel\ntogether.",
                color = ColorText,
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 48.sp,
                letterSpacing = (-0.5).sp
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = "Sustainable rides, brighter miles.",
                color = ColorTextMuted,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal
            )

            Spacer(Modifier.height(64.dp))

            // ── Sign Up (primary) ──
            Button(
                onClick = {
                    viewModel.authType = "SIGNUP"
                    navController.navigate("${AppScreen.PhoneNumberScreen.name}/SIGNUP")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(ColorAccent, Color(0xFF22C55E))
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Get Started",
                        color = Color(0xFF052E16),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.3.sp
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // ── Log In (ghost) ──
            Button(
                onClick = {
                    viewModel.authType = "LOGIN"
                    navController.navigate("${AppScreen.PhoneNumberScreen.name}/LOGIN")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent)
                        .clip(RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Outlined ghost effect via border
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp))
                            .background(ColorSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "I already have an account",
                            color = ColorAccent,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.2.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(40.dp))

            // ── Footer note ──
            Text(
                text = "By continuing you agree to our Terms & Privacy Policy",
                color = ColorTextMuted.copy(alpha = 0.5f),
                fontSize = 12.sp
            )
        }
    }
}