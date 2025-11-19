package com.project.tripplanner.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColors = darkColorScheme(
    primary = TripPlannerPrimaryDark,
    onPrimary = TripPlannerOnPrimaryDark,
    primaryContainer = TripPlannerPrimaryContainerDark,
    onPrimaryContainer = TripPlannerOnPrimaryContainerDark,
    secondary = TripPlannerSecondaryDark,
    onSecondary = TripPlannerOnSecondaryDark,
    secondaryContainer = TripPlannerSecondaryContainerDark,
    onSecondaryContainer = TripPlannerOnSecondaryContainerDark,
    tertiary = TripPlannerTertiaryDark,
    onTertiary = TripPlannerOnTertiaryDark,
    tertiaryContainer = TripPlannerTertiaryContainerDark,
    onTertiaryContainer = TripPlannerOnTertiaryContainerDark,
    error = TripPlannerErrorDark,
    errorContainer = TripPlannerErrorContainerDark,
    onError = TripPlannerOnErrorDark,
    onErrorContainer = TripPlannerOnErrorContainerDark,
    background = TripPlannerBackgroundDark,
    onBackground = TripPlannerOnBackgroundDark,
    surface = TripPlannerSurfaceDark,
    onSurface = TripPlannerOnSurfaceDark,
    surfaceVariant = TripPlannerSurfaceVariantDark,
    onSurfaceVariant = TripPlannerOnSurfaceVariantDark,
    outline = TripPlannerOutlineDark,
    inverseOnSurface = TripPlannerInverseOnSurfaceDark,
    inverseSurface = TripPlannerInverseSurfaceDark,
    inversePrimary = TripPlannerInversePrimaryDark,
    surfaceTint = TripPlannerSurfaceTintDark,
    outlineVariant = TripPlannerOutlineVariantDark,
    scrim = TripPlannerScrimDark,
)

private val LightColors = lightColorScheme(
    primary = TripPlannerPrimaryLight,
    onPrimary = TripPlannerOnPrimaryLight,
    primaryContainer = TripPlannerPrimaryContainerLight,
    onPrimaryContainer = TripPlannerOnPrimaryContainerLight,
    secondary = TripPlannerSecondaryLight,
    onSecondary = TripPlannerOnSecondaryLight,
    secondaryContainer = TripPlannerSecondaryContainerLight,
    onSecondaryContainer = TripPlannerOnSecondaryContainerLight,
    tertiary = TripPlannerTertiaryLight,
    onTertiary = TripPlannerOnTertiaryLight,
    tertiaryContainer = TripPlannerTertiaryContainerLight,
    onTertiaryContainer = TripPlannerOnTertiaryContainerLight,
    error = TripPlannerErrorLight,
    errorContainer = TripPlannerErrorContainerLight,
    onError = TripPlannerOnErrorLight,
    onErrorContainer = TripPlannerOnErrorContainerLight,
    background = TripPlannerBackgroundLight,
    onBackground = TripPlannerOnBackgroundLight,
    surface = TripPlannerSurfaceLight,
    onSurface = TripPlannerOnSurfaceLight,
    surfaceVariant = TripPlannerSurfaceVariantLight,
    onSurfaceVariant = TripPlannerOnSurfaceVariantLight,
    outline = TripPlannerOutlineLight,
    inverseOnSurface = TripPlannerInverseOnSurfaceLight,
    inverseSurface = TripPlannerInverseSurfaceLight,
    inversePrimary = TripPlannerInversePrimaryLight,
    surfaceTint = TripPlannerSurfaceTintLight,
    outlineVariant = TripPlannerOutlineVariantLight,
    scrim = TripPlannerScrimLight,
)


@Composable
fun TripPlannerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }

        darkTheme -> DarkColors
        else -> LightColors
    }
    val additionalColorPalette = if (darkTheme) AdditionalColorPaletteDark else AdditionalColorPaletteLight
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }
    CompositionLocalProvider(LocalAdditionalColorPalette provides additionalColorPalette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = MaterialTypography,
            content = content
        )
    }
}