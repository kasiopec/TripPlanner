package com.project.tripplanner.features.register

import com.project.tripplanner.Effect

sealed interface RegisterEffect : Effect {
    data object GoToHome : RegisterEffect
    data object GoBack : RegisterEffect
    data object GoToLogin : RegisterEffect
}
