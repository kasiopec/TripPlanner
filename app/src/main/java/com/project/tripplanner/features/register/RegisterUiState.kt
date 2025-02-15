package com.project.tripplanner.features.register

import com.project.tripplanner.ErrorState
import com.project.tripplanner.State
import com.project.tripplanner.features.login.LoginUiState
import com.project.tripplanner.utils.validators.PasswordError

sealed class RegisterUiState : State {
    object Loading : RegisterUiState()
    data class Register(
        val name: String = "",
        val email: String = "",
        val password: String = "",
        val secondPassword: String = "",
        val passwordErrors: List<PasswordError> = emptyList(),
        val isEmailValid: Boolean = true
    ) : RegisterUiState()

    data class GlobalError(val errorState: ErrorState) : RegisterUiState()
}
