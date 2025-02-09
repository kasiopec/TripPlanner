package com.project.tripplanner.navigation

sealed class NavigationEvent {
    object Home : NavigationEvent()
    object Login : NavigationEvent()
    object RegisterForm : NavigationEvent()
    object Back : NavigationEvent()
    object ResetPassword : NavigationEvent()
}
