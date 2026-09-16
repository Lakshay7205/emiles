package com.state.greenmiles.com_state_greenmiles.presentation.screens.congratulations_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com_state_greenmiles.composeapp.generated.resources.Res
import com_state_greenmiles.composeapp.generated.resources.compose_multiplatform
import com_state_greenmiles.composeapp.generated.resources.logo
import com_state_greenmiles.composeapp.generated.resources.taxi_logo
import com_state_greenmiles.composeapp.generated.resources.tickicon
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun CongratulationsScreen(){
    Surface {
        Column(
            modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
            painter = painterResource(Res.drawable.tickicon),
            contentDescription = "Success Image",
            modifier = Modifier.size(40.dp)
        )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Congratulations!",
                fontWeight = FontWeight.Normal,
                fontSize = 24.sp
            )
            Spacer(Modifier.height(6.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Your validation is",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.W200
                )
                Text(
                    text = "successful",
                    fontSize = 17.sp,
                    modifier = Modifier.padding(start = 19.dp),
                    fontWeight = FontWeight.W200
                )


            }
        }
    }}