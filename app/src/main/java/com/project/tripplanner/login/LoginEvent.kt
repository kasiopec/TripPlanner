package com.project.tripplanner.login

import com.project.tripplanner.Event

sealed class LoginEvent : Event {
    object PasswordValidateEvent : LoginEvent()
    data class LoginClickedEvent(val userName: String, val password: String) : LoginEvent()
    object ScreenVisibleEvent : LoginEvent()
    object ForgotPasswordClickedEvent : LoginEvent()
    object RegisterButtonClickedEvent : LoginEvent()
    object GoogleSignInButtonClickedEvent : LoginEvent()
}