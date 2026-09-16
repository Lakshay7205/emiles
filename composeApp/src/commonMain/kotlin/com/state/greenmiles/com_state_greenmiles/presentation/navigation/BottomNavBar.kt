package com.state.greenmiles.com_state_greenmiles.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.state.greenmiles.com_state_greenmiles.presentation.screens.home_Screen.ColorAccent
import com.state.greenmiles.com_state_greenmiles.presentation.screens.home_Screen.ColorBackground
import com.state.greenmiles.com_state_greenmiles.presentation.screens.home_Screen.ColorCard
import com.state.greenmiles.com_state_greenmiles.presentation.screens.home_Screen.ColorSurface
import com.state.greenmiles.com_state_greenmiles.presentation.screens.home_Screen.ColorTextMuted

@Composable
fun BottomNavBar(
    selected: NavDestination,
    onSelect: (NavDestination) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorBackground)
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .padding(bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(ColorSurface)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavDestination.entries.forEach { destination ->
                val isSelected = destination == selected
                val interactionSource = remember { MutableInteractionSource() }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = { onSelect(destination) }
                        )
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isSelected)
                                    Brush.linearGradient(listOf(ColorAccent, Color(0xFF22C55E)))
                                else
                                    Brush.linearGradient(listOf(ColorCard, ColorCard))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = destination.icon,
                            contentDescription = destination.label,
                            tint = if (isSelected) Color(0xFF052E16) else ColorTextMuted,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Text(
                        text = destination.label,
                        color = if (isSelected) ColorAccent else ColorTextMuted,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        letterSpacing = 0.2.sp
                    )
                }
            }
        }
    }
}