package com.project.tripplanner.register

import com.project.tripplanner.BaseViewModel
import com.project.tripplanner.Emitter
import com.project.tripplanner.Unused
import com.project.tripplanner.navigation.NavigationEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val passwordValidator: PasswordValidator,
    private val emailValidator: EmailValidator
) : BaseViewModel<RegisterEvent, RegisterUiState, Unused>(
    initialState = RegisterUiState.Loading
) {
    init {
        addEventHandler(::onScreenVisible)
        addEventHandler(::onRegisterClicked)
        addEventHandler(::onBackClicked)
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
                println("ALL OK")
                // supabase creation
            }
        }

    }

    private fun onBackClicked(
        event: RegisterEvent.BackClickedEvent,
        emit: Emitter<RegisterUiState, Unused>
    ) {
        navigate(NavigationEvent.Back)
    }
}