package com.project.tripplanner.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.project.tripplanner.navigation.Screen
import com.project.tripplanner.ui.components.text.DisplayText
import com.project.tripplanner.ui.theme.TripPlannerTheme

@Composable
fun HomeScreen() {
    val colors = TripPlannerTheme.colors
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colors.background),
        contentAlignment = Alignment.Center
    ) {
        DisplayText(
            text = Screen.Home.title,
            color = colors.onBackground
        )
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    TripPlannerTheme {
        HomeScreen()
    }
}
