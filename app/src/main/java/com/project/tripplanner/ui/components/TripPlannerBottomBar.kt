package com.project.tripplanner.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.project.tripplanner.R

@Composable
fun TripPlannerBottomBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
    ) {
        Icon(
            painter = painterResource(R.drawable.home_alt_24),
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = null
        )
    }
}

@Preview
@Composable
fun TripPlannerBottomBarPreview() {
    TripPlannerBottomBar()
}