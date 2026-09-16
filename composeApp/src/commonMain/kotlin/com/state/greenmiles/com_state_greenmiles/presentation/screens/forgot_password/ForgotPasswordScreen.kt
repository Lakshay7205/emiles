package com.state.greenmiles.com_state_greenmiles.presentation.screens.forgot_password

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import com.state.greenmiles.com_state_greenmiles.presentation.screens.states.ForgotPasswordUiState
import kotlinx.coroutines.launch

private val ColorBackground = Color(0xFF0D1A12)
private val ColorSurface    = Color(0xFF142B1C)
private val ColorCard       = Color(0xFF1C3826)
private val ColorBorder     = Color(0xFF2E5C3A)
private val ColorAccent     = Color(0xFF4ADE80)
private val ColorAccentDim  = Color(0xFF166534)
private val ColorText       = Color(0xFFF0FDF4)
private val ColorTextMuted  = Color(0xFF86EFAC)

@Composable
fun ForgotPasswordScreen(
    navController: NavHostController,
    viewModel: ForgotPasswordViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }

    val isSuccess = uiState is ForgotPasswordUiState.Success
    val isLoading = uiState is ForgotPasswordUiState.Loading

    // Glow animation
    val pulse = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by pulse.animateFloat(
        initialValue = 0.15f, targetValue = 0.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ), label = "pa"
    )

    // Success checkmark bounce
    val successScale by animateFloatAsState(
        targetValue = if (isSuccess) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ), label = "checkScale"
    )

    // Navigate after success
    LaunchedEffect(uiState) {
        when (uiState) {
            is ForgotPasswordUiState.Success -> {
                kotlinx.coroutines.delay(2500)
                navController.navigate(AppScreen.WelcomeScreen.name) {
                    popUpTo("auth_graph") { inclusive = true }
                }
            }
            is ForgotPasswordUiState.Error -> {
                snackbarHostState.showSnackbar(
                    (uiState as ForgotPasswordUiState.Error).message
                )
            }
            else -> Unit
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(ColorBackground)
    ) {
        // ── Glow top-left ──
        Box(
            Modifier.size(300.dp)
                .offset((-80).dp, (-60).dp)
                .align(Alignment.TopStart)
                .background(
                    Brush.radialGradient(
                        listOf(ColorAccent.copy(alpha = pulseAlpha), Color.Transparent)
                    ),
                    RoundedCornerShape(50)
                )
        )
        // ── Glow bottom-right ──
        Box(
            Modifier.size(260.dp)
                .offset(60.dp, 60.dp)
                .align(Alignment.BottomEnd)
                .background(
                    Brush.radialGradient(
                        listOf(ColorAccentDim.copy(alpha = 0.4f), Color.Transparent)
                    ),
                    RoundedCornerShape(50)
                )
        )

        // ── Content ──
        Crossfade(
            targetState = isSuccess,
            animationSpec = tween(500, easing = EaseOutCubic),
            label = "content"
        ) { showSuccess ->
            if (showSuccess) {
                // ═══════════════════════════════════════════
                //  SUCCESS SCREEN
                // ═══════════════════════════════════════════
                Column(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Outlined.CheckCircle, null,
                        tint = ColorAccent,
                        modifier = Modifier.size(80.dp).scale(successScale)
                    )
                    Spacer(Modifier.height(24.dp))
                    Text(
                        "Password Updated!",
                        color = ColorText,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Redirecting to login…",
                        color = ColorTextMuted,
                        fontSize = 15.sp
                    )
                }
            } else {
                // ═══════════════════════════════════════════
                //  RESET PASSWORD FORM
                // ═══════════════════════════════════════════
                Column(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 28.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Spacer(Modifier.height(48.dp))

                    // Back button
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Outlined.ArrowBack,
                            "Back", tint = ColorText
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    // Brand chip
                    Box(
                        Modifier.clip(RoundedCornerShape(50))
                            .background(ColorCard)
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            "🔒  New Password",
                            color = ColorAccent,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    Text(
                        "Create\nNew Password",
                        color = ColorText,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 46.sp,
                        letterSpacing = (-0.5).sp
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "Your new password must be at least 6 characters",
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
                        // New Password
                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            placeholder = {
                                Text("New Password", color = ColorTextMuted, fontSize = 14.sp)
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
                                        if (passwordVisible) Icons.Outlined.VisibilityOff
                                        else Icons.Outlined.Visibility,
                                        null, tint = ColorTextMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible)
                                VisualTransformation.None
                            else PasswordVisualTransformation(),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = fieldColors()
                        )

                        // Confirm Password
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            placeholder = {
                                Text("Confirm Password", color = ColorTextMuted, fontSize = 14.sp)
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.Lock, null,
                                    tint = ColorAccent.copy(alpha = 0.8f),
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { confirmVisible = !confirmVisible }) {
                                    Icon(
                                        if (confirmVisible) Icons.Outlined.VisibilityOff
                                        else Icons.Outlined.Visibility,
                                        null, tint = ColorTextMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            },
                            visualTransformation = if (confirmVisible)
                                VisualTransformation.None
                            else PasswordVisualTransformation(),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = fieldColors()
                        )
                    }

                    Spacer(Modifier.height(28.dp))

                    // ── Reset Password button ──
                    Button(
                        onClick = {
                            when {
                                newPassword.length < 6 -> scope.launch {
                                    snackbarHostState.showSnackbar("Password must be at least 6 characters")
                                }
                                newPassword != confirmPassword -> scope.launch {
                                    snackbarHostState.showSnackbar("Passwords don't match")
                                }
                                else -> viewModel.resetPassword(newPassword)
                            }
                        },
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize().background(
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
                                    "Reset Password",
                                    color = Color(0xFF052E16),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.3.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Snackbar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = ColorCard,
                contentColor = ColorText,
                actionColor = ColorAccent,
                shape = RoundedCornerShape(14.dp)
            )
        }
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = ColorAccent,
    unfocusedBorderColor = ColorBorder,
    focusedLabelColor = ColorAccent,
    unfocusedLabelColor = ColorTextMuted,
    focusedTextColor = ColorText,
    unfocusedTextColor = ColorText,
    cursorColor = ColorAccent,
    focusedContainerColor = ColorCard,
    unfocusedContainerColor = ColorCard
)
