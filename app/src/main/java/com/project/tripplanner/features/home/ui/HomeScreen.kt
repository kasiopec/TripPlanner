package com.project.tripplanner.features.home.ui

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.foundation.lazy.LazyListState
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
import com.project.tripplanner.features.home.HomeEffect
import com.project.tripplanner.features.home.HomeEmptyState
import com.project.tripplanner.features.home.HomeError
import com.project.tripplanner.features.home.HomeEvent
import com.project.tripplanner.features.home.HomeFilter
import com.project.tripplanner.features.home.HomeLoading
import com.project.tripplanner.features.home.HomeUiState
import com.project.tripplanner.features.home.HomeViewModel
import com.project.tripplanner.features.home.TripProgress
import com.project.tripplanner.features.home.TripStatusUi
import com.project.tripplanner.features.home.TripUiModel
import com.project.tripplanner.navigation.Screen
import com.project.tripplanner.ui.components.CompactCountdown
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

private const val KeyStatusBarSpacer = "status_bar_spacer"
private const val KeyCurrentTrip = "current_trip"
private const val KeyCountdown = "countdown"
private const val KeyCountdownDivider = "countdown_divider"
private const val KeyHomeHeader = "home_header"

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

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { event ->
            when (event) {
                is HomeEffect.NavigateToTripDetail -> onTripClick(event.tripId)
                is HomeEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = context.getString(event.messageResId)
                    )
                }
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
    val showChrome = !uiState.isInitialLoading && uiState.error == null

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = colors.surface,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            HomeBottomBar(
                visible = isBottomBarVisible && showChrome,
                currentScreen = currentScreen,
                onBottomBarItemClick = onBottomBarItemClick,
                onBottomBarDebugLongClick = onBottomBarDebugLongClick
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            val contentModifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .then(
                    if (isBottomBarVisible && showChrome) {
                        Modifier
                    } else {
                        Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                    }
                )

            when {
                uiState.isInitialLoading -> HomeLoading(modifier = Modifier.fillMaxSize())

                uiState.error != null -> HomeError(
                    modifier = Modifier.fillMaxSize(),
                    errorState = uiState.error,
                    onRetry = onRetry
                )

                uiState.trips.isEmpty() -> HomeEmptyContent(modifier = contentModifier)

                else -> HomeContent(
                    modifier = contentModifier,
                    uiState = uiState,
                    onTripClick = onTripClick,
                    onFilterSelected = onFilterSelected
                )
            }

            if (showChrome) {
                StatusBarScrim(modifier = Modifier.zIndex(1f))
            }
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

    val isCurrentTripOutOfView by rememberIsItemKeyOutOfView(listState, KeyCurrentTrip)
    val isCountdownOutOfView by rememberIsItemKeyOutOfView(listState, KeyCountdown)
    val showCompactHero = currentTrip != null && isCurrentTripOutOfView
    val showCompactCountdown = currentTrip == null && countdownTrip != null && isCountdownOutOfView

    Box(modifier = modifier.fillMaxSize()) {
        HomeTripList(
            modifier = Modifier.fillMaxSize(),
            listState = listState,
            currentTrip = currentTrip,
            countdownTrip = countdownTrip,
            filteredTrips = filteredTrips,
            activeFilter = uiState.activeFilter,
            onTripClick = onTripClick,
            onFilterSelected = onFilterSelected
        )

        HomeOverlays(
            currentTrip = currentTrip,
            showCompactHero = showCompactHero,
            countdownTrip = countdownTrip,
            showCompactCountdown = showCompactCountdown
        )
    }
}

@Composable
private fun HomeBottomBar(
    modifier: Modifier = Modifier,
    visible: Boolean,
    currentScreen: Screen?,
    onBottomBarItemClick: (Screen) -> Unit,
    onBottomBarDebugLongClick: () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
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

@Composable
private fun HomeEmptyContent(modifier: Modifier = Modifier) {
    HomeEmptyState(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
    )
}

@Composable
private fun HomeTripList(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    currentTrip: TripUiModel?,
    countdownTrip: TripUiModel?,
    filteredTrips: List<TripUiModel>,
    activeFilter: HomeFilter,
    onTripClick: (Long) -> Unit,
    onFilterSelected: (HomeFilter) -> Unit
) {
    val fullWidthPadded = Modifier
        .fillMaxWidth()
        .padding(horizontal = Dimensions.spacingL)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = Dimensions.spacingL),
        state = listState
    ) {
        item(key = KeyStatusBarSpacer) {
            Spacer(
                modifier = Modifier
                    .windowInsetsTopHeight(WindowInsets.statusBars)
                    .padding(bottom = Dimensions.spacingL)
            )
        }

        when {
            currentTrip != null -> item(key = KeyCurrentTrip) {
                CurrentTripCard(
                    modifier = fullWidthPadded.padding(bottom = Dimensions.spacingL),
                    trip = currentTrip,
                    onClick = { onTripClick(currentTrip.id) }
                )
            }

            countdownTrip != null -> {
                item(key = KeyCountdown) {
                    CountdownCard(
                        destination = countdownTrip.destination,
                        until = countdownTrip.startDate.atStartOfDay(countdownTrip.timezone),
                        modifier = fullWidthPadded.padding(bottom = Dimensions.spacingL),
                        heroStyle = true
                    )
                }
                item(key = KeyCountdownDivider) {
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(horizontal = Dimensions.spacingL)
                            .padding(bottom = Dimensions.spacingL),
                        thickness = Dimensions.strokeThin,
                        color = TripPlannerTheme.colors.divider
                    )
                }
            }
        }

        item(key = KeyHomeHeader) {
            HomeHeader(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimensions.spacingL, vertical = Dimensions.spacingL),
                activeFilter = activeFilter,
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
                modifier = fullWidthPadded.then(bottomPaddingModifier),
                title = trip.destination,
                dateRange = trip.dateRangeText,
                coverImageUri = trip.coverImageUri?.toString(),
                status = trip.status.toTripCardStatus(),
                onClick = { onTripClick(trip.id) }
            )
        }
    }
}

@Composable
private fun BoxScope.HomeOverlays(
    currentTrip: TripUiModel?,
    showCompactHero: Boolean,
    countdownTrip: TripUiModel?,
    showCompactCountdown: Boolean
) {
    if (currentTrip != null) {
        CompactCurrentTripHeader(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .zIndex(2f),
            trip = currentTrip,
            visible = showCompactHero
        )
    } else if (countdownTrip != null) {
        CompactCountdownHeader(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .zIndex(2f),
            trip = countdownTrip,
            visible = showCompactCountdown
        )
    }
}

private fun TripStatusUi.toTripCardStatus(): TripCardStatus = when (this) {
    TripStatusUi.InProgress -> TripCardStatus.InProgress
    TripStatusUi.Ended -> TripCardStatus.Ended
    else -> TripCardStatus.Upcoming
}

@Composable
private fun CompactCurrentTripHeader(
    modifier: Modifier = Modifier,
    trip: TripUiModel,
    visible: Boolean
) {
    CompactPinnedHeader(
        modifier = modifier,
        visible = visible
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

@Composable
private fun CompactCountdownHeader(
    modifier: Modifier = Modifier,
    trip: TripUiModel,
    visible: Boolean
) {
    CompactPinnedHeader(
        modifier = modifier,
        visible = visible
    ) {
        CompactCountdown(
            tripTitle = trip.destination,
            dateRangeText = trip.dateRangeText,
            countdownTargetEpochMillis = trip.startDate
                .atStartOfDay(trip.timezone)
                .toInstant()
                .toEpochMilli()
        )
    }
}

@Composable
private fun rememberIsItemKeyOutOfView(
    listState: LazyListState,
    key: Any
) = remember(listState, key) {
    derivedStateOf {
        val visibleItems = listState.layoutInfo.visibleItemsInfo
        visibleItems.isNotEmpty() && visibleItems.none { it.key == key }
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
