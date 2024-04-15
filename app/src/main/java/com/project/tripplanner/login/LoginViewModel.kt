package com.project.tripplanner.login

import androidx.lifecycle.viewModelScope
import com.project.tripplanner.BaseViewModel
import com.project.tripplanner.Emitter
import com.project.tripplanner.MviDefaultErrorHandler
import com.project.tripplanner.MviErrorHandler
import com.project.tripplanner.Unused
import com.project.tripplanner.login.LoginEvent.ScreenVisibleEvent
import com.project.tripplanner.navigation.NavigationEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.providers.Google
import io.ktor.client.request.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: Auth,
    private val composeAuth: ComposeAuth
) : BaseViewModel<LoginEvent, LoginUiState, LoginEffect>(
    initialState = LoginUiState.Loading
) {

    init {
        addEventHandler(::onScreenVisible)
        addEventHandler(::onLoginClicked)
        addEventHandler(::onForgotPassword)
        addEventHandler(::onRegisterClicked)
        addEventHandler(::googleSignInSucceeded)
        addEventHandler(::onGoogleSignInClicked)
        addErrorHandler(MviDefaultErrorHandler(LoginUiState::GlobalError))
    }

    private fun onLoginClicked(
        event: LoginEvent.LoginClickedEvent,
        emit: Emitter<LoginUiState, LoginEffect>
    ) {
        viewModelScope.launch {
            try {
                auth.signInWith(Google)
            } catch (e: Exception) {
                println("Got an error")
            }
        }
    }

    private fun onScreenVisible(
        event: ScreenVisibleEvent,
        emit: Emitter<LoginUiState, LoginEffect>
    ) {
        emit.state(LoginUiState.Login())
    }

    private fun onForgotPassword(
        event: LoginEvent.ForgotPasswordClickedEvent,
        emit: Emitter<LoginUiState, LoginEffect>
    ) {
        // do navigation to forgot password form
    }

    private fun onRegisterClicked(
        event: LoginEvent.RegisterButtonClickedEvent,
        emit: Emitter<LoginUiState, LoginEffect>
    ) {
        //emit.effect(LoginEffect.NavigateToRegisterFormEffect)
        navigate(NavigationEvent.RegisterForm)
    }

    private fun googleSignInSucceeded(
        event: LoginEvent.GoogleSignInSuccessEvent,
        emit: Emitter<LoginUiState, LoginEffect>
    ) {
        // save token
    }

    private fun onGoogleSignInClicked(
        event: LoginEvent.OnGoogleSignInClickedEvent,
        emit: Emitter<LoginUiState, LoginEffect>
    ) {
        emit.effect(LoginEffect.StartGoogleSignInEffect)
    }
}