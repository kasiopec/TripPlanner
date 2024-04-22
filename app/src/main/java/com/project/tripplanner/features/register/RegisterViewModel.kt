package com.project.tripplanner.features.register

import androidx.lifecycle.viewModelScope
import com.project.tripplanner.BaseViewModel
import com.project.tripplanner.Emitter
import com.project.tripplanner.MviDefaultErrorHandler
import com.project.tripplanner.Unused
import com.project.tripplanner.data.UserPrefsStorage
import com.project.tripplanner.features.login.LoginUiState
import com.project.tripplanner.navigation.NavigationEvent
import com.project.tripplanner.features.register.validators.EmailValidator
import com.project.tripplanner.features.register.validators.PasswordValidator
import com.project.tripplanner.repositories.UserPrefRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val passwordValidator: PasswordValidator,
    private val emailValidator: EmailValidator,
    private val auth: Auth,
    private val userPrefRepository: UserPrefRepository
) : BaseViewModel<RegisterEvent, RegisterUiState, Unused>(
    initialState = RegisterUiState.Loading
) {
    init {
        addEventHandler(::onScreenVisible)
        addEventHandler(::onRegisterClicked)
        addEventHandler(::onBackClicked)
        addEventHandler(::onLoginClicked)
        addErrorHandler(MviDefaultErrorHandler(RegisterUiState::GlobalError))
    }

    private fun onScreenVisible(
        event: RegisterEvent.ScreenVisibleEvent,
        emit: Emitter<RegisterUiState, Unused>
    ) {
        emit.state(RegisterUiState.Register())
    }

    private fun onRegisterClicked(
        event: RegisterEvent.RegisterClickedEvent,
        emit: Emitter<RegisterUiState, Unused>
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
                    navigate(NavigationEvent.Home)
                }
            }
        }

    }

    private fun onBackClicked(
        event: RegisterEvent.BackClickedEvent,
        emit: Emitter<RegisterUiState, Unused>
    ) {
        navigate(NavigationEvent.Back)
    }

    private fun onLoginClicked(
        event: RegisterEvent.LoginButtonClicked,
        emit: Emitter<RegisterUiState, Unused>
    ) {
        navigate(NavigationEvent.Login)
    }
}