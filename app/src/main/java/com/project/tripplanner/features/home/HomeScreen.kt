package com.project.tripplanner.features.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.project.tripplanner.R
import com.project.tripplanner.ui.components.CompactHero
import com.project.tripplanner.ui.components.CountdownCard
import com.project.tripplanner.ui.components.FilterChip
import com.project.tripplanner.ui.components.TripCard
import com.project.tripplanner.ui.components.TripCardStatus
import com.project.tripplanner.ui.components.text.Headline2
import com.project.tripplanner.ui.components.text.Headline2
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
                modifier = modifier,
                uiState = uiState,
                onTripClick = onTripClick,
                onFilterSelected = onFilterSelected
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun HomeContent(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    onTripClick: (Long) -> Unit,
    onFilterSelected: (HomeFilter) -> Unit
) {
    val listState = rememberLazyListState()
    val baseTrips = uiState.trips
        .filterNot { it.id == uiState.currentTripId }
        .filter { it.status != TripStatusUi.InProgress }
    val filteredTrips = when (uiState.activeFilter) {
        HomeFilter.All -> baseTrips
        HomeFilter.Upcoming -> baseTrips.filter { it.status == TripStatusUi.None }
        HomeFilter.Ended -> baseTrips.filter { it.status == TripStatusUi.Ended }
    }
    val hasHero = uiState.currentTripId != null || (uiState.countdown != null && uiState.countdownTripId != null)
    val currentTrip = uiState.currentTripId?.let { id ->
        uiState.trips.firstOrNull { it.id == id }
    }
    val countdownTrip = if (uiState.countdown != null && uiState.countdownTripId != null) {
        uiState.trips.firstOrNull { it.id == uiState.countdownTripId }
    } else {
        null
    }
    val compactHeroTrip = currentTrip ?: countdownTrip
    var heroHeightPx by remember { mutableIntStateOf(0) }
    val showCompactHero by remember(hasHero, listState, heroHeightPx) {
        derivedStateOf {
            if (!hasHero) {
                false
            } else if (heroHeightPx > 0) {
                listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset >= heroHeightPx
            } else {
                listState.firstVisibleItemIndex > 0
            }
        }
    }
    val density = LocalDensity.current
    var compactHeroHeightPx by remember { mutableIntStateOf(0) }
    val compactHeroPadding = remember(showCompactHero, compactHeroHeightPx, density) {
        if (showCompactHero && compactHeroHeightPx > 0) {
            with(density) { compactHeroHeightPx.toDp() }
        } else {
            0.dp
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        HomeHeader(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimensions.spacingL, vertical = Dimensions.spacingM),
            activeFilter = uiState.activeFilter,
            onFilterSelected = onFilterSelected
        )

        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(Dimensions.spacingL),
                contentPadding = PaddingValues(
                    start = 0.dp,
                    end = 0.dp,
                    top = Dimensions.spacingL + compactHeroPadding,
                    bottom = Dimensions.spacingXXL
                ),
                state = listState
            ) {
                if (currentTrip != null) {
                    item {
                        CurrentTripHero(
                            modifier = Modifier
                                .fillMaxWidth()
                                .onGloballyPositioned { heroHeightPx = it.size.height },
                            trip = currentTrip,
                            onClick = { onTripClick(currentTrip.id) }
                        )
                    }
                } else if (countdownTrip != null) {
                    item {
                        CountdownCard(
                            destination = countdownTrip.destination,
                            until = countdownTrip.startDate.atStartOfDay(countdownTrip.timezone),
                            modifier = Modifier
                                .fillMaxWidth()
                                .onGloballyPositioned { heroHeightPx = it.size.height },
                            heroStyle = true
                        )
                    }
                }

                items(filteredTrips, key = { it.id }) { trip ->
                    TripCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Dimensions.spacingL),
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

            if (showCompactHero && compactHeroTrip != null) {
                androidx.compose.animation.AnimatedVisibility(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .onGloballyPositioned { compactHeroHeightPx = it.size.height },
                    visible = true,
                    enter = fadeIn(animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing)) +
                            slideInVertically(
                                animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
                                initialOffsetY = { -it }
                            ),
                    exit = fadeOut(animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing)) +
                            slideOutVertically(
                                animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
                                targetOffsetY = { -it }
                            )
                ) {
                    CompactHero(
                        modifier = Modifier.fillMaxWidth(),
                        labelRes = compactHeroTrip.statusLabelResId,
                        title = compactHeroTrip.destination,
                        subtitle = compactHeroTrip.dateRangeText
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeHeader(
    modifier: Modifier = Modifier,
    activeFilter: HomeFilter,
    onFilterSelected: (HomeFilter) -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimensions.spacingM)
    ) {
        Headline2(
            text = stringResource(id = R.string.home_title),
            color = TripPlannerTheme.colors.onBackground
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingS),
            verticalAlignment = Alignment.CenterVertically
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
