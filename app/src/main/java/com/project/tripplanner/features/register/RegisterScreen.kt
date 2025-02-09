package com.project.tripplanner.features.register

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.project.tripplanner.utils.validators.PasswordError
import com.project.tripplanner.utils.validators.PasswordError.*
import com.project.tripplanner.features.register.RegisterEvent.BackClickedEvent
import com.project.tripplanner.features.register.RegisterEvent.RegisterClickedEvent
import com.project.tripplanner.ui.IcBackArrow24
import com.project.tripplanner.ui.IcError24
import com.project.tripplanner.ui.Icons
import com.project.tripplanner.ui.components.BaseOutlinedTextField
import com.project.tripplanner.ui.components.LargeRoundedButton
import com.project.tripplanner.ui.components.PasswordTextField
import com.project.tripplanner.ui.components.TextWithLeftIcon
import com.project.tripplanner.ui.components.text.BodyMedium

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel
) {
    LaunchedEffect(Unit) {
        viewModel.emitEvent(RegisterEvent.ScreenVisibleEvent)
    }
    val uiState by viewModel.state.collectAsState()
    val loadingState = uiState as? RegisterUiState.Loading
    val registerState = uiState as? RegisterUiState.Register

    BackHandler(enabled = true) {
        viewModel.emitEvent(BackClickedEvent)
    }

    when {
        registerState != null -> RegisterContent(
            onNavigateBack = { viewModel.emitEvent(BackClickedEvent) },
            onRegisterClicked = { name, email, password, secondPassword ->
                viewModel.emitEvent(
                    RegisterClickedEvent(
                        name = name,
                        email = email,
                        password = password,
                        secondPassword = secondPassword
                    )
                )
            },
            onLoginButtonClicked = { viewModel.emitEvent(RegisterEvent.LoginButtonClicked) },
            isEmailValid = registerState.isEmailValid,
            passwordErrors = registerState.passwordErrors,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterContent(
    onNavigateBack: () -> Unit,
    onRegisterClicked: (name: String, email: String, password: String, secondPassword: String) -> Unit,
    onLoginButtonClicked: () -> Unit,
    isEmailValid: Boolean,
    passwordErrors: List<PasswordError>,
) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack() }) {
                        Icons.IcBackArrow24(tint = MaterialTheme.colorScheme.onBackground)
                    }
                }
            )
        }
    ) { innerPadding ->
        var enteredName by remember { mutableStateOf("") }
        var enteredPassword by remember { mutableStateOf("") }
        var enteredSecondPassword by remember { mutableStateOf("") }
        var enteredEmail by remember { mutableStateOf("") }
        val isEmailError by remember(isEmailValid) { mutableStateOf(!isEmailValid) }
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                BaseOutlinedTextField(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = { enteredName },
                    onTextChanged = { enteredName = it },
                    labelText = context.resources.getString(R.string.name_hint),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    )
                )
                BaseOutlinedTextField(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = { enteredEmail },
                    onTextChanged = { enteredEmail = it },
                    labelText = context.resources.getString(R.string.email_hint),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    )
                )
                PasswordTextField(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp),
                    password = { enteredPassword },
                    onPasswordTextChanged = { enteredPassword = it },
                    isError = passwordErrors.isNotEmpty(),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    labelText = context.resources.getString(R.string.password_hint)
                )
                PasswordTextField(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp),
                    password = { enteredSecondPassword },
                    onPasswordTextChanged = { enteredSecondPassword = it },
                    isError = passwordErrors.isNotEmpty(),
                    keyboardActions = KeyboardActions(
                        onGo = {
                            onRegisterClicked(enteredName, enteredEmail, enteredPassword, enteredSecondPassword)
                        }
                    ),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Go
                    ),
                    labelText = context.resources.getString(R.string.password_hint)
                )
                LazyColumn(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp)) {
                    item {
                        if (isEmailError) {
                            TextWithLeftIcon(
                                modifier = Modifier.fillMaxWidth(),
                                icon = { Icons.IcError24(tint = MaterialTheme.colorScheme.error) },
                                text = context.getString(R.string.error_email_wrong),
                                textColor = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    items(count = passwordErrors.size) { index ->
                        val errorMessage = when (passwordErrors[index]) {
                            ERROR_LENGTH -> context.getString(R.string.error_password_length)
                            ERROR_WHITESPACE -> context.getString(R.string.error_password_white_space)
                            ERROR_DIGIT -> context.getString(R.string.error_password_no_digit)
                            ERROR_UPPER -> context.getString(R.string.error_password_no_upper_case)
                            ERROR_SPECIAL -> context.getString(R.string.error_password_special_char)
                            ERROR_NOT_SAME -> context.getString(R.string.error_password_repeat)
                        }
                        TextWithLeftIcon(
                            modifier = Modifier.fillMaxWidth(),
                            icon = { Icons.IcError24(tint = MaterialTheme.colorScheme.error) },
                            text = errorMessage,
                            textColor = MaterialTheme.colorScheme.error
                        )
                    }
                }
                LargeRoundedButton(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
                    text = context.resources.getString(R.string.register_button_label),
                    onClick = { onRegisterClicked(enteredName, enteredEmail, enteredPassword, enteredSecondPassword) }
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
                        text = context.resources.getString(R.string.register_have_account_label)
                    )
                    BodyMedium(
                        modifier = Modifier.clickable { onLoginButtonClicked() },
                        text = context.resources.getString(R.string.login_button_label),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}