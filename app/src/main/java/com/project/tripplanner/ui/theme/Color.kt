package com.project.tripplanner.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val TripPlannerPrimaryLight = Color(0xFF004AF2)
val TripPlannerOnPrimaryLight = Color(0xFFFFFFFF)
val TripPlannerPrimaryContainerLight = Color(0xFFDDE1FF)
val TripPlannerOnPrimaryContainerLight = Color(0xFF001454)
val TripPlannerSecondaryLight = Color(0xFF7C5800)
val TripPlannerOnSecondaryLight = Color(0xFFFFFFFF)
val TripPlannerSecondaryContainerLight = Color(0xFFFFDEA8)
val TripPlannerOnSecondaryContainerLight = Color(0xFF271900)
val TripPlannerTertiaryLight = Color(0xFF1160A4)
val TripPlannerOnTertiaryLight = Color(0xFFFFFFFF)
val TripPlannerTertiaryContainerLight = Color(0xFFD3E4FF)
val TripPlannerOnTertiaryContainerLight = Color(0xFF001C38)
val TripPlannerErrorLight = Color(0xFFBA1A1A)
val TripPlannerErrorContainerLight = Color(0xFFFFDAD6)
val TripPlannerOnErrorLight = Color(0xFFFFFFFF)
val TripPlannerOnErrorContainerLight = Color(0xFF410002)
val TripPlannerBackgroundLight = Color(0xFFF8FDFF)
val TripPlannerOnBackgroundLight = Color(0xFF001F25)
val TripPlannerSurfaceLight = Color(0xFFF8FDFF)
val TripPlannerOnSurfaceLight = Color(0xFF001F25)
val TripPlannerSurfaceVariantLight = Color(0xFFE2E1EC)
val TripPlannerOnSurfaceVariantLight = Color(0xFF45464F)
val TripPlannerOutlineLight = Color(0xFF767680)
val TripPlannerInverseOnSurfaceLight = Color(0xFFD6F6FF)
val TripPlannerInverseSurfaceLight = Color(0xFF00363F)
val TripPlannerInversePrimaryLight = Color(0xFFB8C4FF)
val TripPlannerShadowLight = Color(0xFF000000)
val TripPlannerSurfaceTintLight = Color(0xFF004AF2)
val TripPlannerOutlineVariantLight = Color(0xFFC6C5D0)
val TripPlannerScrimLight = Color(0xFF000000)

val TripPlannerPrimaryDark = Color(0xFFB8C4FF)
val TripPlannerOnPrimaryDark = Color(0xFF002585)
val TripPlannerPrimaryContainerDark = Color(0xFF0037BA)
val TripPlannerOnPrimaryContainerDark = Color(0xFFDDE1FF)
val TripPlannerSecondaryDark = Color(0xFFFFBA20)
val TripPlannerOnSecondaryDark = Color(0xFF412D00)
val TripPlannerSecondaryContainerDark = Color(0xFF5E4200)
val TripPlannerOnSecondaryContainerDark = Color(0xFFFFDEA8)
val TripPlannerTertiaryDark = Color(0xFFA1C9FF)
val TripPlannerOnTertiaryDark = Color(0xFF00325B)
val TripPlannerTertiaryContainerDark = Color(0xFF004880)
val TripPlannerOnTertiaryContainerDark = Color(0xFFD3E4FF)
val TripPlannerErrorDark = Color(0xFFFFB4AB)
val TripPlannerErrorContainerDark = Color(0xFF93000A)
val TripPlannerOnErrorDark = Color(0xFF690005)
val TripPlannerOnErrorContainerDark = Color(0xFFFFDAD6)
val TripPlannerBackgroundDark = Color(0xFF001F25)
val TripPlannerOnBackgroundDark = Color(0xFFA6EEFF)
val TripPlannerSurfaceDark = Color(0xFF001F25)
val TripPlannerOnSurfaceDark = Color(0xFFA6EEFF)
val TripPlannerSurfaceVariantDark = Color(0xFF45464F)
val TripPlannerOnSurfaceVariantDark = Color(0xFFC6C5D0)
val TripPlannerOutlineDark = Color(0xFF90909A)
val TripPlannerInverseOnSurfaceDark = Color(0xFF001F25)
val TripPlannerInverseSurfaceDark = Color(0xFFA6EEFF)
val TripPlannerInversePrimaryDark = Color(0xFF004AF2)
val TripPlannerShadowDark = Color(0xFF000000)
val TripPlannerSurfaceTintDark = Color(0xFFB8C4FF)
val TripPlannerOutlineVariantDark = Color(0xFF45464F)
val TripPlannerScrimDark = Color(0xFF000000)


val TripPlannerSeed = Color(0xFF0E51FF)
val TripPlannerSuccessLight = Color(0xFF316B00)
val TripPlannerOnSuccessLight = Color(0xFFFFFFFF)
val TripPlannerSuccessContainerLight = Color(0xFF94FC4F)
val TripPlannerOnSuccessContainerLight = Color(0xFF0A2100)
val TripPlannerSuccessDark = Color(0xFF7ADE33)
val TripPlannerOnSuccessDark = Color(0xFF163800)
val TripPlannerSuccessContainerDark = Color(0xFF235100)
val TripPlannerOnSuccessContainerDark = Color(0xFF94FC4F)
val TripPlannerWarningLight = Color(0xFF725C00)
val TripPlannerOnWarningLight = Color(0xFFFFFFFF)
val TripPlannerWarningContainerLight = Color(0xFFFFE07D)
val TripPlannerOnWarningContainerLight = Color(0xFF231B00)
val TripPlannerWarningDark = Color(0xFFEDC200)
val TripPlannerOnWarningDark = Color(0xFF3B2F00)
val TripPlannerWarningContainerDark = Color(0xFF564500)
val TripPlannerOnWarningContainerDark = Color(0xFFFFE07D)
val TripPlannerInactive = Color(0xFFAEB2B9)

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
    inactive = TripPlannerInactive,
    success = TripPlannerSuccessLight,
    onSuccess = TripPlannerOnSuccessLight,
    onSuccessContainer = TripPlannerOnSuccessContainerLight,
    successContainer = TripPlannerSuccessContainerLight,
    warning = TripPlannerWarningLight,
    onWarning = TripPlannerOnWarningLight,
    onWarningContainer = TripPlannerOnWarningContainerLight,
    warningContainer = TripPlannerWarningContainerLight,
    link = TripPlannerSeed
)

val AdditionalColorPaletteDark = AdditionalColorPalette(
    inactive = TripPlannerInactive,
    onSuccess = TripPlannerOnSuccessDark,
    onSuccessContainer = TripPlannerOnSuccessContainerDark,
    successContainer = TripPlannerSuccessContainerDark,
    warning = TripPlannerWarningDark,
    onWarning = TripPlannerOnWarningDark,
    onWarningContainer = TripPlannerOnWarningContainerDark,
    warningContainer = TripPlannerWarningContainerDark,
    link = TripPlannerSeed

)

val LocalAdditionalColorPalette = staticCompositionLocalOf { AdditionalColorPalette() }

val MaterialTheme.additionalColorPalette: AdditionalColorPalette
    @Composable
    @ReadOnlyComposable
    get() = LocalAdditionalColorPalette.current