package com.project.tripplanner.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val md_theme_light_primary = Color(0xFF004AF2)
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_primaryContainer = Color(0xFFDDE1FF)
val md_theme_light_onPrimaryContainer = Color(0xFF001454)
val md_theme_light_secondary = Color(0xFF7C5800)
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_secondaryContainer = Color(0xFFFFDEA8)
val md_theme_light_onSecondaryContainer = Color(0xFF271900)
val md_theme_light_tertiary = Color(0xFF1160A4)
val md_theme_light_onTertiary = Color(0xFFFFFFFF)
val md_theme_light_tertiaryContainer = Color(0xFFD3E4FF)
val md_theme_light_onTertiaryContainer = Color(0xFF001C38)
val md_theme_light_error = Color(0xFFBA1A1A)
val md_theme_light_errorContainer = Color(0xFFFFDAD6)
val md_theme_light_onError = Color(0xFFFFFFFF)
val md_theme_light_onErrorContainer = Color(0xFF410002)
val md_theme_light_background = Color(0xFFF8FDFF)
val md_theme_light_onBackground = Color(0xFF001F25)
val md_theme_light_surface = Color(0xFFF8FDFF)
val md_theme_light_onSurface = Color(0xFF001F25)
val md_theme_light_surfaceVariant = Color(0xFFE2E1EC)
val md_theme_light_onSurfaceVariant = Color(0xFF45464F)
val md_theme_light_outline = Color(0xFF767680)
val md_theme_light_inverseOnSurface = Color(0xFFD6F6FF)
val md_theme_light_inverseSurface = Color(0xFF00363F)
val md_theme_light_inversePrimary = Color(0xFFB8C4FF)
val md_theme_light_shadow = Color(0xFF000000)
val md_theme_light_surfaceTint = Color(0xFF004AF2)
val md_theme_light_outlineVariant = Color(0xFFC6C5D0)
val md_theme_light_scrim = Color(0xFF000000)

val md_theme_dark_primary = Color(0xFFB8C4FF)
val md_theme_dark_onPrimary = Color(0xFF002585)
val md_theme_dark_primaryContainer = Color(0xFF0037BA)
val md_theme_dark_onPrimaryContainer = Color(0xFFDDE1FF)
val md_theme_dark_secondary = Color(0xFFFFBA20)
val md_theme_dark_onSecondary = Color(0xFF412D00)
val md_theme_dark_secondaryContainer = Color(0xFF5E4200)
val md_theme_dark_onSecondaryContainer = Color(0xFFFFDEA8)
val md_theme_dark_tertiary = Color(0xFFA1C9FF)
val md_theme_dark_onTertiary = Color(0xFF00325B)
val md_theme_dark_tertiaryContainer = Color(0xFF004880)
val md_theme_dark_onTertiaryContainer = Color(0xFFD3E4FF)
val md_theme_dark_error = Color(0xFFFFB4AB)
val md_theme_dark_errorContainer = Color(0xFF93000A)
val md_theme_dark_onError = Color(0xFF690005)
val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)
val md_theme_dark_background = Color(0xFF001F25)
val md_theme_dark_onBackground = Color(0xFFA6EEFF)
val md_theme_dark_surface = Color(0xFF001F25)
val md_theme_dark_onSurface = Color(0xFFA6EEFF)
val md_theme_dark_surfaceVariant = Color(0xFF45464F)
val md_theme_dark_onSurfaceVariant = Color(0xFFC6C5D0)
val md_theme_dark_outline = Color(0xFF90909A)
val md_theme_dark_inverseOnSurface = Color(0xFF001F25)
val md_theme_dark_inverseSurface = Color(0xFFA6EEFF)
val md_theme_dark_inversePrimary = Color(0xFF004AF2)
val md_theme_dark_shadow = Color(0xFF000000)
val md_theme_dark_surfaceTint = Color(0xFFB8C4FF)
val md_theme_dark_outlineVariant = Color(0xFF45464F)
val md_theme_dark_scrim = Color(0xFF000000)


val seed = Color(0xFF0E51FF)
val light_Success = Color(0xFF316B00)
val light_onSuccess = Color(0xFFFFFFFF)
val light_SuccessContainer = Color(0xFF94FC4F)
val light_onSuccessContainer = Color(0xFF0A2100)
val dark_Success = Color(0xFF7ADE33)
val dark_onSuccess = Color(0xFF163800)
val dark_SuccessContainer = Color(0xFF235100)
val dark_onSuccessContainer = Color(0xFF94FC4F)
val light_Warning = Color(0xFF725C00)
val light_onWarning = Color(0xFFFFFFFF)
val light_WarningContainer = Color(0xFFFFE07D)
val light_onWarningContainer = Color(0xFF231B00)
val dark_Warning = Color(0xFFEDC200)
val dark_onWarning = Color(0xFF3B2F00)
val dark_WarningContainer = Color(0xFF564500)
val dark_onWarningContainer = Color(0xFFFFE07D)
val inactive = Color(0xFFAEB2B9)

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

val AdditionalColorPaletteLight = AdditionalColorPalette(
    inactive = inactive,
    success = light_Success,
    onSuccess = light_onSuccess,
    onSuccessContainer = light_onSuccessContainer,
    successContainer = light_SuccessContainer,
    warning = light_Warning,
    onWarning = light_onWarning,
    onWarningContainer = light_onWarningContainer,
    warningContainer = light_WarningContainer,
    link = seed
)

val AdditionalColorPaletteDark = AdditionalColorPalette(
    inactive = inactive,
    onSuccess = dark_onSuccess,
    onSuccessContainer = dark_onSuccessContainer,
    successContainer = dark_SuccessContainer,
    warning = dark_Warning,
    onWarning = dark_onWarning,
    onWarningContainer = dark_onWarningContainer,
    warningContainer = dark_WarningContainer,
    link = seed

)

val LocalAdditionalColorPalette = staticCompositionLocalOf { AdditionalColorPalette() }

val MaterialTheme.additionalColorPalette: AdditionalColorPalette
    @Composable
    @ReadOnlyComposable
    get() = LocalAdditionalColorPalette.current