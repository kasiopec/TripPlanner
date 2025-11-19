package com.project.tripplanner.features.login

import androidx.lifecycle.viewModelScope
import com.project.tripplanner.BaseViewModel
import com.project.tripplanner.Emitter
import com.project.tripplanner.ErrorState
import com.project.tripplanner.MviDefaultErrorHandler
import com.project.tripplanner.features.login.LoginEvent.CloseErrorClickedEvent
import com.project.tripplanner.features.login.LoginEvent.ForgotPasswordClickedEvent
import com.project.tripplanner.features.login.LoginEvent.GoogleSignInFailureEvent
import com.project.tripplanner.features.login.LoginEvent.GoogleSignInSuccessEvent
import com.project.tripplanner.features.login.LoginEvent.RegisterButtonClickedEvent
import com.project.tripplanner.features.login.LoginEvent.ScreenVisibleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: Auth
) : BaseViewModel<LoginEvent, LoginUiState, LoginEffect>(
    initialState = LoginUiState.Loading
) {

    init {
        addEventHandler<ScreenVisibleEvent>(::onScreenVisible)
        addEventHandler<CloseErrorClickedEvent>(::onCloseErrorClicked)
        addEventHandler<LoginEvent.ForcedLogoutSessionExpiredEvent>(::onForcedLogout)
        addEventHandler(::onLoginClicked)
        addEventHandler<ForgotPasswordClickedEvent>(::onForgotPassword)
        addEventHandler<RegisterButtonClickedEvent>(::onRegisterClicked)
        addEventHandler<GoogleSignInSuccessEvent>(::googleSignInSucceeded)
        addEventHandler<GoogleSignInFailureEvent>(::googleSignInFailed)
        addErrorHandler(MviDefaultErrorHandler(LoginUiState::GlobalError))
    }

    private fun onLoginClicked(
        event: LoginEvent.LoginClickedEvent,
        emit: Emitter<LoginUiState, LoginEffect>
    ) {
        viewModelScope.launch {
            try {
                auth.signInWith(Email) {
                    email = event.userName
                    password = event.password
                }
                emit.effect(LoginEffect.NavigateToHomeScreenEffect)
            } catch (e: Exception) {
                emit.state(LoginUiState.GlobalError(errorState = ErrorState.UnknownError()))
            }
        }
    }

    private fun onScreenVisible(emit: Emitter<LoginUiState, LoginEffect>) {
        emit.state(LoginUiState.Login())
    }

    private fun onForgotPassword(emit: Emitter<LoginUiState, LoginEffect>) {
        emit.effect(LoginEffect.NavigateToResetPasswordScreenEffect)
    }

    private fun onRegisterClicked(emit: Emitter<LoginUiState, LoginEffect>) {
        emit.effect(LoginEffect.NavigateToRegisterFormEffect)
    }

    private fun googleSignInSucceeded(emit: Emitter<LoginUiState, LoginEffect>) {
        emit.effect(LoginEffect.NavigateToHomeScreenEffect)
    }

    private fun googleSignInFailed(emit: Emitter<LoginUiState, LoginEffect>) {
        emit.state(LoginUiState.GlobalError(errorState = ErrorState.UnknownError()))
    }

    private fun onCloseErrorClicked(emit: Emitter<LoginUiState, LoginEffect>) {
        emit.state(LoginUiState.Login())
    }

    private fun onForcedLogout(emit: Emitter<LoginUiState, LoginEffect>) {
        emit.state(LoginUiState.GlobalError(errorState = ErrorState.SessionExpiredError()))
    }
}