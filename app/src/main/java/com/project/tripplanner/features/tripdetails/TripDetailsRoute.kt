package com.project.tripplanner.features.tripdetails

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.project.tripplanner.R
import com.project.tripplanner.features.tripdetails.mapbottomsheet.TripDetailsLocationSheet
import com.project.tripplanner.features.tripdetails.mapbottomsheet.openMapsSearch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailsRoute(
    onNavigateBack: () -> Unit,
    onNavigateToActivityForm: (Long, LocalDate) -> Unit,
    viewModel: TripDetailsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val locationSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        viewModel.emitEvent(TripDetailsEvent.ScreenLoaded)
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                TripDetailsEffect.NavigateBack -> onNavigateBack()
                is TripDetailsEffect.NavigateToActivityForm -> {
                    onNavigateToActivityForm(effect.tripId, effect.date)
                }

                is TripDetailsEffect.OpenMap -> {
                    val opened = openMapsSearch(
                        context = context,
                        query = effect.locationQuery
                    )
                    if (!opened) {
                        snackbarHostState.showSnackbar(context.getString(R.string.trip_details_map_no_app))
                    }
                }

                is TripDetailsEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(context.getString(effect.messageResId))
                }
            }
        }
    }

    val errorState = uiState.error
    when {
        uiState.isInitialLoading -> TripDetailsLoading()
        errorState != null -> TripDetailsError(
            errorState = errorState,
            onRetry = { viewModel.emitEvent(TripDetailsEvent.RetryClicked) }
        )

        uiState.itinerary.isEmpty() -> TripDetailsEmptyState(
            uiState = uiState,
            snackbarHostState = snackbarHostState,
            onEvent = viewModel::emitEvent
        )

        else -> TripDetailsScreen(
            uiState = uiState,
            snackbarHostState = snackbarHostState,
            onEvent = viewModel::emitEvent
        )
    }

    if (uiState.locationSheetItemId != null) {
        TripDetailsLocationSheet(
            sheetState = locationSheetState,
            locationQuery = uiState.locationQuery,
            isActionEnabled = uiState.isLocationActionEnabled,
            onLocationQueryChange = { viewModel.emitEvent(TripDetailsEvent.LocationQueryChanged(it)) },
            onSaveLocation = { viewModel.emitEvent(TripDetailsEvent.LocationSaveClicked) },
            onSearchInMaps = { viewModel.emitEvent(TripDetailsEvent.LocationSearchInMapsClicked) },
            onDismiss = {
                viewModel.emitEvent(TripDetailsEvent.LocationSheetDismissed)
            }
        )
    }
}
