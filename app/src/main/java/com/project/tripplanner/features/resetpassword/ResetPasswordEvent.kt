package com.project.tripplanner.features.resetpassword

import com.project.tripplanner.Event

sealed class ResetPasswordEvent : Event {
    data class ScreenAppearedEvent(val isFromDeepLink: Boolean = false) : ResetPasswordEvent()
    data class ResetPasswordClickedEvent(val email: String) : ResetPasswordEvent()
}
