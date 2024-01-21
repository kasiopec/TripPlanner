package com.project.tripplanner.navigation

sealed class NavigationEvent {
    object Home : NavigationEvent()
    object Login : NavigationEvent()
}
