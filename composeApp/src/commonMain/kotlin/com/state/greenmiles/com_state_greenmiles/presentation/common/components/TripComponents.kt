package com.state.greenmiles.com_state_greenmiles.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.state.greenmiles.com_state_greenmiles.domain.model.trip.Coordinate
import com.state.greenmiles.com_state_greenmiles.presentation.common.models.ValidationState
import com.state.greenmiles.com_state_greenmiles.presentation.screens.googlemap.PlacePrediction
import com.state.greenmiles.com_state_greenmiles.presentation.screens.googlemap.getPlaceDetails
import com.state.greenmiles.com_state_greenmiles.presentation.screens.googlemap.rememberPlacesClient
import com.state.greenmiles.com_state_greenmiles.presentation.screens.googlemap.searchPlaces
import kotlinx.coroutines.delay
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock as StdClock

// ─── Shared Design Tokens ─────────────────────────────────────────────────────

val ColorBackground = Color(0xFF0D1A12)
val ColorSurface    = Color(0xFF142B1C)
val ColorCard       = Color(0xFF1C3826)
val ColorBorder     = Color(0xFF2E5C3A)
val ColorAccent     = Color(0xFF4ADE80)
val ColorAccentDim  = Color(0xFF166534)
val ColorText       = Color(0xFFF0FDF4)
val ColorTextMuted  = Color(0xFF86EFAC)
val ColorError      = Color(0xFFFF6B6B)
val ColorAmber      = Color(0xFFFBBF24)
val ColorBlue       = Color(0xFF60A5FA)
val ColorPink       = Color(0xFFF472B6)

@Composable
fun GreenButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier.fillMaxWidth().height(54.dp)
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
                .background(
                    brush = if (enabled && !isLoading)
                        Brush.horizontalGradient(listOf(ColorAccent, Color(0xFF22C55E)))
                    else Brush.horizontalGradient(listOf(ColorBorder, ColorBorder)),
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color(0xFF052E16), modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp)
            } else {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (icon != null) Icon(icon, null, tint = Color(0xFF052E16), modifier = Modifier.size(18.dp))
                    Text(text, color = Color(0xFF052E16), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun FormCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(ColorSurface).padding(16.dp),
        content = content
    )
}

@Composable
fun FormSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(title, color = ColorTextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
        content()
    }
}

@Composable
fun PickerRow(
    icon: ImageVector,
    label: String,
    value: String,
    hasValue: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(icon, null, tint = ColorAccent, modifier = Modifier.size(18.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, color = ColorTextMuted, fontSize = 11.sp, letterSpacing = 0.3.sp)
            Text(value, color = if (hasValue) ColorText else ColorTextMuted.copy(alpha = 0.6f), fontSize = 15.sp, fontWeight = FontWeight.Medium)
        }
        Icon(Icons.Default.ChevronRight, null, tint = ColorBorder, modifier = Modifier.size(16.dp))
    }
}

@Composable
fun LocationPickerButton(label: String, value: String, icon: ImageVector, iconTint: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(ColorCard)
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(icon, null, tint = iconTint, modifier = Modifier.size(20.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, color = ColorTextMuted, fontSize = 11.sp)
            Text(
                value.ifEmpty { "Search location" },
                color = if (value.isEmpty()) ColorTextMuted.copy(alpha = 0.5f) else ColorText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Icon(Icons.Default.Search, null, tint = ColorBorder, modifier = Modifier.size(16.dp))
    }
}

@Composable
fun ErrorRow(message: String) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(ColorError.copy(alpha = 0.12f)).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(Icons.Default.Error, null, tint = ColorError, modifier = Modifier.size(16.dp))
        Text(message, color = ColorError, fontSize = 13.sp)
    }
}

@Composable
fun StepperBtn(icon: ImageVector, enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (enabled) ColorAccentDim else ColorBorder)
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, null, tint = if (enabled) ColorAccent else ColorTextMuted, modifier = Modifier.size(16.dp))
    }
}

@Composable
fun GreenOutlinedField(
    label: String,
    value: String,
    icon: ImageVector,
    placeholder: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 13.sp) },
        placeholder = { Text(placeholder, color = ColorTextMuted, fontSize = 13.sp) },
        leadingIcon = { Icon(icon, null, tint = ColorAccent, modifier = Modifier.size(18.dp)) },
        keyboardOptions = keyboardOptions,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = greenTextFieldColors()
    )
}

@Composable
fun greenTextFieldColors() = OutlinedTextFieldDefaults.colors(
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
fun LocationSearchDialog(
    onDismiss: () -> Unit,
    onLocationSelected: (String, Coordinate) -> Unit
) {
    var searchQuery   by remember { mutableStateOf("") }
    var predictions   by remember { mutableStateOf<List<PlacePrediction>>(emptyList()) }
    var isSearching   by remember { mutableStateOf(false) }
    val placesClient  = rememberPlacesClient()

    LaunchedEffect(searchQuery) {
        if (searchQuery.length >= 3) {
            isSearching = true
            delay(500)
            predictions = searchPlaces(placesClient, searchQuery)
            isSearching = false
        } else predictions = emptyList()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.8f)
                .clip(RoundedCornerShape(20.dp))
                .background(ColorSurface)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Search bar
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = ColorText)
                    }
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.weight(1f).height(52.dp),
                        placeholder = { Text("Search location…", color = ColorTextMuted, fontSize = 14.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = greenTextFieldColors(),
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, null, tint = ColorTextMuted, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    )
                }

                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(ColorBorder))

                when {
                    isSearching -> Box(
                        modifier = Modifier.fillMaxWidth().padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator(color = ColorAccent, strokeWidth = 2.5.dp) }

                    predictions.isEmpty() && searchQuery.length >= 3 -> Box(
                        modifier = Modifier.fillMaxWidth().padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) { Text("No locations found", color = ColorTextMuted) }

                    else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(predictions) { prediction ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        getPlaceDetails(
                                            placesClient = placesClient,
                                            placeId = prediction.placeId,
                                            onResult = { name, coord -> onLocationSelected(name, coord) }
                                        )
                                    }
                                    .padding(horizontal = 20.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(Icons.Outlined.LocationOn, null, tint = ColorTextMuted, modifier = Modifier.size(18.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(prediction.primaryText, color = ColorText, fontSize = 14.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(prediction.secondaryText, color = ColorTextMuted, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                            Box(modifier = Modifier.fillMaxWidth().padding(start = 50.dp).height(1.dp).background(ColorBorder))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarTypePickerDialog(currentType: String, onDismiss: () -> Unit, onTypeSelected: (String) -> Unit) {
    val types = listOf("Hatchback", "SUV", "Sedan", "Coupe", "Convertible", "Wagon")
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = ColorSurface,
        dragHandle = {
            Box(modifier = Modifier.padding(top = 14.dp, bottom = 6.dp).size(width = 40.dp, height = 4.dp).clip(CircleShape).background(ColorBorder))
        }
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)) {
            Text("Car Type", color = ColorText, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp))
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(ColorBorder))
            types.forEach { type ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (type == currentType) ColorAccentDim.copy(alpha = 0.3f) else Color.Transparent)
                        .clickable { onTypeSelected(type) }
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Outlined.DirectionsCar, null, tint = if (type == currentType) ColorAccent else ColorTextMuted, modifier = Modifier.size(18.dp))
                    Text(type, color = if (type == currentType) ColorAccent else ColorText, fontSize = 15.sp, fontWeight = if (type == currentType) FontWeight.Bold else FontWeight.Normal, modifier = Modifier.weight(1f))
                    if (type == currentType) Icon(Icons.Default.CheckCircle, null, tint = ColorAccent, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YearPickerDialog(currentYear: Int, onDismiss: () -> Unit, onYearSelected: (Int) -> Unit) {
    val currentCalYear = remember { StdClock.System.now().toLocalDateTime(TimeZone.UTC).year }
    val years = remember { (currentCalYear downTo 2000).toList() }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = ColorSurface,
        dragHandle = {
            Box(modifier = Modifier.padding(top = 14.dp, bottom = 6.dp).size(width = 40.dp, height = 4.dp).clip(CircleShape).background(ColorBorder))
        }
    ) {
        Column(modifier = Modifier.fillMaxWidth().heightIn(max = 420.dp).padding(bottom = 32.dp)) {
            Text("Manufacturing Year", color = ColorText, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp))
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(ColorBorder))
            LazyColumn {
                items(years) { year ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (year == currentYear) ColorAccentDim.copy(alpha = 0.3f) else Color.Transparent)
                            .clickable { onYearSelected(year) }
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(year.toString(), color = if (year == currentYear) ColorAccent else ColorText, fontSize = 15.sp, fontWeight = if (year == currentYear) FontWeight.Bold else FontWeight.Normal)
                        if (year == currentYear) Icon(Icons.Default.CheckCircle, null, tint = ColorAccent, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

fun formatDuration(seconds: Long): String {
    val hours   = seconds / 3600
    val minutes = (seconds % 3600) / 60
    return if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
}

fun getRouteColor(index: Int): Color {
    val colors = listOf(
        Color(0xFF4ADE80), Color(0xFF60A5FA), Color(0xFFFBBF24),
        Color(0xFFF472B6), Color(0xFFA78BFA)
    )
    return colors[index % colors.size]
}

fun Double.format1Decimal(): String = (kotlin.math.round(this * 10) / 10.0).toString()

@Composable
fun ReviewSection(
    title: String,
    icon: ImageVector,
    onEditClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    FormCard {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(icon, null, tint = ColorAccent, modifier = Modifier.size(20.dp))
                Text(title, color = ColorText, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                if (onEditClick != null) {
                    Text(
                        "Edit",
                        color = ColorAccent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onEditClick() }
                    )
                }
            }
            HorizontalDivider(color = ColorBorder, thickness = 1.dp)
            content()
        }
    }
}

@Composable
fun ReviewRow(
    icon: ImageVector,
    label: String,
    value: String,
    iconColor: Color = ColorTextMuted
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(icon, null, tint = iconColor, modifier = Modifier.size(18.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, color = ColorTextMuted, fontSize = 11.sp)
            Text(value, color = ColorText, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}

// ─── Reusable Search Components ─────────────────────────────────────────────

@Composable
fun LocationField(
    label: String,
    query: String,
    predictions: List<PlacePrediction>,
    showPredictions: Boolean,
    isSearching: Boolean,
    validationState: ValidationState,
    onQueryChange: (String) -> Unit,
    onPredictionClick: (PlacePrediction) -> Unit,
    onClear: () -> Unit,
    icon: ImageVector,
    accentColor: Color = ColorAccent
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier
                    .size(22.dp)
                    .offset(y = 2.dp)
            )

            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text(label, color = ColorTextMuted, fontSize = 14.sp) },
                modifier = Modifier.weight(1f).height(52.dp),
                trailingIcon = {
                    when {
                        isSearching -> CircularProgressIndicator(
                            color = ColorAccent,
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                        query.isNotEmpty() -> IconButton(onClick = onClear) {
                            Icon(
                                Icons.Default.Clear,
                                null,
                                tint = ColorTextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                isError = validationState is ValidationState.Invalid,
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = accentColor,
                    unfocusedBorderColor = ColorBorder,
                    errorBorderColor = ColorError,

                    focusedTextColor = ColorText,
                    unfocusedTextColor = ColorText,
                    disabledTextColor = ColorText,
                    errorTextColor = ColorText,

                    cursorColor = accentColor,

                    focusedContainerColor = ColorCard,
                    unfocusedContainerColor = ColorCard,
                    disabledContainerColor = ColorCard,
                    errorContainerColor = ColorCard,

                    focusedPlaceholderColor = ColorTextMuted,
                    unfocusedPlaceholderColor = ColorTextMuted,
                    disabledPlaceholderColor = ColorTextMuted,
                    errorPlaceholderColor = ColorTextMuted
                )
            )
        }

        androidx.compose.animation.AnimatedVisibility(visible = showPredictions && predictions.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp, top = 4.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(ColorCard)
            ) {
                predictions.take(4).forEach { prediction ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPredictionClick(prediction) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            Icons.Outlined.LocationOn,
                            null,
                            tint = ColorTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                prediction.primaryText,
                                color = ColorText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                prediction.secondaryText,
                                color = ColorTextMuted,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SmallIconBtn(icon: ImageVector, enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(20.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(if (enabled) ColorAccentDim else ColorBorder)
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, null, tint = if (enabled) ColorAccent else ColorTextMuted, modifier = Modifier.size(14.dp))
    }
}
