package com.project.tripplanner.ui.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.project.tripplanner.R
import com.project.tripplanner.features.home.TripProgress
import com.project.tripplanner.features.home.TripStatusUi
import com.project.tripplanner.features.home.TripUiModel
import com.project.tripplanner.ui.components.text.BodyRegular
import com.project.tripplanner.ui.components.text.Headline2
import com.project.tripplanner.ui.components.text.MetaText
import com.project.tripplanner.ui.theme.Dimensions
import com.project.tripplanner.ui.theme.TripPlannerTheme
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun CurrentTripHero2(
    modifier: Modifier = Modifier,
    trip: TripUiModel,
    onClick: () -> Unit
) {
    val colors = TripPlannerTheme.colors
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 280.dp)
            .clickable(onClick = onClick)
    ) {
        HeroArtwork(
            modifier = Modifier.matchParentSize(),
            coverImageUri = trip.coverImageUri
        )
        Row(
            modifier = Modifier
                .matchParentSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(
                    start = Dimensions.spacingXL,
                    end = Dimensions.spacingXL,
                    bottom = Dimensions.spacingXXL
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingXL)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(Dimensions.spacingS),
                modifier = Modifier.weight(1f)
            ) {
                MetaText(
                    text = stringResource(id = R.string.home_current_trip_label),
                    color = colors.onPrimary.copy(alpha = 0.8f)
                )
                Headline2(
                    text = trip.destination,
                    color = colors.onPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                BodyRegular(
                    text = trip.dateRangeText,
                    color = colors.onPrimary.copy(alpha = 0.9f)
                )

                trip.progress?.let { progress ->
                    Surface(
                        color = colors.onPrimary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(Dimensions.radiusM)
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = Dimensions.spacingM,
                                vertical = Dimensions.spacingXS
                            ),
                            horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingS),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(Dimensions.spacingS)
                                    .clip(CircleShape)
                                    .background(colors.onPrimary)
                            )
                            BodyRegular(
                                text = stringResource(
                                    id = R.string.home_current_trip_progress,
                                    progress.currentDay,
                                    progress.totalDays
                                ),
                                color = colors.onPrimary
                            )
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .padding(bottom = 16.dp, end = 16.dp)
                .align(Alignment.BottomEnd)
                .clip(RoundedCornerShape(50))
                .background(TripPlannerTheme.colors.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_chevron_right_24),
                contentDescription = null,
                tint = TripPlannerTheme.colors.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun HeroArtwork(
    modifier: Modifier = Modifier,
    coverImageUri: Uri?
) {
    Box(modifier = modifier) {
        if (coverImageUri != null) {
            AsyncImage(
                model = coverImageUri,
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(TripPlannerTheme.colors.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_image_placeholder_48),
                    contentDescription = null,
                    modifier = Modifier.size(Dimensions.iconSizeM)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CurrentTripHeroPreview2() {
    TripPlannerTheme {
        CurrentTripHero2(
            trip = TripUiModel(
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
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CurrentTripHeroLightImagePreview2() {
    TripPlannerTheme {
        CurrentTripHero2(
            trip = TripUiModel(
                id = 2L,
                destination = "Geneva, Switzerland",
                startDate = LocalDate.of(2025, 3, 5),
                endDate = LocalDate.of(2025, 3, 12),
                timezone = ZoneId.systemDefault(),
                dateRangeText = "Mar 05, 2025 - Mar 12, 2025",
                status = TripStatusUi.InProgress,
                statusLabelResId = R.string.trip_status_in_progress,
                coverImageUri = "android.resource://com.project.tripplanner/drawable/home_hero_light".toUri(),
                progress = TripProgress(currentDay = 2, totalDays = 7)
            ),
            onClick = {}
        )
    }
}
