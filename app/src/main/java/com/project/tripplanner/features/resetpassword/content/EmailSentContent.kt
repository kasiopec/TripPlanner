package com.project.tripplanner.features.resetpassword.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import com.project.tripplanner.ui.components.text.BodyMedium

@Composable
fun EmailSentContent() {
    Surface {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            BodyMedium(text = "Hardcoded, yay you have reset password, check your email!")
        }
    }
}