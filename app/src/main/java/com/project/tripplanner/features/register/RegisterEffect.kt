package com.project.tripplanner.features.register

import com.project.tripplanner.Effect
import com.project.tripplanner.navigation.NavigationEvent

sealed class RegisterEffect : Effect {
    data class Navigate(val navigationEvent: NavigationEvent) : RegisterEffect()
}