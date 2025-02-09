package com.project.tripplanner.features.resetpassword.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.project.tripplanner.R
import com.project.tripplanner.ui.IcError24
import com.project.tripplanner.ui.Icons
import com.project.tripplanner.ui.components.BaseOutlinedTextField
import com.project.tripplanner.ui.components.LargeRoundedButton
import com.project.tripplanner.ui.components.TextWithLeftIcon

@Composable
fun ResetPasswordContent(
    onResetPasswordClicked: (email: String) -> Unit,
    isEmailValid: Boolean,
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        var enteredEmail by remember { mutableStateOf("") }
        val context = LocalContext.current
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            BaseOutlinedTextField(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = { enteredEmail },
                onTextChanged = { enteredEmail = it },
                labelText = context.resources.getString(R.string.email_hint),
                isError = isEmailValid,
                keyboardActions = KeyboardActions(
                    onGo = {
                        onResetPasswordClicked(enteredEmail)
                    }
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Go
                )
            )
            if (!isEmailValid) {
                TextWithLeftIcon(
                    modifier = Modifier.fillMaxWidth(),
                    icon = { Icons.IcError24(tint = MaterialTheme.colorScheme.error) },
                    text = context.getString(R.string.error_email_wrong),
                    textColor = MaterialTheme.colorScheme.error
                )
            }
            LargeRoundedButton(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
                text = context.resources.getString(R.string.reset_password_button_label),
                isEnabled = enteredEmail.isNotEmpty(),
                onClick = { onResetPasswordClicked(enteredEmail) }
            )
        }
    }
}