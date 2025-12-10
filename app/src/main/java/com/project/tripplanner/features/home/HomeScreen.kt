package com.project.tripplanner.features.home

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.project.tripplanner.R
import com.project.tripplanner.navigation.Screen
import com.project.tripplanner.ui.components.CountdownCard2
import com.project.tripplanner.ui.components.CurrentTripHero
import com.project.tripplanner.ui.components.CurrentTripHero2
import com.project.tripplanner.ui.components.HomeHeader
import com.project.tripplanner.ui.components.TripCard
import com.project.tripplanner.ui.components.TripCardStatus
import com.project.tripplanner.ui.components.TripPlannerBottomBar
import com.project.tripplanner.ui.theme.Dimensions
import com.project.tripplanner.ui.theme.TripPlannerTheme
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun HomeRoute(
    onTripClick: (Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController
) {
    val uiState by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentScreen = Screen.fromRoute(currentRoute)
    val isBottomBarVisible = currentScreen?.isBottomBarVisible == true

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
        navController = navController
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
    navController: NavController
) {
    val colors = TripPlannerTheme.colors

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = colors.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0)
    ) { _ ->
        when {
            uiState.isInitialLoading -> HomeLoading(modifier = Modifier.fillMaxSize())
            uiState.error != null -> HomeError(
                modifier = Modifier.fillMaxSize(),
                errorState = uiState.error,
                onRetry = onRetry
            )

            uiState.trips.isEmpty() -> HomeEmptyState(modifier = Modifier.fillMaxSize())
            else -> HomeContent(
                uiState = uiState,
                onTripClick = onTripClick,
                onFilterSelected = onFilterSelected,
                currentScreen = currentScreen,
                isBottomBarVisible = isBottomBarVisible,
                navController = navController,
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
    navController: NavController
) {
    val defaultHeroHeight = 240.dp
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
    val density = LocalDensity.current
    val hasHero = currentTrip != null || countdownTrip != null
    var heroHeight by remember(hasHero) { mutableStateOf(if (hasHero) defaultHeroHeight else 0.dp) }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val heroHeightDp = if (hasHero && heroHeight > 0.dp) heroHeight else 0.dp
        val overlap = if (heroHeightDp > 0.dp) Dimensions.spacingM else 0.dp
        val surfaceHeight = (maxHeight - heroHeightDp + overlap).coerceAtLeast(0.dp)

        if (currentTrip != null) {
            CurrentTripHero2(
                modifier = Modifier
                    .fillMaxWidth()
                    .onSizeChanged { size ->
                        heroHeight = with(density) { size.height.toDp() }
                    },
                trip = currentTrip,
                onClick = { onTripClick(currentTrip.id) }
            )
        } else if (countdownTrip != null) {
            CountdownCard2(
                destination = countdownTrip.destination,
                until = countdownTrip.startDate.atStartOfDay(countdownTrip.timezone),
                modifier = Modifier
                    .fillMaxWidth()
                    .onSizeChanged { size ->
                        heroHeight = with(density) { size.height.toDp() }
                    },
                heroStyle = true
            )
        }

        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(surfaceHeight),
            shape = RoundedCornerShape(topStart = Dimensions.radiusL, topEnd = Dimensions.radiusL),
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
            color = TripPlannerTheme.colors.background
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top
            ) {
                HomeHeader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = Dimensions.spacingL,
                            end = Dimensions.spacingL,
                            top = Dimensions.spacingL,
                            bottom = Dimensions.spacingS
                        ),
                    activeFilter = uiState.activeFilter,
                    onFilterSelected = onFilterSelected
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(Dimensions.spacingL),
                        contentPadding = PaddingValues(
                            start = Dimensions.spacingL,
                            end = Dimensions.spacingL,
                            top = Dimensions.spacingL,
                            bottom = Dimensions.spacingL
                        ),
                        state = listState
                    ) {
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
                AnimatedVisibility(
                    visible = isBottomBarVisible,
                    enter = slideInVertically { it },
                    exit = slideOutVertically { it }
                ) {
                    TripPlannerBottomBar(
                        navController = navController,
                        currentScreen = currentScreen
                    )
                }
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
            navController = rememberNavController()
        )
    }
}
