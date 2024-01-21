package com.project.tripplanner.login

import com.project.tripplanner.State

sealed class LoginUiState : State {
    object Loading : LoginUiState()
    data class Login(
        val userName: String = "",
        val password: String = ""
    ) : LoginUiState()

    data class GlobalError(val errorMessage: String) : LoginUiState()
}