package com.project.tripplanner.features.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.project.tripplanner.R
import com.project.tripplanner.ui.components.CountdownCard
import com.project.tripplanner.ui.components.FilterChip
import com.project.tripplanner.ui.components.TripCard
import com.project.tripplanner.ui.components.TripCardStatus
import com.project.tripplanner.ui.theme.Dimensions
import com.project.tripplanner.ui.theme.TripPlannerTheme
import java.time.ZoneId

@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    onTripClick: (Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.emitEvent(HomeEvent.ScreenLoaded)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HomeEffect.NavigateToTripDetail -> onTripClick(effect.tripId)
                is HomeEffect.ShowSnackbar -> snackbarHostState.showSnackbar(
                    message = context.getString(effect.messageResId)
                )
            }
        }
    }

    HomeScreen(
        modifier = modifier,
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onRetry = { viewModel.emitEvent(HomeEvent.RetryClicked) },
        onTripClick = { viewModel.emitEvent(HomeEvent.TripClicked(it)) },
        onFilterSelected = { viewModel.emitEvent(HomeEvent.FilterSelected(it)) }
    )
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    snackbarHostState: SnackbarHostState,
    onRetry: () -> Unit,
    onTripClick: (Long) -> Unit,
    onFilterSelected: (HomeFilter) -> Unit
) {
    val colors = TripPlannerTheme.colors
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colors.background,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when {
            uiState.isInitialLoading -> HomeLoading(modifier = Modifier.padding(paddingValues))
            uiState.error != null -> HomeError(
                modifier = Modifier.padding(paddingValues),
                errorState = uiState.error,
                onRetry = onRetry
            )

            uiState.trips.isEmpty() -> HomeEmptyState(modifier = Modifier.padding(paddingValues))
            else -> HomeContent(
                modifier = Modifier.padding(paddingValues),
                uiState = uiState,
                onTripClick = onTripClick,
                onFilterSelected = onFilterSelected
            )
        }
    }
}

@Composable
private fun HomeContent(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    onTripClick: (Long) -> Unit,
    onFilterSelected: (HomeFilter) -> Unit
) {
    val baseTrips = uiState.trips
        .filterNot { it.id == uiState.currentTripId }
        .filterNot { it.id == uiState.countdownTripId }
        .filter { it.status != TripStatusUi.InProgress }
    val filteredTrips = when (uiState.activeFilter) {
        HomeFilter.All -> baseTrips
        HomeFilter.Upcoming -> baseTrips.filter { it.status == TripStatusUi.None }
        HomeFilter.Ended -> baseTrips.filter { it.status == TripStatusUi.Ended }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(Dimensions.spacingL),
        contentPadding = PaddingValues(
            start = Dimensions.spacingL,
            end = Dimensions.spacingL,
            top = Dimensions.spacingXL,
            bottom = Dimensions.spacingXXL
        )
    ) {
        val currentTrip = uiState.currentTripId?.let { id ->
            uiState.trips.firstOrNull { it.id == id }
        }

        if (currentTrip != null) {
            item {
                CurrentTripHero(
                    trip = currentTrip,
                    onClick = { onTripClick(currentTrip.id) }
                )
            }
        } else if (uiState.countdown != null && uiState.countdownTripId != null) {
            val countdownTrip = uiState.trips.firstOrNull { it.id == uiState.countdownTripId }
            if (countdownTrip != null) {
                item {
                    CountdownHero(
                        trip = countdownTrip,
                        onClick = { onTripClick(countdownTrip.id) }
                    )
                }
            }
        }

        item {
            FilterRow(
                activeFilter = uiState.activeFilter,
                onFilterSelected = onFilterSelected
            )
        }

        items(filteredTrips, key = { it.id }) { trip ->
            TripCard(
                modifier = Modifier.fillMaxWidth(),
                title = trip.destination,
                dateRange = trip.dateRangeText,
                coverImageUri = trip.coverImageUri?.toString(),
                status = when (trip.status) {
                    TripStatusUi.InProgress -> TripCardStatus.InProgress
                    TripStatusUi.Ended -> TripCardStatus.Ended
                    else -> TripCardStatus.Upcoming
                },
                onClick = { onTripClick(trip.id) }
            )
        }
    }
}

@Composable
private fun FilterRow(
    modifier: Modifier = Modifier,
    activeFilter: HomeFilter,
    onFilterSelected: (HomeFilter) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingS)
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingS)
        ) {
            HomeFilter.entries.forEach { filter ->
                FilterChip(
                    label = stringResource(id = filter.labelResId),
                    selected = activeFilter == filter,
                    onClick = { onFilterSelected(filter) }
                )
            }
        }
    }
}

@Composable
private fun CountdownHero(
    modifier: Modifier = Modifier,
    trip: TripUiModel,
    onClick: () -> Unit
) {
    val startDateTime = trip.startDate.atStartOfDay(trip.timezone)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        CountdownCard(
            destination = trip.destination,
            until = startDateTime,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    val trips = listOf(
        TripUiModel(
            id = 1L,
            destination = "Lisbon, Portugal",
            startDate = java.time.LocalDate.of(2025, 2, 20),
            endDate = java.time.LocalDate.of(2025, 3, 2),
            timezone = ZoneId.systemDefault(),
            dateRangeText = "Feb 20, 2025 - Mar 02, 2025",
            status = TripStatusUi.InProgress,
            statusLabelResId = R.string.trip_status_in_progress,
            coverImageUri = null,
            progress = TripProgress(currentDay = 3, totalDays = 11)
        ),
        TripUiModel(
            id = 2L,
            destination = "Tokyo, Japan",
            startDate = java.time.LocalDate.of(2025, 5, 1),
            endDate = java.time.LocalDate.of(2025, 5, 8),
            timezone = ZoneId.systemDefault(),
            dateRangeText = "May 01, 2025 - May 08, 2025",
            status = TripStatusUi.None,
            statusLabelResId = R.string.home_status_upcoming,
            coverImageUri = null,
            progress = null
        ),
        TripUiModel(
            id = 3L,
            destination = "Berlin, Germany",
            startDate = java.time.LocalDate.of(2024, 12, 10),
            endDate = java.time.LocalDate.of(2024, 12, 15),
            timezone = ZoneId.systemDefault(),
            dateRangeText = "Dec 10, 2024 - Dec 15, 2024",
            status = TripStatusUi.Ended,
            statusLabelResId = R.string.trip_status_ended,
            coverImageUri = null,
            progress = null
        )
    )
    TripPlannerTheme {
        HomeScreen(
            uiState = HomeUiState(
                isInitialLoading = false,
                trips = trips,
                currentTripId = 1L,
                countdown = null,
                countdownTripId = null,
                activeFilter = HomeFilter.All
            ),
            snackbarHostState = SnackbarHostState(),
            onRetry = {},
            onTripClick = {},
            onFilterSelected = {}
        )
    }
}
