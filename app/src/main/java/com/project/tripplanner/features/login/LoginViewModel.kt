package com.project.tripplanner.features.login

import androidx.lifecycle.viewModelScope
import com.project.tripplanner.BaseViewModel
import com.project.tripplanner.Emitter
import com.project.tripplanner.MviDefaultErrorHandler
import com.project.tripplanner.features.login.LoginEvent.ScreenVisibleEvent
import com.project.tripplanner.utils.validators.EmailValidator
import com.project.tripplanner.navigation.NavigationEvent
import com.project.tripplanner.repositories.UserPrefRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.exceptions.UnknownRestException
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
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
                auth.signInWith(Email) {
                    email = event.userName
                    password = event.password
                }
                val supabaseAccessToken = auth.currentAccessTokenOrNull().orEmpty()
                userPrefRepository.saveUserAccessToken(supabaseAccessToken)
                navigate(NavigationEvent.Home)
            } catch (e: Exception) {
                println("Got an error")
            }
        }
    }

    private fun onScreenVisible(
        event: ScreenVisibleEvent,
        emit: Emitter<LoginUiState, LoginEffect>
    ) {
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
                    navigate(NavigationEvent.Home)
                } catch (ex: UnknownRestException) {
                    userPrefRepository.saveUserAccessToken("")
                    emit.state(LoginUiState.Login())
                }
            }
        }
    }

    private fun onForgotPassword(
        event: LoginEvent.ForgotPasswordClickedEvent,
        emit: Emitter<LoginUiState, LoginEffect>
    ) {
        navigate(NavigationEvent.ResetPassword)
    }

    private fun onRegisterClicked(
        event: LoginEvent.RegisterButtonClickedEvent,
        emit: Emitter<LoginUiState, LoginEffect>
    ) {
        navigate(NavigationEvent.RegisterForm)
    }

    private fun googleSignInSucceeded(
        event: LoginEvent.GoogleSignInSuccessEvent,
        emit: Emitter<LoginUiState, LoginEffect>
    ) {
        val supabaseAccessToken = auth.currentAccessTokenOrNull().orEmpty()
        userPrefRepository.saveUserAccessToken(supabaseAccessToken)
        navigate(NavigationEvent.Home)
    }

    private fun onGoogleSignInClicked(
        event: LoginEvent.OnGoogleSignInClickedEvent,
        emit: Emitter<LoginUiState, LoginEffect>
    ) {
        emit.effect(LoginEffect.StartGoogleSignInEffect)
    }


}