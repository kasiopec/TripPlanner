package com.project.tripplanner.features.register

import androidx.lifecycle.viewModelScope
import com.project.tripplanner.BaseViewModel
import com.project.tripplanner.Emitter
import com.project.tripplanner.MviDefaultErrorHandler
import com.project.tripplanner.navigation.NavigationEvent
import com.project.tripplanner.repositories.UserPrefRepository
import com.project.tripplanner.utils.validators.EmailValidator
import com.project.tripplanner.utils.validators.PasswordValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val passwordValidator: PasswordValidator,
    private val emailValidator: EmailValidator,
    private val auth: Auth,
    private val userPrefRepository: UserPrefRepository
) : BaseViewModel<RegisterEvent, RegisterUiState, RegisterEffect>(
    initialState = RegisterUiState.Loading
) {
    init {
        addEventHandlerWithoutEvent<RegisterEvent.ScreenVisibleEvent> { emit ->
            onScreenVisible(emit)
        }
        addEventHandler(::onRegisterClicked)
        addEventHandlerWithoutEvent<RegisterEvent.BackClickedEvent> { emit ->
            onBackClicked(emit)
        }
        addEventHandlerWithoutEvent<RegisterEvent.LoginButtonClicked> { emit ->
            onLoginClicked(emit)
        }
        addErrorHandler(MviDefaultErrorHandler(RegisterUiState::GlobalError))
    }

    private fun onScreenVisible(emit: Emitter<RegisterUiState, RegisterEffect>) {
        emit.state(RegisterUiState.Register())
    }

    private fun onRegisterClicked(
        event: RegisterEvent.RegisterClickedEvent,
        emit: Emitter<RegisterUiState, RegisterEffect>
    ) {
        val validatedPassword = passwordValidator.isValid(event.password, event.secondPassword)
        val isEmailValid = emailValidator.isValid(event.email)
        println("password: ${event.password}")
        println("isEmailValid: $isEmailValid")
        when {
            validatedPassword.isValid.not() && isEmailValid.not() -> {
                emit.state(
                    RegisterUiState.Register(
                        passwordErrors = validatedPassword.errors,
                        isEmailValid = isEmailValid
                    )
                )
            }

            validatedPassword.isValid.not() -> {
                emit.state(RegisterUiState.Register(passwordErrors = validatedPassword.errors))
                println("pass not valid: ${validatedPassword.errors}")
            }

            isEmailValid.not() -> {
                emit.state(RegisterUiState.Register(isEmailValid = isEmailValid))
                println("email not valid")
            }

            validatedPassword.isValid && isEmailValid -> {
                viewModelScope.launch {
                    auth.signUpWith(Email) {
                        email = event.email
                        password = event.secondPassword
                    }
                    val supabaseAccessToken = auth.currentAccessTokenOrNull().orEmpty()
                    userPrefRepository.saveUserAccessToken(supabaseAccessToken)
                    emit.effect(RegisterEffect.Navigate(NavigationEvent.Home))
                }
            }
        }

    }

    private fun onBackClicked(
        emit: Emitter<RegisterUiState, RegisterEffect>
    ) {
        emit.effect(RegisterEffect.Navigate(NavigationEvent.Back))
    }

    private fun onLoginClicked(
        emit: Emitter<RegisterUiState, RegisterEffect>
    ) {
        emit.effect(RegisterEffect.Navigate(NavigationEvent.Login))
    }
}