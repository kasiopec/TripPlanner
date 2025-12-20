package com.project.tripplanner.features.tripdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.project.tripplanner.R
import com.project.tripplanner.data.model.ItineraryType
import com.project.tripplanner.ui.components.CalendarRow
import com.project.tripplanner.ui.components.DayItem
import com.project.tripplanner.ui.components.ItineraryItemCard
import com.project.tripplanner.ui.components.ItineraryUiModel
import com.project.tripplanner.ui.components.text.Headline1
import com.project.tripplanner.ui.theme.Dimensions
import com.project.tripplanner.ui.theme.TripPlannerTheme
import java.time.LocalDate

@Composable
fun TripDetailsScreen() {
    val colors = TripPlannerTheme.colors
    val scrollState = rememberScrollState()

    val startDate = LocalDate.of(2025, 1, 15)
    var selectedDate by remember { mutableStateOf(startDate) }

    val days = remember {
        (0..6).map { index ->
            DayItem(
                dayIndex = index + 1,
                date = startDate.plusDays(index.toLong()),
                isSelected = index == 0
            )
        }
    }

    val itineraryItems = remember {
        listOf(
            ItineraryUiModel(
                id = "1",
                title = "Flight to Rome",
                categoryName = "Flight",
                durationText = "3h 30m",
                type = ItineraryType.Flight,
                hasMap = false,
                hasDocs = true
            ),
            ItineraryUiModel(
                id = "2",
                title = "Hotel Colosseum View",
                categoryName = "Accommodation",
                durationText = "5 nights",
                type = ItineraryType.Hotel,
                hasMap = true,
                hasDocs = true
            ),
            ItineraryUiModel(
                id = "3",
                title = "Colosseum Tour",
                categoryName = "Sightseeing",
                durationText = "2h",
                type = ItineraryType.Activity,
                hasMap = true,
                hasDocs = false
            ),
            ItineraryUiModel(
                id = "4",
                title = "Trattoria da Luigi",
                categoryName = "Dinner",
                durationText = "1h 30m",
                type = ItineraryType.Food,
                hasMap = true,
                hasDocs = false
            ),
            ItineraryUiModel(
                id = "5",
                title = "Via del Corso Shopping",
                categoryName = "Shopping",
                durationText = "3h",
                type = ItineraryType.Shopping,
                hasMap = true,
                hasDocs = false
            )
        )
    }

    var expandedCardIds by remember { mutableStateOf(setOf<String>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Headline1(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = Dimensions.spacingL,
                    vertical = Dimensions.spacingM
                ),
            text = stringResource(R.string.trip_details_title),
            color = colors.onBackground
        )

        CalendarRow(
            modifier = Modifier.fillMaxWidth(),
            days = days.map { it.copy(isSelected = it.date == selectedDate) },
            onDaySelected = { date -> selectedDate = date }
        )

        Spacer(modifier = Modifier.height(Dimensions.spacingM))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = Dimensions.spacingL),
            verticalArrangement = Arrangement.spacedBy(Dimensions.spacingS)
        ) {
            itineraryItems.forEach { item ->
                val isExpanded = expandedCardIds.contains(item.id)
                ItineraryItemCard(
                    itinerary = item,
                    isExpanded = isExpanded,
                    onExpandedChange = { expanded ->
                        expandedCardIds = if (expanded) {
                            expandedCardIds + item.id
                        } else {
                            expandedCardIds - item.id
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(Dimensions.spacingXL))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TripDetailsScreenPreview() {
    TripPlannerTheme {
        TripDetailsScreen()
    }
}