package com.state.greenmiles.com_state_greenmiles.presentation.screens.aadhaar_kyc

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

// ─── Design Tokens ────────────────────────────────────────────────────────────

private val ColorBackground = Color(0xFF0D1A12)
private val ColorSurface    = Color(0xFF142B1C)
private val ColorCard       = Color(0xFF1C3826)
private val ColorBorder     = Color(0xFF2E5C3A)
private val ColorAccent     = Color(0xFF4ADE80)
private val ColorAccentDim  = Color(0xFF166534)
private val ColorText       = Color(0xFFF0FDF4)
private val ColorTextMuted  = Color(0xFF86EFAC)

private val PrimaryGradient = Brush.horizontalGradient(
    listOf(ColorAccent, Color(0xFF22C55E))
)
private val GreenSuccess = Color(0xFF4ADE80)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AadhaarKycScreen(
    viewModel: AadhaarViewModel = koinViewModel(),
    filePicker: FilePicker = koinInject(),
    onBackClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is AadhaarUiEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is AadhaarUiEffect.NavigationBack -> {
                    onBackClick()
                }
                is AadhaarUiEffect.ProfileUpdateSuccess -> {
                    snackbarHostState.showSnackbar("Profile updated successfully!")
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Identity Verification", 
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = ColorText
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = ColorText)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = ColorBackground
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(ColorBackground)
        ) {
            // Ambient glow orbs
            Box(
                modifier = Modifier
                    .size(340.dp)
                    .offset(x = 90.dp, y = (-70).dp)
                    .align(Alignment.TopEnd)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(ColorAccent.copy(alpha = 0.15f), Color.Transparent)
                        ),
                        shape = CircleShape
                    )
            )
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .offset(x = (-60).dp, y = 60.dp)
                    .align(Alignment.BottomStart)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(ColorAccentDim.copy(alpha = 0.4f), Color.Transparent)
                        ),
                        shape = CircleShape
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Progress Indicator
                AadhaarProgressHeader(step = state.step)

                Spacer(modifier = Modifier.height(32.dp))

                AnimatedContent(
                    targetState = state.step,
                    transitionSpec = {
                        (fadeIn() + slideInHorizontally { it }).togetherWith(fadeOut() + slideOutHorizontally { -it })
                    }
                ) { step ->
                    when (step) {
                        AadhaarStep.UPLOAD -> UploadStepContent(
                            isLoading = state.isLoading,
                            onFileSelect = { filePicker.pickFile { viewModel.uploadFile(it) } }
                        )
                        AadhaarStep.VERIFY -> VerifyStepContent(
                            isLoading = state.isLoading,
                            onVerify = { viewModel.verifyPin(it) }
                        )
                        AadhaarStep.CONFIRM_PROFILE -> ConfirmProfileStepContent(
                            isLoading = state.isLoading || state.isUpdatingProfile,
                            kyc = state.kycDetails,
                            onConfirm = { viewModel.confirmProfileUpdate() },
                            onSkip = { viewModel.skipProfileUpdate() }
                        )
                        AadhaarStep.SUCCESS -> SuccessStepContent(
                            onDone = { viewModel.onReset() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AadhaarProgressHeader(step: AadhaarStep) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProgressNode(
            modifier = Modifier.weight(1f),
            icon = Icons.Outlined.UploadFile,
            label = "Upload",
            isActive = step == AadhaarStep.UPLOAD,
            isCompleted = step > AadhaarStep.UPLOAD
        )
        ProgressDivider()
        ProgressNode(
            modifier = Modifier.weight(1f),
            icon = Icons.Outlined.Fingerprint,
            label = "Verify",
            isActive = step == AadhaarStep.VERIFY || step == AadhaarStep.CONFIRM_PROFILE,
            isCompleted = step > AadhaarStep.CONFIRM_PROFILE
        )
        ProgressDivider()
        ProgressNode(
            modifier = Modifier.weight(1f),
            icon = Icons.Outlined.CheckCircle,
            label = "Success",
            isActive = step == AadhaarStep.SUCCESS,
            isCompleted = false
        )
    }
}

@Composable
fun ProgressNode(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    isCompleted: Boolean
) {
    val color = when {
        isCompleted -> ColorAccent
        isActive -> ColorAccent
        else -> ColorBorder
    }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(if (isActive || isCompleted) color.copy(alpha = 0.15f) else ColorCard),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
        }
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (isActive || isCompleted) ColorText else ColorTextMuted,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp).fillMaxWidth()
        )
    }
}

@Composable
fun ProgressDivider() {
    Box(
        modifier = Modifier
            .width(40.dp)
            .height(1.dp)
            .background(ColorBorder)
    )
}

@Composable
fun UploadStepContent(isLoading: Boolean, onFileSelect: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = ColorCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ColorBorder)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(PrimaryGradient),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.UploadFile,
                    contentDescription = null,
                    tint = Color(0xFF052E16),
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Upload offline Aadhaar",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = ColorText
            )

            Text(
                "Please upload the .zip file downloaded from the UIDAI portal.",
                style = MaterialTheme.typography.bodyMedium,
                color = ColorTextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onFileSelect,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp),
                enabled = !isLoading
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(PrimaryGradient, shape = RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color(0xFF052E16), modifier = Modifier.size(24.dp))
                    } else {
                        Text("Choose File", fontWeight = FontWeight.Bold, color = Color(0xFF052E16))
                    }
                }
            }
        }
    }
}

@Composable
fun VerifyStepContent(isLoading: Boolean, onVerify: (String) -> Unit) {
    var pin by remember { mutableStateOf("") }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = ColorCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, ColorBorder)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Secure Verification",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = ColorText
            )
            
            Text(
                "Enter the 4-digit share PIN of your Aadhaar file.",
                style = MaterialTheme.typography.bodyMedium,
                color = ColorTextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = pin,
                onValueChange = { if (it.length <= 4) pin = it },
                label = { Text("Share PIN", color = ColorTextMuted) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ColorAccent,
                    unfocusedBorderColor = ColorBorder,
                    focusedTextColor = ColorText,
                    unfocusedTextColor = ColorText,
                    cursorColor = ColorAccent
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { onVerify(pin) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp),
                enabled = !isLoading && pin.length == 4
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = if (pin.length == 4) PrimaryGradient else SolidColor(ColorSurface),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color(0xFF052E16), modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = "Verify & Continue", 
                            fontWeight = FontWeight.Bold,
                            color = if (pin.length == 4) Color(0xFF052E16) else ColorTextMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SuccessStepContent(onDone: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = ColorCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, ColorBorder)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(GreenSuccess.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = GreenSuccess, modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Verification Complete",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = GreenSuccess
            )

            Text(
                "Your Aadhaar has been verified successfully and your profile has been updated.",
                style = MaterialTheme.typography.bodyMedium,
                color = ColorTextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 12.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = onDone,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(PrimaryGradient, shape = RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Finish Registration", fontWeight = FontWeight.Bold, color = Color(0xFF052E16))
                }
            }
        }
    }
}

@Composable
fun ConfirmProfileStepContent(
    isLoading: Boolean,
    kyc: com.state.greenmiles.com_state_greenmiles.domain.model.AadhaarKyc?,
    onConfirm: () -> Unit,
    onSkip: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = ColorCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, ColorBorder)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Confirm Profile Details",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = ColorText
            )

            Text(
                "We found these details in your Aadhaar. Would you like to update your profile with this information?",
                style = MaterialTheme.typography.bodyMedium,
                color = ColorTextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            kyc?.let {
                ProfileField(label = "NAME", value = it.name)
                ProfileField(label = "DOB", value = it.dob)
                ProfileField(label = "GENDER", value = it.gender)
                ProfileField(label = "ADDRESS", value = it.address)
                ProfileField(label = "CITY", value = it.city)
                ProfileField(label = "STATE", value = it.state)
                ProfileField(label = "PINCODE", value = it.pincode)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp),
                enabled = !isLoading
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(PrimaryGradient, shape = RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color(0xFF052E16), modifier = Modifier.size(24.dp))
                    } else {
                        Text("Update Profile", fontWeight = FontWeight.Bold, color = Color(0xFF052E16))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = onSkip,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Skip for now", color = ColorTextMuted)
            }
        }
    }
}

@Composable
fun ProfileField(label: String, value: String) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = ColorTextMuted)
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = ColorText)
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = ColorBorder.copy(alpha = 0.5f))
    }
}