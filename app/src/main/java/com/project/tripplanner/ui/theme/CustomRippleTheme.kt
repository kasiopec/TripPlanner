package com.project.tripplanner.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

class CustomRippleTheme(
    private val rippleColor: Color
) : RippleTheme {
    @Composable
    override fun defaultColor(): Color {
        return rippleColor
    }

    @Composable
    override fun rippleAlpha(): RippleAlpha {
        return RippleTheme.defaultRippleAlpha(
            contentColor = Color.Black,
            lightTheme = isSystemInDarkTheme().not()
        )
    }
}