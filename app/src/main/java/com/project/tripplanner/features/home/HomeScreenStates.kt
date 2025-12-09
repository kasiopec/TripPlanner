package com.project.tripplanner.features.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
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
fun HomeLoading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = TripPlannerTheme.colors.primary)
    }
}

@Composable
fun HomeError(
    modifier: Modifier = Modifier,
    errorState: ErrorState,
    onRetry: () -> Unit
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
fun HomeEmptyState(modifier: Modifier = Modifier) {
    val colors = TripPlannerTheme.colors
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Dimensions.spacingXL),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_image_placeholder_48),
            contentDescription = null
        )
        Spacer(modifier = Modifier.height(Dimensions.spacingM))
        Headline2(
            text = stringResource(id = R.string.home_empty_title),
            color = colors.onBackground
        )
        Spacer(modifier = Modifier.height(Dimensions.spacingS))
        BodyRegular(
            text = stringResource(id = R.string.home_empty_message),
            color = colors.onSurfaceVariant
        )
    }
}
