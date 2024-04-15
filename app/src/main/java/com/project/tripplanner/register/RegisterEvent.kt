package com.project.tripplanner.register

import com.project.tripplanner.Event

sealed class RegisterEvent : Event {
    object OnRegisterClickedEvent : RegisterEvent()
    object ScreenVisibleEvent : RegisterEvent()
}
