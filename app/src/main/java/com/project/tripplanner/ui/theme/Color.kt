package com.project.tripplanner.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class TripPlannerColorScheme(
    val primary: Color,
    val primaryStrong: Color,
    val primaryContainer: Color,
    val onPrimary: Color,
    val onPrimaryContainer: Color,
    val secondary: Color,
    val onSecondary: Color,
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val mutedSurface: Color,
    val surfaceVariant: Color,
    val onSurface: Color,
    val onSurfaceVariant: Color,
    val outline: Color,
    val border: Color,
    val divider: Color,
    val tertiaryContainer: Color,
    val onTertiaryContainer: Color,
    val error: Color,
    val onError: Color,
    val iconMuted: Color,
    val rating: Color,
    val scrim: Color,
)

@Immutable
data class AdditionalColorPalette(
    val warning: Color = Color.Unspecified,
    val onWarning: Color = Color.Unspecified,
    val warningContainer: Color = Color.Unspecified,
    val onWarningContainer: Color = Color.Unspecified,
    val success: Color = Color.Unspecified,
    val onSuccess: Color = Color.Unspecified,
    val successContainer: Color = Color.Unspecified,
    val onSuccessContainer: Color = Color.Unspecified,
    val inactive: Color = Color.Unspecified,
    val link: Color = Color.Unspecified
)

val LightColorScheme = TripPlannerColorScheme(
    primary = Color(0xFF5182FF), // primary.brand
    primaryStrong = Color(0xFF1F5AE7), // primary.brandDark
    primaryContainer = Color(0xFFE6EEFF), // primary.brandTint
    onPrimary = Color(0xFFFFFFFF),
    onPrimaryContainer = Color(0xFF1F5AE7),
    secondary = Color(0xFF6F7785), // neutrals.textSecondary
    onSecondary = Color(0xFFFFFFFF),
    background = Color(0xFFF0F1F6), // neutrals.background
    onBackground = Color(0xFF1B1D25), // neutrals.textPrimary
    surface = Color(0xFFFFFFFF), // neutrals.surface
    mutedSurface = Color(0xFFF7F8FD), // neutrals.mutedSurface
    surfaceVariant = Color(0xFFF7F8FD),
    onSurface = Color(0xFF1B1D25),
    onSurfaceVariant = Color(0xFF6F7785),
    outline = Color(0xFFE2E5ED), // neutrals.border
    border = Color(0xFFE2E5ED),
    divider = Color(0xFFECEEF3),
    tertiaryContainer = Color(0xFFE6E9F0), // neutral control/disabled
    onTertiaryContainer = Color(0xFF6F7785),
    error = Color(0xFFFF3B30), // semantic.danger
    onError = Color(0xFFFFFFFF),
    iconMuted = Color(0xFFC7CED8),
    rating = Color(0xFFF5B400),
    scrim = Color(0x66000000)
)

val DarkColorScheme = LightColorScheme.copy()

val AdditionalColorPaletteLight = AdditionalColorPalette(
    inactive = Color(0xFF9DA3AE), // neutrals.textTertiary
    success = Color(0xFF43C565),
    onSuccess = Color(0xFFFFFFFF),
    successContainer = Color(0xFFECFAF1),
    onSuccessContainer = Color(0xFF1D7C46),
    warning = Color(0xFFFF3B30),
    onWarning = Color(0xFFFFFFFF),
    onWarningContainer = Color(0xFFFF3B30),
    warningContainer = Color(0xFFFFF4F2),
    link = Color(0xFF1F5AE7)
)

val AdditionalColorPaletteDark = AdditionalColorPaletteLight

val LocalTripPlannerColorScheme = staticCompositionLocalOf { LightColorScheme }
val LocalAdditionalColorPalette = staticCompositionLocalOf { AdditionalColorPaletteLight }
