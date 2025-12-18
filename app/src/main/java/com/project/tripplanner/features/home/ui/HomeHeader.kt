package com.project.tripplanner.features.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.project.tripplanner.R
import com.project.tripplanner.features.home.HomeFilterType
import com.project.tripplanner.ui.components.FilterChip
import com.project.tripplanner.ui.components.text.DisplayText
import com.project.tripplanner.ui.theme.Dimensions
import com.project.tripplanner.ui.theme.TripPlannerTheme

@Composable
fun HomeHeader(
    modifier: Modifier = Modifier,
    activeFilter: HomeFilterType,
    onFilterSelected: (HomeFilterType) -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Top
    ) {
        DisplayText(
            text = stringResource(id = R.string.home_title),
            color = TripPlannerTheme.colors.onBackground
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingS),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HomeFilterType.entries.forEach { filter ->
                FilterChip(
                    label = stringResource(id = filter.labelResId),
                    selected = activeFilter == filter,
                    onClick = { onFilterSelected(filter) }
                )
            }
        }
    }
}
