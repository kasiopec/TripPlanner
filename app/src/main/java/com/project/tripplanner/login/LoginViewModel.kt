package com.project.tripplanner.login

import com.project.tripplanner.BaseViewModel
import com.project.tripplanner.Emitter
import com.project.tripplanner.Unused
import com.project.tripplanner.login.LoginEvent.ScreenVisibleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : BaseViewModel<LoginEvent, LoginUiState, Unused>(
    initialState = LoginUiState.Loading
) {

    init {
        addEventHandler(::onScreenVisible)
        addEventHandler(::onLoginClicked)
        addEventHandler(::onForgotPassword)
        addEventHandler(::onRegisterClicked)
        addEventHandler(::onGoogleSignInClicked)
    }

    private fun onLoginClicked(
        event: LoginEvent.LoginClickedEvent,
        emit: Emitter<LoginUiState, Unused>
    ) {
        emit.updatedState<LoginUiState.Login> {
            it.copy(userName = "123")
        }
    }

    private fun onScreenVisible(
        event: ScreenVisibleEvent,
        emit: Emitter<LoginUiState, Unused>
    ) {
        emit.state(LoginUiState.Login())
    }

    private fun onForgotPassword(
        event: LoginEvent.ForgotPasswordClickedEvent,
        emit: Emitter<LoginUiState, Unused>
    ) {
        // do navigation to forgot password form
    }

    private fun onRegisterClicked(
        event: LoginEvent.RegisterButtonClickedEvent,
        emit: Emitter<LoginUiState, Unused>
    ) {
        // do navigation to register form
    }

    private fun onGoogleSignInClicked(
        event: LoginEvent.GoogleSignInSuccessEvent,
        emit: Emitter<LoginUiState, Unused>
    ) {
        // open google account picker
    }
}