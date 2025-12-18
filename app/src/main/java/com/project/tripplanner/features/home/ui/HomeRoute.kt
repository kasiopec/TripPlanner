package com.project.tripplanner.features.home.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.project.tripplanner.features.home.HomeEffect
import com.project.tripplanner.features.home.HomeEvent
import com.project.tripplanner.features.home.HomeViewModel
import com.project.tripplanner.navigation.Screen
import kotlinx.coroutines.flow.collect

@Composable
fun HomeRoute(
    currentScreen: Screen?,
    isBottomBarVisible: Boolean,
    onTripClick: (Long) -> Unit,
    onBottomBarItemClick: (Screen) -> Unit,
    onBottomBarDebugLongClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
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
        onBottomBarDebugLongClick = onBottomBarDebugLongClick,
    )
}

