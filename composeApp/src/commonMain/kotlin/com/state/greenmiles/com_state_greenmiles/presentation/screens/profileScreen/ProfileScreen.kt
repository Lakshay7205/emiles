package com.state.greenmiles.com_state_greenmiles.presentation.screens.profileScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.state.greenmiles.com_state_greenmiles.domain.model.UserProfile
import com.state.greenmiles.com_state_greenmiles.presentation.navigation.AppScreen
import com.state.greenmiles.com_state_greenmiles.presentation.screens.home_Screen.ColorAccent
import com.state.greenmiles.com_state_greenmiles.presentation.screens.home_Screen.ColorBackground
import com.state.greenmiles.com_state_greenmiles.presentation.screens.home_Screen.ColorCard
import com.state.greenmiles.com_state_greenmiles.presentation.screens.home_Screen.ColorSurface
import com.state.greenmiles.com_state_greenmiles.presentation.screens.home_Screen.ColorText
import com.state.greenmiles.com_state_greenmiles.presentation.screens.home_Screen.ColorTextMuted
import org.koin.compose.viewmodel.koinViewModel

private val ColorBorder  = Color(0xFF2E5C3A)
private val ColorError   = Color(0xFFF87171)
private val ColorSuccess = Color(0xFF4ADE80)

// ─── Sub-screen navigation ────────────────────────────────────────────────────

private enum class ProfileSubScreen {
    MAIN, EDIT_EMAIL, SEND_MOBILE_OTP, VERIFY_MOBILE_OTP
}

// ─── Screen entry point ───────────────────────────────────────────────────────

@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    navController: NavController
) {
    val viewModel: ProfileViewModel = koinViewModel()
    val state = viewModel.uiState
    var subScreen by remember { mutableStateOf(ProfileSubScreen.MAIN) }
    var pendingMobile by remember { mutableStateOf("") }
    var storedOtpToken by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { viewModel.loadProfile() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBackground)
    ) {
        when {
            state.isLoading && state.profile == null -> {
                CircularProgressIndicator(
                    color = ColorAccent,
                    strokeWidth = 2.dp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            state.error != null && state.profile == null -> {
                ErrorState(message = state.error!!, onRetry = { viewModel.loadProfile() })
            }
            state.profile != null -> {
                when (subScreen) {
                    ProfileSubScreen.MAIN -> ProfileMain(
                        profile = state.profile,
                        onEditEmail  = { subScreen = ProfileSubScreen.EDIT_EMAIL },
                        onEditMobile = { subScreen = ProfileSubScreen.SEND_MOBILE_OTP },
                        navController = navController
                    )
                    ProfileSubScreen.EDIT_EMAIL -> EditEmailScreen(
                        currentEmail = state.profile.email,
                        isLoading    = state.isLoading,
                        message      = state.message,
                        error        = state.error,
                        onSubmit     = { email -> viewModel.updateEmail(email) },
                        onBack       = { subScreen = ProfileSubScreen.MAIN }
                    )
                    ProfileSubScreen.SEND_MOBILE_OTP -> SendMobileOtpScreen(
                        currentMobile = state.profile.mobile,
                        isLoading     = state.isLoading,
                        message       = state.message,
                        error         = state.error,
                        onSendOtp     = { mobile ->
                            pendingMobile = mobile
                            viewModel.sendMobileOtp(mobile)
                            subScreen = ProfileSubScreen.VERIFY_MOBILE_OTP
                        },
                        onBack        = { subScreen = ProfileSubScreen.MAIN }
                    )
                    ProfileSubScreen.VERIFY_MOBILE_OTP -> VerifyMobileOtpScreen(
                        mobile    = pendingMobile,
                        isLoading = state.isLoading,
                        message   = state.message,
                        error     = state.error,
                        onVerify  = { otp ->
                            viewModel.verifyAndUpdateMobile(pendingMobile, otp, state.otpToken ?: "")
                        },
                        onResend  = { viewModel.sendMobileOtp(pendingMobile) },
                        onBack    = { subScreen = ProfileSubScreen.SEND_MOBILE_OTP }
                    )
                }
            }
        }
    }
}

// ─── Main Profile Screen ──────────────────────────────────────────────────────

@Composable
private fun ProfileMain(
    navController: NavController,
    profile: UserProfile,
    onEditEmail: () -> Unit,
    onEditMobile: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBackground)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Header banner ──
        Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(brush = Brush.linearGradient(listOf(ColorCard, ColorSurface)))
            )
            // Glow orb
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .offset(x = 80.dp, y = (-40).dp)
                    .align(Alignment.TopEnd)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(ColorAccent.copy(alpha = 0.10f), Color.Transparent)
                        ),
                        shape = RoundedCornerShape(50)
                    )
            )
            // Avatar
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 24.dp)
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(brush = Brush.linearGradient(listOf(ColorAccent, Color(0xFF22C55E)))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = profile.name.take(1).uppercase(),
                    color = Color(0xFF052E16),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            // Verified badge
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 76.dp)
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(ColorBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Verified, contentDescription = null, tint = ColorAccent, modifier = Modifier.size(18.dp))
            }
        }

        Spacer(Modifier.height(12.dp))

        // ── Name + rating ──
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text(profile.name, color = ColorText, fontSize = 22.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.3).sp)
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Outlined.Star, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(15.dp))
                Text("${profile.rating}  ·  ${profile.totalRatings} ratings", color = ColorTextMuted, fontSize = 13.sp)
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Stats ──
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(Icons.Outlined.DirectionsCar, profile.tripsCompleted.toString(), "Trips",     Modifier.weight(1f))
            StatCard(Icons.Outlined.Route,          profile.travelCompleted.toString(), "Travelled", Modifier.weight(1f))
            StatCard(Icons.Outlined.Inventory2,     profile.parcelDelivered.toString(), "Parcels",   Modifier.weight(1f))
        }

        Spacer(Modifier.height(24.dp))

        // ── Personal Info ──
        SectionLabel("Personal Info")
        Spacer(Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(ColorCard)
        ) {
            InfoRowEditable(icon = Icons.Outlined.Email,      label = "Email",  value = profile.email,  onEdit = onEditEmail)
            RowDivider()
            InfoRowEditable(icon = Icons.Outlined.Phone,      label = "Mobile", value = profile.mobile, onEdit = onEditMobile)
            RowDivider()
            InfoRow(icon = Icons.Outlined.Person,             label = "Gender", value = profile.gender ?: "N/A")
            RowDivider()
            InfoRow(icon = Icons.Outlined.LocationOn,         label = "City",   value = profile.city   ?: "N/A")
            RowDivider()
            InfoRow(icon = Icons.Outlined.Map,                label = "State",  value = profile.state  ?: "N/A")
            InfoRowEditable(icon = Icons.Outlined.Verified,                label = "Adhar Kyc",  value = "E KyC",  onEdit = {navController.navigate(AppScreen.AadhaarKycScreen.name)})

        }

        Spacer(Modifier.height(32.dp))
    }
}

// ─── Edit Email Screen ────────────────────────────────────────────────────────

@Composable
private fun EditEmailScreen(
    currentEmail: String,
    isLoading: Boolean,
    message: String?,
    error: String?,
    onSubmit: (String) -> Unit,
    onBack: () -> Unit
) {
    var email by remember { mutableStateOf(currentEmail) }

    SubScreenScaffold(title = "Update Email", onBack = onBack) {
        Text("Enter your new email address below.", color = ColorTextMuted, fontSize = 14.sp)
        Spacer(Modifier.height(24.dp))
        GreenTextField(value = email, onValueChange = { email = it }, label = "Email Address", icon = Icons.Outlined.Email, keyboardType = KeyboardType.Email)
        Spacer(Modifier.height(8.dp))
        message?.let { FeedbackBanner(it, isError = false) }
        error?.let   { FeedbackBanner(it, isError = true)  }
        Spacer(Modifier.height(24.dp))
        PrimaryButton(text = "Update Email", isLoading = isLoading, onClick = { onSubmit(email) })
    }
}

// ─── Send Mobile OTP Screen ───────────────────────────────────────────────────

@Composable
private fun SendMobileOtpScreen(
    currentMobile: String,
    isLoading: Boolean,
    message: String?,
    error: String?,
    onSendOtp: (String) -> Unit,
    onBack: () -> Unit
) {
    var mobile by remember { mutableStateOf(currentMobile) }

    SubScreenScaffold(title = "Update Mobile", onBack = onBack) {
        Text("Enter your new mobile number. We'll send an OTP to verify it.", color = ColorTextMuted, fontSize = 14.sp)
        Spacer(Modifier.height(24.dp))
        GreenTextField(value = mobile, onValueChange = { mobile = it }, label = "Mobile Number", icon = Icons.Outlined.Phone, keyboardType = KeyboardType.Phone)
        Spacer(Modifier.height(8.dp))
        message?.let { FeedbackBanner(it, isError = false) }
        error?.let   { FeedbackBanner(it, isError = true)  }
        Spacer(Modifier.height(24.dp))
        PrimaryButton(text = "Send OTP", isLoading = isLoading, onClick = { onSendOtp(mobile) })
    }
}

// ─── Verify OTP Screen ────────────────────────────────────────────────────────

@Composable
private fun VerifyMobileOtpScreen(
    mobile: String,
    isLoading: Boolean,
    message: String?,
    error: String?,
    onVerify: (otp: String) -> Unit,
    onResend: () -> Unit,
    onBack: () -> Unit
) {
    var otp by remember { mutableStateOf("") }

    SubScreenScaffold(title = "Verify OTP", onBack = onBack) {
        Text("Enter the OTP sent to $mobile", color = ColorTextMuted, fontSize = 14.sp)
        Spacer(Modifier.height(24.dp))
        GreenTextField(value = otp, onValueChange = { otp = it }, label = "OTP", icon = Icons.Outlined.Lock, keyboardType = KeyboardType.NumberPassword)
        Spacer(Modifier.height(8.dp))
        message?.let { FeedbackBanner(it, isError = false) }
        error?.let   { FeedbackBanner(it, isError = true)  }
        Spacer(Modifier.height(24.dp))
        PrimaryButton(text = "Verify & Update", isLoading = isLoading, onClick = { onVerify(otp) })
        Spacer(Modifier.height(12.dp))
        TextButton(onClick = onResend, modifier = Modifier.fillMaxWidth()) {
            Text("Resend OTP", color = ColorTextMuted, fontSize = 13.sp)
        }
    }
}

// ─── Sub-screen Scaffold ──────────────────────────────────────────────────────

@Composable
private fun SubScreenScaffold(
    title: String,
    onBack: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBackground)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 52.dp, bottom = 16.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ColorCard)
            ) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = "Back", tint = ColorAccent)
            }
            Text(
                text = title,
                color = ColorText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            content()
        }
    }
}

// ─── Reusable UI Components ───────────────────────────────────────────────────

@Composable
private fun GreenTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = ColorTextMuted, fontSize = 13.sp) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = ColorAccent, modifier = Modifier.size(18.dp)) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor        = ColorText,
            unfocusedTextColor      = ColorText,
            focusedBorderColor      = ColorAccent,
            unfocusedBorderColor    = ColorBorder,
            cursorColor             = ColorAccent,
            focusedContainerColor   = ColorCard,
            unfocusedContainerColor = ColorCard
        )
    )
}

@Composable
private fun PrimaryButton(text: String, isLoading: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(listOf(ColorAccent, Color(0xFF22C55E))),
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color(0xFF052E16), strokeWidth = 2.dp, modifier = Modifier.size(22.dp))
            } else {
                Text(text, color = Color(0xFF052E16), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun FeedbackBanner(message: String, isError: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isError) ColorError.copy(alpha = 0.12f) else ColorSuccess.copy(alpha = 0.12f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isError) Icons.Outlined.ErrorOutline else Icons.Outlined.CheckCircle,
            contentDescription = null,
            tint = if (isError) ColorError else ColorSuccess,
            modifier = Modifier.size(16.dp)
        )
        Text(text = message, color = if (isError) ColorError else ColorSuccess, fontSize = 13.sp)
    }
}

@Composable
private fun StatCard(icon: ImageVector, value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(ColorCard)
            .padding(vertical = 14.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(icon, contentDescription = null, tint = ColorAccent, modifier = Modifier.size(18.dp))
        Text(value, color = ColorText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(label, color = ColorTextMuted, fontSize = 11.sp)
    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(icon, contentDescription = null, tint = ColorAccent, modifier = Modifier.size(18.dp))
        Column {
            Text(label, color = ColorTextMuted, fontSize = 11.sp)
            Text(value, color = ColorText, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun InfoRowEditable(icon: ImageVector, label: String, value: String, onEdit: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(icon, contentDescription = null, tint = ColorAccent, modifier = Modifier.size(18.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, color = ColorTextMuted, fontSize = 11.sp)
            Text(value, color = ColorText, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
        IconButton(
            onClick = onEdit,
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(ColorSurface)
        ) {
            Icon(Icons.Outlined.Edit, contentDescription = "Edit $label", tint = ColorAccent, modifier = Modifier.size(15.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        color = ColorTextMuted,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
    )
}

@Composable
private fun RowDivider() {
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = ColorBorder, thickness = 0.5.dp)
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(Icons.Outlined.ErrorOutline, contentDescription = null, tint = ColorError, modifier = Modifier.size(48.dp))
            Text(message, color = ColorTextMuted, fontSize = 14.sp)
            Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = ColorCard), shape = RoundedCornerShape(12.dp)) {
                Text("Retry", color = ColorAccent)
            }
        }
    }
}