package com.project.tripplanner.features.register

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
    object LoginButtonClicked : RegisterEvent()
}
