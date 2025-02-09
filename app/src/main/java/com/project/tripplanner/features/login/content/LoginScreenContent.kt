package com.project.tripplanner.features.login.content

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.project.tripplanner.R
import com.project.tripplanner.ui.components.BaseOutlinedTextField
import com.project.tripplanner.ui.components.GoogleSignInButton
import com.project.tripplanner.ui.components.LargeRoundedButton
import com.project.tripplanner.ui.components.LoginSeparator
import com.project.tripplanner.ui.components.PasswordTextField
import com.project.tripplanner.ui.components.text.BodyMedium
import com.project.tripplanner.ui.theme.additionalColorPalette


@Composable
fun LoginScreenContent(
    onLoginButtonClicked: (userName: String, password: String) -> Unit,
    onForgotPasswordClicked: () -> Unit,
    onRegisterClicked: () -> Unit,
    onGoogleSignInClicked: () -> Unit,
    userName: String,
    password: String
) {
    var currentUserName by remember(userName) { mutableStateOf(userName) }
    var currentPassword by remember { mutableStateOf(password) }

    val context = LocalContext.current
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))
            BaseOutlinedTextField(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = { currentUserName },
                onTextChanged = { currentUserName = it },
                labelText = context.resources.getString(R.string.email_hint),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                )
            )
            PasswordTextField(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp),
                password = { currentPassword },
                onPasswordTextChanged = { currentPassword = it },
                keyboardActions = KeyboardActions(
                    onGo = {
                        onLoginButtonClicked(currentUserName, currentPassword)
                    }
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Go
                ),
                labelText = context.resources.getString(R.string.password_hint)
            )
            LargeRoundedButton(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
                text = context.resources.getString(R.string.login_button_label),
                isEnabled = currentUserName.isNotEmpty() && currentPassword.isNotEmpty(),
                onClick = { onLoginButtonClicked(currentUserName, currentPassword) }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                BodyMedium(
                    modifier = Modifier.clickable { onForgotPasswordClicked() },
                    text = context.resources.getString(R.string.forgot_password_label),
                    color = MaterialTheme.additionalColorPalette.link
                )
            }
            LoginSeparator(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp)
            )
            GoogleSignInButton(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp),
                onClick = { onGoogleSignInClicked() }
            )
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, bottom = 32.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                BodyMedium(
                    modifier = Modifier.padding(end = 4.dp),
                    text = context.resources.getString(R.string.login_no_account_label)
                )
                BodyMedium(
                    modifier = Modifier.clickable { onRegisterClicked() },
                    text = context.resources.getString(R.string.register_button_label),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}