package com.project.tripplanner.features.home.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.project.tripplanner.ui.theme.TripPlannerTheme

@Composable
fun CompactPinnedHeader(
    modifier: Modifier = Modifier,
    visible: Boolean,
    content: @Composable () -> Unit
) {
    val animationDurationMs = 250
    val colors = TripPlannerTheme.colors
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(animationSpec = tween(durationMillis = animationDurationMs)) +
                expandVertically(
                    animationSpec = tween(durationMillis = animationDurationMs),
                    expandFrom = Alignment.Top
                ),
        exit = fadeOut(animationSpec = tween(durationMillis = animationDurationMs)) +
                shrinkVertically(
                    animationSpec = tween(durationMillis = animationDurationMs),
                    shrinkTowards = Alignment.Top
                )
    ) {
        Box(modifier = Modifier.fillMaxWidth().background(colors.surface)) { content() }
    }
}