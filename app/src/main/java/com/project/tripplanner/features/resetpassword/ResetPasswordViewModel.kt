package com.project.tripplanner.features.resetpassword

import androidx.lifecycle.viewModelScope
import com.project.tripplanner.BaseViewModel
import com.project.tripplanner.Emitter
import com.project.tripplanner.Unused
import com.project.tripplanner.utils.validators.EmailValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.gotrue.Auth
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val emailValidator: EmailValidator,
    private val auth: Auth
) : BaseViewModel<ResetPasswordEvent, ResetPasswordUiState, Unused>(
    initialState = ResetPasswordUiState.Loading
) {
    init {
        addEventHandler(::onScreenVisible)
        addEventHandler(::onResetPasswordClicked)
    }

    private fun onScreenVisible(
        event: ResetPasswordEvent.ScreenAppearedEvent,
        emit: Emitter<ResetPasswordUiState, Unused>
    ) {
        emit.state(ResetPasswordUiState.ResetPasswordState())
    }

    private fun onResetPasswordClicked(
        event: ResetPasswordEvent.ResetPasswordClickedEvent,
        emit: Emitter<ResetPasswordUiState, Unused>
    ) {
        val isEmailValid = emailValidator.isValid(event.email)
        if (isEmailValid) {
            viewModelScope.launch {
                auth.resetPasswordForEmail(event.email)
                emit.state(ResetPasswordUiState.EmailSentState)
            }
        } else {
            emit.state(ResetPasswordUiState.ResetPasswordState(isEmailValid = false))
        }
    }
}