package com.project.tripplanner.features.register

import com.project.tripplanner.BaseViewModel
import com.project.tripplanner.Emitter
import com.project.tripplanner.MviDefaultErrorHandler
import com.project.tripplanner.features.register.RegisterEvent.BackClickedEvent
import com.project.tripplanner.features.register.RegisterEvent.LoginButtonClicked
import com.project.tripplanner.features.register.RegisterEvent.ScreenVisibleEvent
import com.project.tripplanner.repositories.UserPrefRepository
import com.project.tripplanner.utils.validators.EmailValidator
import com.project.tripplanner.utils.validators.PasswordValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.providers.builtin.Email
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
        addEventHandler<ScreenVisibleEvent>(::onScreenVisible)
        addEventHandler(::onRegisterClicked)
        addEventHandler<BackClickedEvent>(::onBackClicked)
        addEventHandler<LoginButtonClicked>(::onLoginClicked)
        addErrorHandler(MviDefaultErrorHandler(RegisterUiState::GlobalError))
    }

    private fun onScreenVisible(emit: Emitter<RegisterUiState, RegisterEffect>) {
        emit.state(RegisterUiState.Register())
    }

    private suspend fun onRegisterClicked(
        event: RegisterEvent.RegisterClickedEvent,
        emit: Emitter<RegisterUiState, RegisterEffect>
    ) {
        val validatedPassword = passwordValidator.isValid(event.password, event.secondPassword)
        val isEmailValid = emailValidator.isValid(event.email)
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
            }

            isEmailValid.not() -> {
                emit.state(RegisterUiState.Register(isEmailValid = isEmailValid))
            }

            validatedPassword.isValid && isEmailValid -> {
                auth.signUpWith(Email) {
                    email = event.email
                    password = event.secondPassword
                }
                val supabaseAccessToken = auth.currentAccessTokenOrNull().orEmpty()
                userPrefRepository.saveUserAccessToken(supabaseAccessToken)
                emit.effect(RegisterEffect.GoToHome)
            }
        }
    }

    private fun onBackClicked(emit: Emitter<RegisterUiState, RegisterEffect>) {
        emit.effect(RegisterEffect.GoBack)
    }

    private fun onLoginClicked(emit: Emitter<RegisterUiState, RegisterEffect>) {
        emit.effect(RegisterEffect.GoToLogin)
    }
}