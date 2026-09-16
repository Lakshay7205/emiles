package com.state.greenmiles.com_state_greenmiles.presentation.screens.signup_screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.state.greenmiles.com_state_greenmiles.presentation.navigation.AppScreen
import com.state.greenmiles.com_state_greenmiles.presentation.common.AuthViewModel
import com.state.greenmiles.com_state_greenmiles.presentation.screens.states.CompleteRegistrationState
import com.state.greenmiles.com_state_greenmiles.presentation.screens.states.SendOtpState

// ─── Design Tokens ──────────────────────────────────────────────────────────────

private val ColorBackground   = Color(0xFF0D1A12)   // deep forest night
private val ColorSurface      = Color(0xFF142B1C)   // dark leaf surface
private val ColorCard         = Color(0xFF1C3826)   // elevated card
private val ColorBorder       = Color(0xFF2E5C3A)   // muted green border
private val ColorAccent       = Color(0xFF4ADE80)   // vivid spring green
private val ColorAccentDim    = Color(0xFF166534)   // deep accent for gradient end
private val ColorText         = Color(0xFFF0FDF4)   // near-white text
private val ColorTextMuted    = Color(0xFF86EFAC)   // soft green muted
private val ColorError        = Color(0xFFFF6B6B)   // warm red error

// ─── Screen ─────────────────────────────────────────────────────────────────────

@Composable
fun SignUpScreen(
    navController: NavHostController,
    viewModel: AuthViewModel,
    verifiedToken: String
) {
    var fullName  by remember { mutableStateOf("") }
    var email     by remember { mutableStateOf("") }
    var password  by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val registerState by viewModel.completeRegistrationState.collectAsState()

    LaunchedEffect(registerState) {
        if (registerState is CompleteRegistrationState.Success) {
            navController.navigate(AppScreen.HomeScreen.name) {
                popUpTo(AppScreen.SignUp.name) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBackground)
    ) {
        // Decorative glow orb (top-right)
        Box(
            modifier = Modifier
                .size(320.dp)
                .offset(x = 100.dp, y = (-60).dp)
                .align(Alignment.TopEnd)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            ColorAccent.copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(50)
                )
        )

        // Decorative glow orb (bottom-left)
        Box(
            modifier = Modifier
                .size(250.dp)
                .offset(x = (-80).dp, y = 80.dp)
                .align(Alignment.BottomStart)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            ColorAccentDim.copy(alpha = 0.5f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(50)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(Modifier.height(72.dp))

            // ── Brand chip ──
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(ColorCard)
                    .padding(horizontal = 14.dp, vertical = 6.dp)
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

            // ── Heading ──
            Text(
                text = "Complete your\nprofile.",
                color = ColorText,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 46.sp,
                letterSpacing = (-0.5).sp
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Just a few more details to get started",
                color = ColorTextMuted,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal
            )

            Spacer(Modifier.height(40.dp))

            // ── Form card ──
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(ColorSurface)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                GreenInputField(
                    label = "Full Name",
                    value = fullName,
                    onValueChange = { fullName = it },
                    icon = Icons.Outlined.Person,
                    keyboardType = KeyboardType.Text
                )
                GreenInputField(
                    label = "Email address",
                    value = email,
                    onValueChange = { email = it },
                    icon = Icons.Outlined.Email,
                    keyboardType = KeyboardType.Email
                )
                GreenInputField(
                    label = "Password",
                    value = password,
                    onValueChange = { password = it },
                    icon = Icons.Outlined.Lock,
                    keyboardType = KeyboardType.Password,
                    isPassword = true,
                    passwordVisible = passwordVisible,
                    onPasswordToggle = { passwordVisible = !passwordVisible }
                )
            }

            Spacer(Modifier.height(8.dp))

            // ── Error messages ──
            AnimatedVisibility(visible = registerState is CompleteRegistrationState.Error) {
                ErrorBanner((registerState as? CompleteRegistrationState.Error)?.message ?: "")
            }

            Spacer(Modifier.height(24.dp))

            // ── CTA Button ──
            val isLoading = registerState is CompleteRegistrationState.Loading

            GreenPrimaryButton(
                text = "Create Account",
                isLoading = isLoading,
                onClick = {
                    viewModel.completeRegistration(
                        name = fullName,
                        email = email,
                        password = password,
                        verifiedToken = verifiedToken
                    )
                }
            )

            Spacer(Modifier.height(20.dp))

            // ── Login redirect ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Already have an account? ", color = ColorTextMuted, fontSize = 14.sp)
                TextButton(
                    onClick = { navController.popBackStack() },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Log in", color = ColorAccent, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

// ─── Components ──────────────────────────────────────────────────────────────────

@Composable
private fun GreenInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    keyboardType: KeyboardType,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordToggle: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 13.sp) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ColorAccent.copy(alpha = 0.8f),
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = { onPasswordToggle?.invoke() }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Outlined.VisibilityOff
                        else Icons.Outlined.Visibility,
                        contentDescription = null,
                        tint = ColorTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        } else null,
        visualTransformation = if (isPassword && !passwordVisible)
            PasswordVisualTransformation() else VisualTransformation.None,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor      = ColorAccent,
            unfocusedBorderColor    = ColorBorder,
            focusedLabelColor       = ColorAccent,
            unfocusedLabelColor     = ColorTextMuted,
            focusedTextColor        = ColorText,
            unfocusedTextColor      = ColorText,
            cursorColor             = ColorAccent,
            focusedContainerColor   = ColorCard,
            unfocusedContainerColor = ColorCard
        )
    )
}

@Composable
private fun GreenPrimaryButton(
    text: String,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = if (!isLoading)
                        Brush.horizontalGradient(listOf(ColorAccent, Color(0xFF22C55E)))
                    else
                        Brush.horizontalGradient(listOf(ColorBorder, ColorBorder)),
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = ColorAccent,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.5.dp
                )
            } else {
                Text(
                    text = text,
                    color = Color(0xFF052E16),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.3.sp
                )
            }
        }
    }
}

@Composable
private fun ErrorBanner(message: String) {
    Spacer(Modifier.height(4.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ColorError.copy(alpha = 0.12f))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.ErrorOutline,
            contentDescription = null,
            tint = ColorError,
            modifier = Modifier.size(16.dp)
        )
        Text(text = message, color = ColorError, fontSize = 13.sp)
    }
    Spacer(Modifier.height(4.dp))
}