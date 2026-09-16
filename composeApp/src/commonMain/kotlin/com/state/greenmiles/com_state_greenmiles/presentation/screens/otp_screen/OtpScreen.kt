package com.state.greenmiles.com_state_greenmiles.presentation.screens.otp_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.state.greenmiles.com_state_greenmiles.presentation.common.CustomButton
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun OtpScreen(
    mobileNumber: String = "", navController: NavHostController,
) {
    var otp by remember { mutableStateOf("")}
    Surface {
        Column (horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()){
            Text(
                text = "Welcome",
                fontSize = 34.sp,
                color = Color.Black,
                modifier = Modifier.padding(top=152.dp, start = 38.dp).fillMaxWidth(),
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(120.dp))
            Text(
                text = "Enter the OTP we’ve sent to your phone number.",
                fontSize = 14.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField( colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                cursorColor = Color.Black
            ),
                value = otp,
                onValueChange = { otp= it },
                placeholder = {
                    Text("OTP"  , color = Color.Black.copy(alpha = 0.4f)
                    )
                },
                modifier = Modifier
                    .height(60.dp).width(285.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone
                ),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(26.dp))
            CustomButton(text = "Verify" )



        }
    }
}