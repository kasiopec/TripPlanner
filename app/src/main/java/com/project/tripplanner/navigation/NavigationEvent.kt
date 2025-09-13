package com.project.tripplanner.navigation

import com.project.tripplanner.Effect

sealed class NavigationEvent {
    object Home : NavigationEvent()
    object Login : NavigationEvent()
    object RegisterForm : NavigationEvent()
    object Back : NavigationEvent()
    object ResetPassword : NavigationEvent()
}

sealed class NavigationEffect : Effect {
    object NavigateToHome : NavigationEffect()
    object NavigateToLogin : NavigationEffect()
    object NavigateToRegisterForm : NavigationEffect()
    object NavigateBack : NavigationEffect()
    object NavigateToResetPassword : NavigationEffect()
}
