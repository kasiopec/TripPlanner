package com.project.tripplanner.login

import com.project.tripplanner.Effect

sealed class LoginEffect : Effect {
    object NavigateToRegisterFormEffect : LoginEffect()
    object StartGoogleSignInEffect : LoginEffect()
}