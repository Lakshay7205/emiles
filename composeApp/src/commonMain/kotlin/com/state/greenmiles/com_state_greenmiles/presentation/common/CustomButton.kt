package com.state.greenmiles.com_state_greenmiles.presentation.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.state.greenmiles.com_state_greenmiles.theme.appPrimary


@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    text: String,
    containerColor: Color = appPrimary,
    textColor: Color = Color.White,
    onClick: () -> Unit={}
) {
    Surface(
        onClick = {onClick()},
        color = containerColor,
        modifier = modifier,
        shape = RoundedCornerShape(10.dp)

    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text,
                modifier.padding(
                    horizontal = 116.dp, vertical = 14.dp
                ),
                color = textColor,
                style = MaterialTheme.typography.titleMedium

            )
        }
    }
}
@Composable
fun CustomOutlinedButton(
    modifier: Modifier = Modifier,
    text: String,
    borderColor: Color = Color.Black,
    textColor: Color = Color.Black,
    onClick: () -> Unit = {}
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(10.dp),
        color = Color.Transparent,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(
                    horizontal = 80.dp,
                    vertical = 10.dp
                ),
                color = textColor,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
