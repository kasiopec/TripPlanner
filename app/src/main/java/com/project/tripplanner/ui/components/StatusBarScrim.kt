package com.project.tripplanner.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.project.tripplanner.ui.theme.TripPlannerTheme

@Composable
fun StatusBarScrim(modifier: Modifier = Modifier) {
    val colors = TripPlannerTheme.colors
    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsTopHeight(WindowInsets.statusBars)
            .background(
                Brush.verticalGradient(
                    listOf(
                        colors.surface.copy(alpha = 0.7f),
                        Color.Transparent
                    )
                )
            )
    )
}
