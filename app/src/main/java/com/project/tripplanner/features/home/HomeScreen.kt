package com.project.tripplanner.features.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.project.tripplanner.ui.theme.TripPlannerTheme

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        HomeHero(
            modifier = Modifier.fillMaxWidth(),
            currentTrip = previewCurrentTrip()
        )
    }
}

private fun previewCurrentTrip(): CurrentTripHeroData {
    return CurrentTripHeroData(
        trip = HeroTrip(
            id = 1L,
            destination = "Lisbon, Portugal",
            dateRangeText = "Feb 20 - Mar 02",
            progressText = "Day 3 of 11"
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenCurrentPreview() {
    TripPlannerTheme {
        HomeScreen()
    }
}
