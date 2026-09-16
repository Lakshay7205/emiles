package com.state.greenmiles.com_state_greenmiles.presentation.screens.phone_number_screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.state.greenmiles.com_state_greenmiles.presentation.navigation.AppScreen
import com.state.greenmiles.com_state_greenmiles.presentation.common.AuthViewModel
import com.state.greenmiles.com_state_greenmiles.presentation.screens.states.LoginState
import com.state.greenmiles.com_state_greenmiles.presentation.screens.states.SendOtpState
import com.state.greenmiles.com_state_greenmiles.presentation.screens.states.VerifyRegisterOtpState
import kotlinx.coroutines.launch

// ─── Design Tokens (shared with SignUpScreen) ────────────────────────────────

private val ColorBackground   = Color(0xFF0D1A12)
private val ColorSurface      = Color(0xFF142B1C)
private val ColorCard         = Color(0xFF1C3826)
private val ColorBorder       = Color(0xFF2E5C3A)
private val ColorAccent       = Color(0xFF4ADE80)
private val ColorAccentDim    = Color(0xFF166534)
private val ColorText         = Color(0xFFF0FDF4)
private val ColorTextMuted    = Color(0xFF86EFAC)
private val ColorError        = Color(0xFFFF6B6B)

// ─── Screen ──────────────────────────────────────────────────────────────────

@Composable
fun PhoneNumberScreen(
    navController: NavHostController,
    viewModel: AuthViewModel
) {
    val scope = rememberCoroutineScope()

    val authType = viewModel.authType ?: ""
    val isLogin  = authType == "LOGIN"

    var phoneNumber     by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var otp             by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val sendOtpState   by viewModel.sendOtpState.collectAsState()
    val verifyOtpState by viewModel.verifyRegisterOtpState.collectAsState()
    val loginState     by viewModel.loginState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val isOtpSent = sendOtpState is SendOtpState.Success
    val isLoading = sendOtpState is SendOtpState.Loading ||
            verifyOtpState is VerifyRegisterOtpState.Loading ||
            loginState is LoginState.Loading

    // Ambient glow pulse
    val pulse = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by pulse.animateFloat(
        initialValue = 0.15f, targetValue = 0.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ), label = "pa"
    )

    /* ── Side Effects ── */

    LaunchedEffect(sendOtpState) {
        when (sendOtpState) {
            is SendOtpState.Success -> {
                val result = (sendOtpState as SendOtpState.Success).otp
                viewModel.otpToken = result.otpToken
                viewModel.number   = phoneNumber
                
                if (viewModel.authType == "FORGOT_PASSWORD") {
                    // Stay on this screen to enter OTP
                    snackbarHostState.showSnackbar("OTP sent to +91 $phoneNumber")
                } else if (viewModel.authType == "SIGNUP") {
                    snackbarHostState.showSnackbar("OTP sent to +91 $phoneNumber")
                }
            }
            is SendOtpState.Error ->
                snackbarHostState.showSnackbar((sendOtpState as SendOtpState.Error).message)
            else -> Unit
        }
    }

    LaunchedEffect(verifyOtpState) {
        when (verifyOtpState) {
            is VerifyRegisterOtpState.Success -> {
                val verifiedToken = (verifyOtpState as VerifyRegisterOtpState.Success).result.tempToken
                if (viewModel.authType == "FORGOT_PASSWORD") {
                    navController.navigate("${AppScreen.ForgotPassword.name}/${phoneNumber}/${verifiedToken}")
                } else {
                    navController.navigate("${AppScreen.SignUp.name}/${verifiedToken}")
                }
                // Reset OTP states after navigation to prevent re-navigation on back
                viewModel.resetOtpStates()
            }
            is VerifyRegisterOtpState.Error ->
                snackbarHostState.showSnackbar((verifyOtpState as VerifyRegisterOtpState.Error).message)
            else -> Unit
        }
    }

    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginState.Success ->
                navController.navigate(AppScreen.HomeScreen.name) { popUpTo(0) }
            is LoginState.Error ->
                snackbarHostState.showSnackbar((loginState as LoginState.Error).message)
            else -> Unit
        }
    }

    /* ── UI ── */

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBackground)
    ) {
        // Glow top-left
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-80).dp, y = (-60).dp)
                .align(Alignment.TopStart)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(ColorAccent.copy(alpha = pulseAlpha), Color.Transparent)
                    ),
                    shape = RoundedCornerShape(50)
                )
        )
        // Glow bottom-right
        Box(
            modifier = Modifier
                .size(260.dp)
                .offset(x = 60.dp, y = 60.dp)
                .align(Alignment.BottomEnd)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(ColorAccentDim.copy(alpha = 0.5f), Color.Transparent)
                    ),
                    shape = RoundedCornerShape(50)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(Modifier.height(72.dp))

            // Brand chip
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

            // Heading — changes by authType
            Text(
                text = when {
                    isLogin -> "Welcome\nback."
                    authType == "FORGOT_PASSWORD" -> "Reset\nPassword."
                    else -> "Let's get\nyou started."
                },
                color = ColorText,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 46.sp,
                letterSpacing = (-0.5).sp
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = when {
                    isLogin -> "Log in to continue your journey"
                    authType == "FORGOT_PASSWORD" -> "Enter your mobile number to reset password"
                    else -> "Enter your mobile number to sign up"
                },
                color = ColorTextMuted,
                fontSize = 15.sp
            )

            Spacer(Modifier.height(44.dp))

            // ── Form card ──
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(ColorSurface)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Phone row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Country code box
                    Box(
                        modifier = Modifier
                            .height(56.dp)
                            .width(70.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(ColorCard)
                            .border(1.dp, ColorBorder, RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+91",
                            color = ColorAccent,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Phone input
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { if (it.length <= 10) phoneNumber = it },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        placeholder = {
                            Text("Mobile number", color = ColorTextMuted, fontSize = 14.sp)
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Phone, null,
                                tint = ColorAccent.copy(alpha = 0.8f),
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = outlinedFieldColors()
                    )
                }

                // Password (LOGIN only) with animated reveal
                AnimatedVisibility(
                    visible = isLogin,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column {
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            placeholder = {
                                Text("Password", color = ColorTextMuted, fontSize = 14.sp)
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.Lock, null,
                                    tint = ColorAccent.copy(alpha = 0.8f),
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Outlined.VisibilityOff
                                        else Icons.Outlined.Visibility,
                                        contentDescription = null,
                                        tint = ColorTextMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible)
                                VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = outlinedFieldColors()
                        )

                        // Forgot Password link
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            TextButton(
                                onClick = {
                                    viewModel.resetOtpStates() // Clear stale OTP state
                                    viewModel.authType = "FORGOT_PASSWORD"
                                },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    text = "Forgot Password?",
                                    color = ColorAccent,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // OTP (SIGNUP or FORGOT_PASSWORD only after sending)
                AnimatedVisibility(
                    visible = isOtpSent && (authType == "SIGNUP" || authType == "FORGOT_PASSWORD"),
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    OutlinedTextField(
                        value = otp,
                        onValueChange = { if (it.length <= 4) otp = it },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        placeholder = { Text("4-digit OTP", color = ColorTextMuted, fontSize = 14.sp) },
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.VpnKey, null,
                                tint = ColorAccent.copy(alpha = 0.8f),
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = outlinedFieldColors()
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            // ── CTA Button ──
            GreenPrimaryButton(
                text = when {
                    isLogin -> "Log In"
                    isOtpSent -> "Verify OTP"
                    else -> "Continue"
                },
                isLoading = isLoading,
                onClick = {
                    if (phoneNumber.length != 10) {
                        scope.launch { snackbarHostState.showSnackbar("Enter a valid 10-digit number") }
                        return@GreenPrimaryButton
                    }
                    when (viewModel.authType) {
                        "LOGIN" -> {
                            if (password.isBlank()) {
                                scope.launch { snackbarHostState.showSnackbar("Password cannot be empty") }
                                return@GreenPrimaryButton
                            }
                            viewModel.login(phoneNumber, password)
                        }
                        "FORGOT_PASSWORD" -> {
                            if (!isOtpSent) {
                                viewModel.sendForgotPasswordOtp(phoneNumber)
                            } else {
                                if (otp.length != 4) {
                                    scope.launch { snackbarHostState.showSnackbar("Enter valid 4-digit OTP") }
                                } else {
                                    viewModel.verifyForgotPasswordOtp(otp)
                                }
                            }
                        }
                        else -> { // SIGNUP
                            if (!isOtpSent) {
                                viewModel.sendRegisterOtp(phoneNumber)
                            } else {
                                if (otp.length != 4) {
                                    scope.launch { snackbarHostState.showSnackbar("Enter valid 4-digit OTP") }
                                } else {
                                    viewModel.verifyRegisterOtp(otp)
                                }
                            }
                        }
                    }
                }
            )

            Spacer(Modifier.height(20.dp))

            // ── Auth-type toggle ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (isLogin) "Don't have an account? " else "Already have an account? ",
                    color = ColorTextMuted,
                    fontSize = 14.sp
                )
                TextButton(
                    onClick = { 
                        if (authType == "FORGOT_PASSWORD") {
                            viewModel.authType = "LOGIN"
                        } else {
                            navController.popBackStack()
                        }
                    },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = if (isLogin) "Sign up" else "Log in",
                        color = ColorAccent,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier  = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = ColorCard,
                contentColor   = ColorText,
                actionColor    = ColorAccent,
                shape          = RoundedCornerShape(14.dp)
            )
        }
    }
}

// ─── Shared helpers ───────────────────────────────────────────────────────────

@Composable
private fun outlinedFieldColors() = OutlinedTextFieldDefaults.colors(
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
            containerColor        = Color.Transparent,
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