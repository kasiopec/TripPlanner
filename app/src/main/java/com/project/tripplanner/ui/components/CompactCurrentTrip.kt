package com.project.tripplanner.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import coil.compose.AsyncImage
import com.project.tripplanner.R
import com.project.tripplanner.ui.components.text.Headline2
import com.project.tripplanner.ui.components.text.Headline3
import com.project.tripplanner.ui.components.text.MetaText
import com.project.tripplanner.ui.theme.Dimensions
import com.project.tripplanner.ui.theme.TripPlannerTheme
import com.project.tripplanner.utils.capitalize

@Composable
fun CompactCurrentTrip(
    modifier: Modifier = Modifier,
    @StringRes labelRes: Int,
    coverImageUri: String?,
    tripTitle: String,
    currentDay: Int,
    totalDays: Int
) {
    val colors = TripPlannerTheme.colors
    val additionalColors = TripPlannerTheme.additionalColors
    val cardShape = RoundedCornerShape(Dimensions.radiusCard)
    val thumbnailShape = RoundedCornerShape(Dimensions.radiusM)

    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = cardShape,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface)
    ) {
        Row(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(Dimensions.cardPadding),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingM),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(Dimensions.iconSize48)
                    .clip(thumbnailShape),
                contentAlignment = Alignment.Center
            ) {
                if (coverImageUri != null) {
                    AsyncImage(
                        model = coverImageUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
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
            }

            Column(
                modifier = Modifier.weight(3f),
                verticalArrangement = Arrangement.spacedBy(Dimensions.spacingXS)
            ) {
                MetaText(
                    text = stringResource(id = labelRes),
                    color = colors.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Headline3(
                    text = tripTitle.capitalize(),
                    color = colors.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(Dimensions.spacingXS)
            ) {
                MetaText(
                    text = stringResource(id = R.string.home_current_trip_day_label),
                    color = additionalColors.inactive,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (totalDays > 0) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Headline2(
                            text = currentDay.toString(),
                            color = colors.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        MetaText(
                            text = "/$totalDays",
                            color = additionalColors.inactive,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                } else {
                    Headline2(
                        text = currentDay.toString(),
                        color = colors.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CompactCurrentTripPreview() {
    TripPlannerTheme {
        Box(
            modifier = Modifier
                .background(TripPlannerTheme.colors.background)
                .width(360.dp)
        ) {
            CompactCurrentTrip(
                labelRes = R.string.home_current_trip_label,
                coverImageUri = "android.resource://com.project.tripplanner/${R.drawable.home_hero_light}",
                tripTitle = "Trip to Bali",
                currentDay = 1,
                totalDays = 5
            )
        }
    }
}
