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
        // Demonstrate different handler types
        addSimpleEventHandler<RegisterEvent.ScreenVisibleEvent> { emit ->
            emit.state(RegisterUiState.Register())
        }

        addEventHandler(::onRegisterClicked)

        // Simple navigation handlers using new approach
        addNoParamHandler<RegisterEvent.BackClickedEvent> {
            emitEffect(RegisterEffect.NavigateBack)
        }

        addNoParamHandler<RegisterEvent.LoginButtonClicked> {
            emitEffect(RegisterEffect.NavigateToLogin)
        }

        addErrorHandler(MviDefaultErrorHandler(RegisterUiState::GlobalError))
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
                viewModelScope.launch {
                    auth.signUpWith(Email) {
                        email = event.email
                        password = event.secondPassword
                    }
                    val supabaseAccessToken = auth.currentAccessTokenOrNull().orEmpty()
                    userPrefRepository.saveUserAccessToken(supabaseAccessToken)

                    // Use the new effect-based navigation
                    emit.effect(RegisterEffect.NavigateToHome)
                }
            }
        }
    }
}