package com.project.tripplanner.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.project.tripplanner.R

object Typography {
    val titleLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.nunitosans_bold)),
        fontSize = 28.0.sp,
        lineHeight = 34.0.sp
    )
    val titleMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.nunitosans_bold)),
        fontSize = 22.0.sp,
        lineHeight = 28.0.sp
    )
    val titleSmall = TextStyle(
        fontFamily = FontFamily(Font(R.font.nunitosans_bold)),
        fontSize = 20.0.sp,
        lineHeight = 24.0.sp
    )
    val headline = TextStyle(
        fontFamily = FontFamily(Font(R.font.nunitosans_semibold)),
        fontSize = 17.0.sp,
        lineHeight = 22.0.sp
    )
    val body_medium = TextStyle(
        fontFamily = FontFamily(Font(R.font.nunitosans_medium)),
        fontSize = 17.0.sp,
        lineHeight = 22.0.sp
    )
    val body_regular = TextStyle(
        fontFamily = FontFamily(Font(R.font.nunitosans_regular)),
        fontSize = 17.0.sp,
        lineHeight = 22.0.sp
    )
}

@Composable
fun Int.scaledSp(): TextUnit {
    return with(LocalDensity.current) {
        val fontScale = this.fontScale
        val texSize = this@scaledSp / fontScale
        texSize.sp
    }
}