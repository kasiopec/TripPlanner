package com.project.tripplanner.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.project.tripplanner.R
import com.project.tripplanner.data.model.ItineraryType
import com.project.tripplanner.ui.theme.TripPlannerTheme

@Composable
fun ItineraryType.getIcon(): Int = when (this) {
    ItineraryType.Flight -> R.drawable.ic_plane_24
    ItineraryType.Hotel -> R.drawable.ic_home_32
    ItineraryType.Activity -> R.drawable.ic_map_pin_24
    ItineraryType.Food -> R.drawable.ic_star_16
    ItineraryType.Shopping -> R.drawable.ic_list_24
}

@Composable
fun ItineraryType.getIconColor(): Color {
    val categories = TripPlannerTheme.colors.categories
    return when (this) {
        ItineraryType.Flight -> categories.flightIcon
        ItineraryType.Hotel -> categories.hotelIcon
        ItineraryType.Activity -> categories.sightseeingIcon
        ItineraryType.Food -> categories.foodIcon
        ItineraryType.Shopping -> categories.shoppingIcon
    }
}

@Composable
fun ItineraryType.getTintColor(): Color {
    val categories = TripPlannerTheme.colors.categories
    return when (this) {
        ItineraryType.Flight -> categories.flightTint
        ItineraryType.Hotel -> categories.hotelTint
        ItineraryType.Activity -> categories.sightseeingTint
        ItineraryType.Food -> categories.foodTint
        ItineraryType.Shopping -> categories.shoppingTint
    }
}
