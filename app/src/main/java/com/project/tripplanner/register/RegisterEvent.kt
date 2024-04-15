package com.project.tripplanner.register

import com.project.tripplanner.Event

sealed class RegisterEvent : Event {
    data class RegisterClickedEvent(
        val name: String,
        val email: String,
        val password: String,
        val secondPassword: String
    ) : RegisterEvent()

    object ScreenVisibleEvent : RegisterEvent()
    object BackClickedEvent : RegisterEvent()
}
