package com.project.tripplanner.register

import com.project.tripplanner.State

sealed class RegisterUiState : State {
    object Loading : RegisterUiState()
    data class Register(
        val name: String = "",
        val email: String = "",
        val password: String = "",
        val passwordRepeat: String = ""
    ) : RegisterUiState()
}
