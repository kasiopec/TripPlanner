package com.project.tripplanner.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val PrimaryLight = Color(0xFF004AF2)
val OnPrimaryLight = Color(0xFFFFFFFF)
val PrimaryContainerLight = Color(0xFFDDE1FF)
val OnPrimaryContainerLight = Color(0xFF001454)
val SecondaryLight = Color(0xFF7C5800)
val OnSecondaryLight = Color(0xFFFFFFFF)
val SecondaryContainerLight = Color(0xFFFFDEA8)
val OnSecondaryContainerLight = Color(0xFF271900)
val TertiaryLight = Color(0xFF1160A4)
val OnTertiaryLight = Color(0xFFFFFFFF)
val TertiaryContainerLight = Color(0xFFD3E4FF)
val OnTertiaryContainerLight = Color(0xFF001C38)
val ErrorLight = Color(0xFFBA1A1A)
val ErrorContainerLight = Color(0xFFFFDAD6)
val OnErrorLight = Color(0xFFFFFFFF)
val OnErrorContainerLight = Color(0xFF410002)
val BackgroundLight = Color(0xFFF8FDFF)
val OnBackgroundLight = Color(0xFF001F25)
val SurfaceLight = Color(0xFFF8FDFF)
val OnSurfaceLight = Color(0xFF001F25)
val SurfaceVariantLight = Color(0xFFE2E1EC)
val OnSurfaceVariantLight = Color(0xFF45464F)
val OutlineLight = Color(0xFF767680)
val InverseOnSurfaceLight = Color(0xFFD6F6FF)
val InverseSurfaceLight = Color(0xFF00363F)
val InversePrimaryLight = Color(0xFFB8C4FF)
val ShadowLight = Color(0xFF000000)
val SurfaceTintLight = Color(0xFF004AF2)
val OutlineVariantLight = Color(0xFFC6C5D0)
val ScrimLight = Color(0xFF000000)

val PrimaryDark = Color(0xFFB8C4FF)
val OnPrimaryDark = Color(0xFF002585)
val PrimaryContainerDark = Color(0xFF0037BA)
val OnPrimaryContainerDark = Color(0xFFDDE1FF)
val SecondaryDark = Color(0xFFFFBA20)
val OnSecondaryDark = Color(0xFF412D00)
val SecondaryContainerDark = Color(0xFF5E4200)
val OnSecondaryContainerDark = Color(0xFFFFDEA8)
val TertiaryDark = Color(0xFFA1C9FF)
val OnTertiaryDark = Color(0xFF00325B)
val TertiaryContainerDark = Color(0xFF004880)
val OnTertiaryContainerDark = Color(0xFFD3E4FF)
val ErrorDark = Color(0xFFFFB4AB)
val ErrorContainerDark = Color(0xFF93000A)
val OnErrorDark = Color(0xFF690005)
val OnErrorContainerDark = Color(0xFFFFDAD6)
val BackgroundDark = Color(0xFF111318)
val OnBackgroundDark = Color(0xFFE2E2E9)
val SurfaceDark = Color(0xFF191C20)
val OnSurfaceDark = Color(0xFFE2E2E9)
val SurfaceVariantDark = Color(0xFF45464F)
val OnSurfaceVariantDark = Color(0xFFC6C5D0)
val OutlineDark = Color(0xFF90909A)
val InverseOnSurfaceDark = Color(0xFF001F25)
val InverseSurfaceDark = Color(0xFFA6EEFF)
val InversePrimaryDark = Color(0xFF004AF2)
val ShadowDark = Color(0xFF000000)
val SurfaceTintDark = Color(0xFFB8C4FF)
val OutlineVariantDark = Color(0xFF45464F)
val ScrimDark = Color(0xFF000000)

//Additional colors
val Seed = Color(0xFF0E51FF)
val SuccessLight = Color(0xFF316B00)
val OnSuccessLight = Color(0xFFFFFFFF)
val SuccessContainerLight = Color(0xFF94FC4F)
val OnSuccessContainerLight = Color(0xFF0A2100)
val SuccessDark = Color(0xFF7ADE33)
val OnSuccessDark = Color(0xFF163800)
val SuccessContainerDark = Color(0xFF235100)
val OnSuccessContainerDark = Color(0xFF94FC4F)
val WarningLight = Color(0xFF725C00)
val OnWarningLight = Color(0xFFFFFFFF)
val WarningContainerLight = Color(0xFFFFE07D)
val OnWarningContainerLight = Color(0xFF231B00)
val WarningDark = Color(0xFFEDC200)
val OnWarningDark = Color(0xFF3B2F00)
val WarningContainerDark = Color(0xFF564500)
val OnWarningContainerDark = Color(0xFFFFE07D)
val Inactive = Color(0xFFAEB2B9)


val DarkColors = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = SecondaryContainerDark,
    onSecondaryContainer = OnSecondaryContainerDark,
    tertiary = TertiaryDark,
    onTertiary = OnTertiaryDark,
    tertiaryContainer = TertiaryContainerDark,
    onTertiaryContainer = OnTertiaryContainerDark,
    error = ErrorDark,
    errorContainer = ErrorContainerDark,
    onError = OnErrorDark,
    onErrorContainer = OnErrorContainerDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark,
    inverseOnSurface = InverseOnSurfaceDark,
    inverseSurface = InverseSurfaceDark,
    inversePrimary = InversePrimaryDark,
    surfaceTint = SurfaceTintDark,
    outlineVariant = OutlineVariantDark,
    scrim = ScrimDark,
)

val LightColors = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryContainerLight,
    onPrimaryContainer = OnPrimaryContainerLight,
    secondary = SecondaryLight,
    onSecondary = OnSecondaryLight,
    secondaryContainer = SecondaryContainerLight,
    onSecondaryContainer = OnSecondaryContainerLight,
    tertiary = TertiaryLight,
    onTertiary = OnTertiaryLight,
    tertiaryContainer = TertiaryContainerLight,
    onTertiaryContainer = OnTertiaryContainerLight,
    error = ErrorLight,
    errorContainer = ErrorContainerLight,
    onError = OnErrorLight,
    onErrorContainer = OnErrorContainerLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight,
    inverseOnSurface = InverseOnSurfaceLight,
    inverseSurface = InverseSurfaceLight,
    inversePrimary = InversePrimaryLight,
    surfaceTint = SurfaceTintLight,
    outlineVariant = OutlineVariantLight,
    scrim = ScrimLight,
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

val AdditionalColorPaletteLight = AdditionalColorPalette(
    inactive = Inactive,
    success = SuccessLight,
    onSuccess = OnSuccessLight,
    onSuccessContainer = OnSuccessContainerLight,
    successContainer = SuccessContainerLight,
    warning = WarningLight,
    onWarning = OnWarningLight,
    onWarningContainer = OnWarningContainerLight,
    warningContainer = WarningContainerLight,
    link = Seed
)

val AdditionalColorPaletteDark = AdditionalColorPalette(
    inactive = Inactive,
    onSuccess = OnSuccessDark,
    onSuccessContainer = OnSuccessContainerDark,
    successContainer = SuccessContainerDark,
    warning = WarningDark,
    onWarning = OnWarningDark,
    onWarningContainer = OnWarningContainerDark,
    warningContainer = WarningContainerDark,
    link = Seed
)

val LocalAdditionalColorPalette = staticCompositionLocalOf { AdditionalColorPalette() }

val MaterialTheme.additionalColorPalette: AdditionalColorPalette
    @Composable
    @ReadOnlyComposable
    get() = LocalAdditionalColorPalette.current