package com.project.tripplanner.features.resetpassword

import com.project.tripplanner.State

sealed class ResetPasswordUiState : State {
    object Loading : ResetPasswordUiState()
    data class ResetPasswordState(val isEmailValid: Boolean = true) : ResetPasswordUiState()
    object EmailSentState : ResetPasswordUiState()
    object DeepLinkReceivedState : ResetPasswordUiState()
}
