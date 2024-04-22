package com.project.tripplanner.features.login

import com.project.tripplanner.Effect

sealed class LoginEffect : Effect {
    object NavigateToRegisterFormEffect : LoginEffect()
    object StartGoogleSignInEffect : LoginEffect()
}