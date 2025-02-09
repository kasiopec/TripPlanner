package com.project.tripplanner.features.resetpassword

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.project.tripplanner.features.resetpassword.content.ResetPasswordContent
import com.project.tripplanner.features.resetpassword.content.EmailSentContent

@Composable
fun ResetPasswordScreen(
    viewModel: ResetPasswordViewModel
) {
    LaunchedEffect(Unit) {
        viewModel.emitEvent(ResetPasswordEvent.ScreenAppearedEvent())
    }
    val uiState by viewModel.state.collectAsState()

    when (val state = uiState) {
        ResetPasswordUiState.DeepLinkReceivedState -> {
            // show deeplinked ui state
        }

        ResetPasswordUiState.EmailSentState -> {
            EmailSentContent()
        }

        ResetPasswordUiState.Loading -> {
            //show loader
        }

        is ResetPasswordUiState.ResetPasswordState -> {
            ResetPasswordContent(
                onResetPasswordClicked = {
                    viewModel.emitEvent(ResetPasswordEvent.ResetPasswordClickedEvent(it))
                },
                isEmailValid = state.isEmailValid
            )
        }
    }
}