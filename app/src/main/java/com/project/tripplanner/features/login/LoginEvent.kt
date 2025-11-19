package com.project.tripplanner.features.login

import com.project.tripplanner.Event

sealed class LoginEvent : Event {
    object PasswordValidateEvent : LoginEvent()
    data class LoginClickedEvent(val userName: String, val password: String) : LoginEvent()
    object ScreenVisibleEvent : LoginEvent()
    object ForgotPasswordClickedEvent : LoginEvent()
    data class ResetPasswordClickedEvent(val email: String) : LoginEvent()
    object RegisterButtonClickedEvent : LoginEvent()
    object GoogleSignInSuccessEvent : LoginEvent()
    object CloseErrorClickedEvent : LoginEvent()
    data class GoogleSignInFailureEvent(val errorMessage: String) : LoginEvent()
    object ForcedLogoutSessionExpiredEvent : LoginEvent()
}