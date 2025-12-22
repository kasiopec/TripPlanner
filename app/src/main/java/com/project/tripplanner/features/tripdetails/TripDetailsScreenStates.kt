package com.project.tripplanner.features.tripdetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.project.tripplanner.ErrorState
import com.project.tripplanner.R
import com.project.tripplanner.ui.components.FullScreenError
import com.project.tripplanner.ui.components.text.BodyRegular
import com.project.tripplanner.ui.components.text.Headline2
import com.project.tripplanner.ui.theme.Dimensions
import com.project.tripplanner.ui.theme.TripPlannerTheme

@Composable
fun TripDetailsLoading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = TripPlannerTheme.colors.primary)
    }
}

@Composable
fun TripDetailsError(
    errorState: ErrorState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    FullScreenError(
        modifier = modifier,
        titleText = stringResource(id = errorState.titleId),
        messageText = stringResource(id = errorState.message),
        primaryActionText = stringResource(id = R.string.home_error_retry),
        onPrimaryActionClick = onRetry
    )
}

@Composable
fun TripDetailsEmptyState(
    uiState: TripDetailsUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (TripDetailsEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val colors = TripPlannerTheme.colors

    TripDetailsScaffold(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onEvent = onEvent,
        onDoneClick = {},
        listState = listState,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Dimensions.spacingXL),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_image_placeholder_48),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.height(Dimensions.spacingM))
                Headline2(
                    text = stringResource(id = R.string.trip_details_empty_title),
                    color = colors.onBackground
                )
                Spacer(modifier = Modifier.height(Dimensions.spacingS))
                BodyRegular(
                    text = stringResource(id = R.string.trip_details_empty_message),
                    color = colors.onSurfaceVariant
                )
            }
        }
    }
}
