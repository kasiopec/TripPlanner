package com.project.tripplanner.features.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.project.tripplanner.features.login.LoginUiState.GlobalError
import com.project.tripplanner.features.login.LoginUiState.Login
import com.project.tripplanner.features.login.content.LoginScreenContent
import com.project.tripplanner.ui.components.text.TitleLargeBold
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult.ClosedByUser
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult.Error
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult.NetworkError
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult.Success
import io.github.jan.supabase.compose.auth.composable.rememberSignInWithGoogle
import io.github.jan.supabase.compose.auth.composable.rememberSignOutWithGoogle

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    supabaseComposeAuth: ComposeAuth
) {
    val supabaseAuthState = runCatching {
        supabaseComposeAuth.rememberSignInWithGoogle(
            onResult = { result ->
                when (result) {
                    ClosedByUser -> {
                        // do nothing
                    }

                    is Error -> {
                        println(result.message)
                    }

                    is NetworkError -> {
                        println("no internet")
                    }

                    Success -> {
                        viewModel.emitEvent(LoginEvent.GoogleSignInSuccessEvent)
                    }
                }
            },
            fallback = {}
        )
    }.onFailure {
        println("error in the onFailure")
    }
    val supabaseLogOutState = supabaseComposeAuth.rememberSignOutWithGoogle()

    LaunchedEffect(Unit) {
        viewModel.emitEvent(LoginEvent.ScreenVisibleEvent)
    }
    val uiState by viewModel.state.collectAsState()

    when (val state = uiState) {
        is Login -> {
            LoginScreenContent(
                onLoginButtonClicked = { userName, password ->
                    viewModel.emitEvent(
                        LoginEvent.LoginClickedEvent(
                            userName = userName,
                            password = password
                        )
                    )
                },
                onForgotPasswordClicked = {
                    supabaseLogOutState.startFlow()
                    viewModel.emitEvent(LoginEvent.ForgotPasswordClickedEvent)
                },
                onRegisterClicked = {
                    viewModel.emitEvent(LoginEvent.RegisterButtonClickedEvent)
                },
                onGoogleSignInClicked = {
                    supabaseAuthState.onSuccess { it.startFlow() }
                },
                userName = state.userName,
                password = state.password
            )
        }

        is GlobalError -> {
            val context = LocalContext.current
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.background)
            ) {
                TitleLargeBold(text = context.resources.getString(state.errorState.message))
            }
        }

        is LoginUiState.Loading -> {
            // do loading
        }
    }
}


@Preview(apiLevel = 29)
@Composable
fun LoginScreenPreview() {
    LoginScreenContent(
        onLoginButtonClicked = { _, _ -> },
        onForgotPasswordClicked = {},
        onRegisterClicked = {},
        onGoogleSignInClicked = {},
        userName = "Username",
        password = ""
    )
}