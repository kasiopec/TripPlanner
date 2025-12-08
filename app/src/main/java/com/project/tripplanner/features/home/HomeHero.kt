package com.project.tripplanner.features.home

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.project.tripplanner.R
import com.project.tripplanner.ui.components.text.BodyRegular
import com.project.tripplanner.ui.components.text.Headline2
import com.project.tripplanner.ui.components.text.MetaText
import com.project.tripplanner.ui.theme.Dimensions
import com.project.tripplanner.ui.theme.TripPlannerTheme
import androidx.core.net.toUri
import coil.size.Dimension

data class HeroTrip(
    val id: Long,
    val destination: String,
    val dateRangeText: String,
    val progressText: String? = null,
    val coverImageUri: Uri? = null
)

data class CurrentTripHeroData(
    val trip: HeroTrip
)

@Composable
fun HomeHero(
    modifier: Modifier = Modifier,
    currentTrip: CurrentTripHeroData? = null,
    onHeroClick: (Long) -> Unit = {}
) {
    if (currentTrip != null) {
        CurrentTripHero(
            data = currentTrip,
            modifier = modifier,
            onClick = { onHeroClick(currentTrip.trip.id) }
        )
    }
}

@Composable
private fun CurrentTripHero(
    data: CurrentTripHeroData,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val colors = TripPlannerTheme.colors
    val backgroundBrush = Brush.linearGradient(
        colors = listOf(colors.primaryStrong, colors.primary)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(240.dp)
            .background(backgroundBrush)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .matchParentSize()
                .padding(
                    horizontal = Dimensions.spacingXL,
                    vertical = Dimensions.spacingXL
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
                    text = data.trip.destination,
                    color = colors.onPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                BodyRegular(
                    text = data.trip.dateRangeText,
                    color = colors.onPrimary.copy(alpha = 0.9f)
                )

                data.trip.progressText?.let { progress ->
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
                        )
                        {
                            Box(
                                modifier = Modifier
                                    .size(Dimensions.spacingS)
                                    .clip(CircleShape)
                                    .background(colors.onPrimary)
                            )
                            BodyRegular(
                                text = progress,
                                color = colors.onPrimary
                            )
                        }
                    }
                }
            }

            HeroArtwork(coverImageUri = data.trip.coverImageUri)
        }

    }
}

@Composable
private fun HeroArtwork(
    coverImageUri: Uri?,
    modifier: Modifier = Modifier
) {
    val colors = TripPlannerTheme.colors
    val accent = colors.onPrimary.copy(alpha = 0.12f)
    val accentStrong = colors.onPrimary.copy(alpha = 0.2f)
    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(180.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .width(160.dp)
                .clip(RoundedCornerShape(Dimensions.radiusL))
                .background(accent)
        )
        Column(
            modifier = Modifier.align(Alignment.CenterEnd),
            verticalArrangement = Arrangement.spacedBy(Dimensions.spacingS),
            horizontalAlignment = Alignment.End
        ) {
            Bar(accent = accentStrong, width = 72.dp)
            Bar(accent = accent, width = 56.dp)
            Bar(accent = accentStrong, width = 48.dp)
        }
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .width(160.dp)
                .clip(RoundedCornerShape(Dimensions.radiusL))
        ) {
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
                        .background(accent),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_image_placeholder_48),
                        contentDescription = null,
                        modifier = Modifier.size(Dimensions.iconSizeM)
                    )
                }
            }

            ImageBottomOverlay()
        }
    }
}

@Composable
private fun Bar(
    accent: Color,
    width: Dp
) {
    Box(
        modifier = Modifier
            .width(width)
            .height(Dimensions.strokeThick * 3)
            .clip(RoundedCornerShape(Dimensions.radiusS))
            .background(accent)
    )
}

@Composable
private fun BoxScope.ImageBottomOverlay(modifier: Modifier = Modifier) {
    val colors = TripPlannerTheme.colors
    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color.Transparent,
            colors.scrim
        )
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .align(Alignment.BottomCenter)
            .background(gradient)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = Dimensions.spacingM,
                    vertical = Dimensions.spacingS
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Surface(
                color = colors.surface.copy(alpha = 0.7f),
                shape = RoundedCornerShape(Dimensions.radiusM),
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .heightIn(min = Dimensions.chipHeight)
                        .padding(
                            top = Dimensions.spacingXS,
                            bottom = Dimensions.spacingXS,
                            start = Dimensions.spacingS
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingXS)
                ) {
                    BodyRegular(
                        text = stringResource(id = R.string.home_hero_view_details),
                        color = colors.onSurface
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_chevron_right_24),
                        contentDescription = null,
                        tint = colors.onSurface
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CurrentTripHeroPreview() {
    TripPlannerTheme {
        HomeHero(
            currentTrip = CurrentTripHeroData(
                trip = HeroTrip(
                    id = 1L,
                    destination = "Lisbon, Portugal",
                    dateRangeText = "Feb 20 - Mar 02",
                    progressText = "Day 3 of 11"
                )
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CurrentTripHeroLightImagePreview() {
    TripPlannerTheme {
        HomeHero(
            currentTrip = CurrentTripHeroData(
                trip = HeroTrip(
                    id = 2L,
                    destination = "Geneva, Switzerland",
                    dateRangeText = "Mar 05 - Mar 12",
                    progressText = "Day 2 of 7",
                    coverImageUri = "android.resource://com.project.tripplanner/drawable/home_hero_light".toUri()
                )
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CurrentTripHeroDarkImagePreview() {
    TripPlannerTheme {
        HomeHero(
            currentTrip = CurrentTripHeroData(
                trip = HeroTrip(
                    id = 3L,
                    destination = "Yosemite, USA",
                    dateRangeText = "Sep 10 - Sep 17",
                    progressText = "Day 4 of 8",
                    coverImageUri = "android.resource://com.project.tripplanner/drawable/home_hero_dark".toUri()
                )
            )
        )
    }
}
