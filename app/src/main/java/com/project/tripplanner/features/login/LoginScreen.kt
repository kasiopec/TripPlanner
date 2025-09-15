package com.project.tripplanner.features.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.tooling.preview.Preview
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.project.tripplanner.BuildConfig
import com.project.tripplanner.R
import com.project.tripplanner.features.login.LoginUiState.GlobalError
import com.project.tripplanner.features.login.LoginUiState.Login
import com.project.tripplanner.features.login.content.LoginScreenContent
import com.project.tripplanner.ui.components.FullScreenError
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    supabaseAuth: Auth
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val resources = LocalResources.current

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
                    // supabaseLogOutState.startFlow()
                    viewModel.emitEvent(LoginEvent.ForgotPasswordClickedEvent)
                },
                onRegisterClicked = {
                    viewModel.emitEvent(LoginEvent.RegisterButtonClickedEvent)
                },
                onGoogleSignInClicked = {
                    val rawNonce = UUID.randomUUID().toString()
                    val credentialManager = CredentialManager.create(context)

                    val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                        .setFilterByAuthorizedAccounts(false)
                        .setServerClientId(BuildConfig.SUPABASE_CLIENT_ID)
                        .setNonce(createNonce(rawNonce = rawNonce))
                        .build()

                    val request: GetCredentialRequest = GetCredentialRequest.Builder()
                        .addCredentialOption(googleIdOption)
                        .build()

                    coroutineScope.launch {
                        try {
                            val result = credentialManager.getCredential(
                                request = request,
                                context = context,
                            )
                            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
                            val googleIdToken = googleIdTokenCredential.idToken
                            supabaseAuth.signInWith(IDToken) {
                                idToken = googleIdToken
                                provider = Google
                                nonce = rawNonce
                            }
                            viewModel.emitEvent(LoginEvent.GoogleSignInSuccessEvent)
                        } catch (e: Exception) {
                            viewModel.emitEvent(LoginEvent.GoogleSignInFailureEvent(e.message ?: "Unknown error"))
                        }
                    }
                },
                userName = state.userName,
                password = state.password
            )
        }

        is GlobalError -> {
            FullScreenError(
                titleText = resources.getString(R.string.error_unknown_title),
                primaryActionText = resources.getString(R.string.error_unknown_primary_button_title),
                messageText = resources.getString(state.errorState.message),
                onPrimaryActionClick = {
                    viewModel.emitEvent(LoginEvent.CloseErrorClickedEvent)
                }
            )
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