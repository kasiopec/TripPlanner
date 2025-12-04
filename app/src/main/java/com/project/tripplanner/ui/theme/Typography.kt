package com.project.tripplanner.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.project.tripplanner.R

private val TripPlannerFontFamily = FontFamily(
    Font(R.font.nunitosans_regular, FontWeight.W400),
    Font(R.font.nunitosans_medium, FontWeight.W500),
    Font(R.font.nunitosans_semibold, FontWeight.W600),
    Font(R.font.nunitosans_bold, FontWeight.W700)
)

/**
 * Typography ramp derived from design-system.json foundations.typography.ramp.
 */
val display = TextStyle(
    fontFamily = TripPlannerFontFamily,
    fontWeight = FontWeight.W700,
    fontSize = 24.sp,
    lineHeight = 28.8.sp
)
val h1 = TextStyle(
    fontFamily = TripPlannerFontFamily,
    fontWeight = FontWeight.W700,
    fontSize = 20.sp,
    lineHeight = 24.sp
)
val h2 = TextStyle(
    fontFamily = TripPlannerFontFamily,
    fontWeight = FontWeight.W600,
    fontSize = 18.sp,
    lineHeight = 22.5.sp
)
val h3 = TextStyle(
    fontFamily = TripPlannerFontFamily,
    fontWeight = FontWeight.W600,
    fontSize = 16.sp,
    lineHeight = 20.8.sp
)
val body = TextStyle(
    fontFamily = TripPlannerFontFamily,
    fontWeight = FontWeight.W500,
    fontSize = 14.sp,
    lineHeight = 18.2.sp
)
val bodyRegular = TextStyle(
    fontFamily = TripPlannerFontFamily,
    fontWeight = FontWeight.W400,
    fontSize = 14.sp,
    lineHeight = 18.2.sp
)
val label = TextStyle(
    fontFamily = TripPlannerFontFamily,
    fontWeight = FontWeight.W500,
    fontSize = 12.sp,
    lineHeight = 14.4.sp
)
val caption = TextStyle(
    fontFamily = TripPlannerFontFamily,
    fontWeight = FontWeight.W500,
    fontSize = 10.sp,
    lineHeight = 12.sp
)
val meta = TextStyle(
    fontFamily = TripPlannerFontFamily,
    fontWeight = FontWeight.W500,
    fontSize = 12.sp,
    lineHeight = 13.2.sp,
    letterSpacing = 0.5.sp
)

@Immutable
data class ThemeTypography(
    val display: TextStyle,
    val h1: TextStyle,
    val h2: TextStyle,
    val h3: TextStyle,
    val meta: TextStyle,
    val caption: TextStyle,
    val label: TextStyle,
    val body: TextStyle,
    val bodyRegular: TextStyle,
)

val LocalTripTypography = staticCompositionLocalOf { TripPlannerTypography }

val TripPlannerTypography = ThemeTypography(
    h1 = h1,
    h2 = h2,
    h3 = h3,
    body = body,
    caption = caption,
    meta = meta,
    label = label,
    display = display,
    bodyRegular = bodyRegular
)

@Composable
fun Int.scaledSp(): TextUnit {
    return with(LocalDensity.current) {
        val fontScale = this.fontScale
        val texSize = this@scaledSp / fontScale
        texSize.sp
    }
}
