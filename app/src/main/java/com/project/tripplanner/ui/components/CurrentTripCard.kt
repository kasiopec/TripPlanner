package com.project.tripplanner.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.project.tripplanner.R
import com.project.tripplanner.features.home.TripProgress
import com.project.tripplanner.features.home.TripStatusUi
import com.project.tripplanner.features.home.TripUiModel
import com.project.tripplanner.ui.components.text.Headline1
import com.project.tripplanner.ui.components.text.LabelText
import com.project.tripplanner.ui.components.text.MetaText
import com.project.tripplanner.ui.theme.Dimensions
import com.project.tripplanner.ui.theme.TripPlannerTheme
import com.project.tripplanner.utils.capitalize
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CurrentTripCard(
    modifier: Modifier = Modifier,
    trip: TripUiModel,
    onClick: () -> Unit
) {
    val colors = TripPlannerTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val cardShape = RoundedCornerShape(Dimensions.radiusCard)
    val cardContentHeight = 220.dp

    val progressText = trip.progress?.let { progress ->
        val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault())
        val date = trip.startDate.plusDays((progress.currentDay - 1).toLong()).format(formatter)
        "${stringResource(id = R.string.home_current_trip_progress, progress.currentDay, progress.totalDays)} \u2022 $date"
    } ?: trip.dateRangeText

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        MetaText(
            modifier = Modifier.padding(top = Dimensions.spacingL, bottom = Dimensions.spacingM),
            text = stringResource(id = R.string.home_current_trip_label),
            color = colors.secondary
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer(
                    scaleX = if (isPressed) 0.98f else 1f,
                    scaleY = if (isPressed) 0.98f else 1f
                )
                .clickable(
                    interactionSource = interactionSource,
                    onClick = onClick
                ),
            shape = cardShape,
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surface)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(cardContentHeight)
                    .clip(cardShape)
            ) {
                if (trip.coverImageUri != null) {
                    AsyncImage(
                        model = trip.coverImageUri,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(colors.mutedSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_image_placeholder_48),
                            contentDescription = null,
                            tint = colors.iconMuted,
                            modifier = Modifier.size(Dimensions.iconSize48)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(Dimensions.spacingL)
                ) {
                    Headline1(
                        text = trip.destination.capitalize(),
                        color = colors.surface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    LabelText(
                        text = progressText,
                        color = colors.surface.copy(alpha = 0.9f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.W500
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CurrentTripCardPreview() {
    TripPlannerTheme {
        CurrentTripCard(
            trip = TripUiModel(
                id = 1L,
                destination = "Trip to Bali",
                startDate = LocalDate.of(2025, 12, 13),
                endDate = LocalDate.of(2025, 12, 17),
                timezone = ZoneId.systemDefault(),
                dateRangeText = "Dec 13, 2025 - Dec 17, 2025",
                status = TripStatusUi.InProgress,
                coverImageUri = null,
                progress = TripProgress(currentDay = 1, totalDays = 5)
            ),
            onClick = {}
        )
    }
}
