package com.project.tripplanner.features.login

import androidx.lifecycle.viewModelScope
import com.project.tripplanner.BaseViewModel
import com.project.tripplanner.Emitter
import com.project.tripplanner.MviDefaultErrorHandler
import com.project.tripplanner.features.login.LoginEvent.ForgotPasswordClickedEvent
import com.project.tripplanner.features.login.LoginEvent.GoogleSignInSuccessEvent
import com.project.tripplanner.features.login.LoginEvent.RegisterButtonClickedEvent
import com.project.tripplanner.features.login.LoginEvent.ScreenVisibleEvent
import com.project.tripplanner.repositories.UserPrefRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.exceptions.UnknownRestException
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: Auth,
    private val userPrefRepository: UserPrefRepository
) : BaseViewModel<LoginEvent, LoginUiState, LoginEffect>(
    initialState = LoginUiState.Loading
) {

    init {
        addEventHandler<ScreenVisibleEvent>(::onScreenVisible)
        addEventHandler(::onLoginClicked)
        addEventHandler<ForgotPasswordClickedEvent>(::onForgotPassword)
        addEventHandler<RegisterButtonClickedEvent>(::onRegisterClicked)
        addEventHandler<GoogleSignInSuccessEvent>(::googleSignInSucceeded)
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
                val supabaseAccessToken = auth.currentAccessTokenOrNull().orEmpty()
                userPrefRepository.saveUserAccessToken(supabaseAccessToken)
                emit.effect(LoginEffect.NavigateToHomeScreenEffect)
            } catch (e: Exception) {
                println("Got an error")
            }
        }
    }

    private fun onScreenVisible(emit: Emitter<LoginUiState, LoginEffect>) {
        val supabaseAccessToken = userPrefRepository.getUserAccessToken()
        if (supabaseAccessToken.isNullOrEmpty()) {
            emit.state(LoginUiState.Login())
        } else {
            viewModelScope.launch {
                try {
                    auth.retrieveUser(supabaseAccessToken)
                    auth.refreshCurrentSession()
                    val refreshedAccessToken = auth.currentAccessTokenOrNull().orEmpty()
                    userPrefRepository.saveUserAccessToken(refreshedAccessToken)
                    emit.effect(LoginEffect.NavigateToHomeScreenEffect)
                } catch (ex: UnknownRestException) {
                    userPrefRepository.saveUserAccessToken("")
                    emit.state(LoginUiState.Login())
                }
            }
        }
    }

    private fun onForgotPassword(emit: Emitter<LoginUiState, LoginEffect>) {
        emit.effect(LoginEffect.NavigateToResetPasswordScreenEffect)
    }

    private fun onRegisterClicked(emit: Emitter<LoginUiState, LoginEffect>) {
        emit.effect(LoginEffect.NavigateToRegisterFormEffect)
    }

    private fun googleSignInSucceeded(emit: Emitter<LoginUiState, LoginEffect>) {
        val supabaseAccessToken = auth.currentAccessTokenOrNull().orEmpty()
        userPrefRepository.saveUserAccessToken(supabaseAccessToken)
        emit.effect(LoginEffect.NavigateToHomeScreenEffect)
    }
}