package com.project.tripplanner.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.project.tripplanner.R

// Set of Material typography styles to start with
private val defaultTypography = Typography()
private val nunitoBold = FontFamily(Font(R.font.nunitosans_bold))
private val nunitoRegular = FontFamily(Font(R.font.nunitosans_regular))
val MaterialTypography = Typography(
    titleLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.nunitosans_bold)),
        fontSize = 28.0.sp,
        lineHeight = 34.0.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.nunitosans_bold)),
        fontSize = 22.0.sp,
        lineHeight = 28.0.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily(Font(R.font.nunitosans_bold)),
        fontSize = 20.0.sp,
        lineHeight = 24.0.sp,
    ),
    headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = nunitoBold),
    headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = nunitoBold),
    headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = nunitoBold),

    labelLarge = defaultTypography.labelLarge.copy(fontFamily = nunitoRegular),
    labelMedium = defaultTypography.labelMedium.copy(fontFamily = nunitoRegular),
    labelSmall = defaultTypography.labelSmall.copy(fontFamily = nunitoRegular),

    bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = nunitoRegular, fontSize = 18.sp),
    bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = nunitoRegular, fontSize = 14.sp),
    bodySmall = defaultTypography.bodySmall.copy(fontFamily = nunitoRegular, fontSize = 12.sp),

    displayLarge = defaultTypography.displayLarge.copy(fontFamily = nunitoRegular),
    displayMedium = defaultTypography.displayMedium.copy(fontFamily = nunitoRegular),
    displaySmall = defaultTypography.displaySmall.copy(fontFamily = nunitoRegular)
)