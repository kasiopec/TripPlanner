package com.project.tripplanner.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

object TripPlannerTheme {
    val colors: TripPlannerColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalTripPlannerColorScheme.current

    val additionalColors: AdditionalColorPalette
        @Composable
        @ReadOnlyComposable
        get() = LocalAdditionalColorPalette.current

    val typography: ThemeTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalTripTypography.current
}

@Composable
fun TripPlannerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val additionalColorPalette = if (darkTheme) AdditionalColorPaletteDark else AdditionalColorPaletteLight
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    CompositionLocalProvider(
        LocalTripPlannerColorScheme provides if (darkTheme) DarkColorScheme else LightColorScheme,
        LocalAdditionalColorPalette provides additionalColorPalette,
        LocalTripTypography provides TripPlannerTypography,
        content = content
    )
}
