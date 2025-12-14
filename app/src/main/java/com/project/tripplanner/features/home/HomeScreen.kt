package com.project.tripplanner.features.home

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.project.tripplanner.R
import com.project.tripplanner.navigation.Screen
import com.project.tripplanner.ui.components.CompactCurrentTrip
import com.project.tripplanner.ui.components.CountdownCard
import com.project.tripplanner.ui.components.CurrentTripCard
import com.project.tripplanner.ui.components.HomeHeader
import com.project.tripplanner.ui.components.StatusBarScrim
import com.project.tripplanner.ui.components.TripCard
import com.project.tripplanner.ui.components.TripCardStatus
import com.project.tripplanner.ui.components.TripPlannerBottomBar
import com.project.tripplanner.ui.theme.Dimensions
import com.project.tripplanner.ui.theme.TripPlannerTheme
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun HomeRoute(
    currentScreen: Screen?,
    isBottomBarVisible: Boolean,
    onTripClick: (Long) -> Unit,
    onBottomBarItemClick: (Screen) -> Unit,
    onBottomBarDebugLongClick: () -> Unit,
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
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onRetry = { viewModel.emitEvent(HomeEvent.RetryClicked) },
        onTripClick = { viewModel.emitEvent(HomeEvent.TripClicked(it)) },
        onFilterSelected = { viewModel.emitEvent(HomeEvent.FilterSelected(it)) },
        currentScreen = currentScreen,
        isBottomBarVisible = isBottomBarVisible,
        onBottomBarItemClick = onBottomBarItemClick,
        onBottomBarDebugLongClick = onBottomBarDebugLongClick
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    snackbarHostState: SnackbarHostState,
    onRetry: () -> Unit,
    onTripClick: (Long) -> Unit,
    onFilterSelected: (HomeFilter) -> Unit,
    isBottomBarVisible: Boolean,
    currentScreen: Screen?,
    onBottomBarItemClick: (Screen) -> Unit,
    onBottomBarDebugLongClick: () -> Unit
) {
    val colors = TripPlannerTheme.colors

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = colors.surface,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { _ ->
        when {
            uiState.isInitialLoading -> HomeLoading(modifier = Modifier.fillMaxSize())
            uiState.error != null -> HomeError(
                modifier = Modifier.fillMaxSize(),
                errorState = uiState.error,
                onRetry = onRetry
            )

            uiState.trips.isEmpty() -> Box(modifier = Modifier.fillMaxSize()) {
                HomeEmptyState(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(bottom = Dimensions.spacingXXL)
                )
                AnimatedVisibility(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    visible = isBottomBarVisible,
                    enter = slideInVertically { it },
                    exit = slideOutVertically { it }
                ) {
                    TripPlannerBottomBar(
                        currentScreen = currentScreen,
                        onItemSelected = onBottomBarItemClick,
                        onLastItemLongPress = onBottomBarDebugLongClick
                    )
                }
                StatusBarScrim(modifier = Modifier.zIndex(1f))
            }

            else -> HomeContent(
                uiState = uiState,
                onTripClick = onTripClick,
                onFilterSelected = onFilterSelected,
                currentScreen = currentScreen,
                isBottomBarVisible = isBottomBarVisible,
                onBottomBarItemClick = onBottomBarItemClick,
                onBottomBarDebugLongClick = onBottomBarDebugLongClick
            )
        }
    }
}

@Composable
private fun HomeContent(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    onTripClick: (Long) -> Unit,
    onFilterSelected: (HomeFilter) -> Unit,
    isBottomBarVisible: Boolean,
    currentScreen: Screen?,
    onBottomBarItemClick: (Screen) -> Unit,
    onBottomBarDebugLongClick: () -> Unit
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
    val currentTrip = uiState.currentTripId?.let { id ->
        uiState.trips.firstOrNull { it.id == id }
    }
    val countdownTrip = if (uiState.countdown != null && uiState.countdownTripId != null) {
        uiState.trips.firstOrNull { it.id == uiState.countdownTripId }
    } else {
        null
    }

    val showCompactHero by remember(listState, currentTrip) {
        derivedStateOf {
            if (currentTrip == null) return@derivedStateOf false
            val visibleItems = listState.layoutInfo.visibleItemsInfo
            visibleItems.isNotEmpty() && visibleItems.none { it.key == "current_trip" }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = Dimensions.spacingL),
                    state = listState
                ) {
                    item(key = "status_bar_spacer") {
                        Spacer(
                            modifier = Modifier
                                .windowInsetsTopHeight(WindowInsets.statusBars)
                                .padding(bottom = Dimensions.spacingL)
                        )
                    }

                    if (currentTrip != null) {
                        item(key = "current_trip") {
                            CurrentTripCard(
                                modifier = Modifier
                                    .padding(horizontal = Dimensions.spacingL)
                                    .padding(bottom = Dimensions.spacingL)
                                    .fillMaxWidth(),
                                trip = currentTrip,
                                onClick = { onTripClick(currentTrip.id) }
                            )
                        }
                    } else if (countdownTrip != null) {
                        item(key = "countdown") {
                            CountdownCard(
                                destination = countdownTrip.destination,
                                until = countdownTrip.startDate.atStartOfDay(countdownTrip.timezone),
                                modifier = Modifier
                                    .padding(horizontal = Dimensions.spacingL)
                                    .padding(bottom = Dimensions.spacingL)
                                    .fillMaxWidth(),
                                heroStyle = true
                            )
                        }
                        item(key = "countdown_divider") {
                            HorizontalDivider(
                                modifier = Modifier
                                    .padding(horizontal = Dimensions.spacingL)
                                    .padding(bottom = Dimensions.spacingL),
                                thickness = Dimensions.strokeThin,
                                color = TripPlannerTheme.colors.divider
                            )
                        }
                    }

                    item(key = "home_header") {
                        HomeHeader(
                            modifier = Modifier
                                .padding(
                                    start = Dimensions.spacingL,
                                    top = Dimensions.spacingL,
                                    end = Dimensions.spacingL,
                                    bottom = Dimensions.spacingL
                                )
                                .fillMaxWidth(),
                            activeFilter = uiState.activeFilter,
                            onFilterSelected = onFilterSelected
                        )
                    }

                    itemsIndexed(filteredTrips, key = { _, trip -> trip.id }) { index, trip ->
                        val bottomPaddingModifier = if (index != filteredTrips.lastIndex) {
                            Modifier.padding(bottom = Dimensions.spacingL)
                        } else {
                            Modifier
                        }
                        TripCard(
                            modifier = Modifier
                                .padding(horizontal = Dimensions.spacingL)
                                .then(bottomPaddingModifier)
                                .fillMaxWidth(),
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

            AnimatedVisibility(
                visible = isBottomBarVisible,
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
                TripPlannerBottomBar(
                    currentScreen = currentScreen,
                    onItemSelected = onBottomBarItemClick,
                    onLastItemLongPress = onBottomBarDebugLongClick
                )
            }
        }

        if (currentTrip != null) {
            CompactHeroStickyHeader(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .zIndex(2f),
                trip = currentTrip,
                visible = showCompactHero
            )
        }

        StatusBarScrim(modifier = Modifier.zIndex(1f))
    }
}

@Composable
private fun CompactHeroStickyHeader(
    modifier: Modifier = Modifier,
    trip: TripUiModel,
    visible: Boolean
) {
    val animationDurationMs = 250
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(animationSpec = tween(durationMillis = animationDurationMs)) +
                expandVertically(
                    animationSpec = tween(durationMillis = animationDurationMs),
                    expandFrom = Alignment.Top
                ),
        exit = fadeOut(animationSpec = tween(durationMillis = animationDurationMs)) +
                slideOutVertically(
                    animationSpec = tween(durationMillis = animationDurationMs),
                    targetOffsetY = { -it }
                )
    ) {
        CompactCurrentTrip(
            labelRes = R.string.home_current_trip_label,
            coverImageUri = trip.coverImageUri?.toString(),
            tripTitle = trip.destination,
            currentDay = trip.progress?.currentDay ?: 1,
            totalDays = trip.progress?.totalDays ?: 0
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
            startDate = LocalDate.of(2025, 2, 20),
            endDate = LocalDate.of(2025, 3, 2),
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
            startDate = LocalDate.of(2025, 5, 1),
            endDate = LocalDate.of(2025, 5, 8),
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
            startDate = LocalDate.of(2024, 12, 10),
            endDate = LocalDate.of(2024, 12, 15),
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
            onFilterSelected = {},
            isBottomBarVisible = true,
            currentScreen = Screen.fromRoute("home_screen"),
            onBottomBarItemClick = {},
            onBottomBarDebugLongClick = {}
        )
    }
}
