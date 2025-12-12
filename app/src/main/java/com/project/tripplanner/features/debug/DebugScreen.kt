package com.project.tripplanner.features.debug

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.project.tripplanner.R
import com.project.tripplanner.ui.components.LargeRoundedButton
import com.project.tripplanner.ui.components.StatusBarScrim
import com.project.tripplanner.ui.components.text.BodyRegular
import com.project.tripplanner.ui.components.text.Headline2
import com.project.tripplanner.ui.theme.Dimensions
import com.project.tripplanner.ui.theme.TripPlannerTheme

@Composable
fun DebugRoute(
    viewModel: DebugViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is DebugEffect.ShowMessage -> snackbarHostState.showSnackbar(
                    message = context.getString(effect.messageResId)
                )
            }
        }
    }

    DebugScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onDeleteAllTripsClick = { viewModel.emitEvent(DebugEvent.DeleteAllTripsClicked) },
        onMarkAllEndedClick = { viewModel.emitEvent(DebugEvent.MarkAllTripsEndedClicked) }
    )
}

@Composable
fun DebugScreen(
    uiState: DebugUiState,
    snackbarHostState: SnackbarHostState,
    onDeleteAllTripsClick: () -> Unit,
    onMarkAllEndedClick: () -> Unit
) {
    val colors = TripPlannerTheme.colors
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = colors.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = Dimensions.spacingL, vertical = Dimensions.spacingL)
                    .windowInsetsPadding(WindowInsets.navigationBars),
                verticalArrangement = Arrangement.spacedBy(Dimensions.spacingL)
            ) {
                Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
                Headline2(
                    text = stringResource(id = R.string.debug_title),
                    color = colors.onBackground
                )
                BodyRegular(
                    text = stringResource(id = R.string.debug_description),
                    color = colors.onBackground
                )
                LargeRoundedButton(
                    text = stringResource(id = R.string.debug_delete_trips),
                    onClick = onDeleteAllTripsClick,
                    isEnabled = !uiState.isProcessing
                )
                LargeRoundedButton(
                    text = stringResource(id = R.string.debug_mark_all_ended),
                    onClick = onMarkAllEndedClick,
                    isEnabled = !uiState.isProcessing
                )
            }
            StatusBarScrim(modifier = Modifier.zIndex(1f))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DebugScreenPreview() {
    TripPlannerTheme {
        DebugScreen(
            uiState = DebugUiState(),
            snackbarHostState = SnackbarHostState(),
            onDeleteAllTripsClick = {},
            onMarkAllEndedClick = {}
        )
    }
}
