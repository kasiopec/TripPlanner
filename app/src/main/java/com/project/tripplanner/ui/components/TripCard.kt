package com.project.tripplanner.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.project.tripplanner.ui.components.text.Headline3
import com.project.tripplanner.ui.components.text.LabelText
import com.project.tripplanner.ui.theme.Dimensions
import com.project.tripplanner.ui.theme.TripPlannerTheme
import com.project.tripplanner.R
import com.project.tripplanner.ui.components.text.Headline2

enum class TripCardStatus {
    None,
    Upcoming,
    InProgress,
    Ended
}

@Composable
fun TripCard(
    modifier: Modifier = Modifier,
    title: String,
    dateRange: String,
    coverImageUri: String?,
    status: TripCardStatus = TripCardStatus.None,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(Dimensions.radiusCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = TripPlannerTheme.colors.surface)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                if (coverImageUri != null) {
                    AsyncImage(
                        model = coverImageUri,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .background(TripPlannerTheme.colors.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_image_placeholder_48),
                            contentDescription = null,
                            modifier = Modifier.height(Dimensions.iconSizeM)
                        )
                    }
                }

                if (status != TripCardStatus.None) {
                    StatusBadge(
                        status = status,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = Dimensions.spacingS, end = Dimensions.spacingS)
                    )
                }
            }

            Column(
                modifier = Modifier.padding(Dimensions.cardPadding)
            ) {
                Headline2(
                    text = title.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                    color = TripPlannerTheme.colors.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(Dimensions.spacingXS))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_calendar_24),
                            contentDescription = "Calendar",
                            tint = TripPlannerTheme.colors.onSurfaceVariant,
                            modifier = Modifier.width(Dimensions.iconSizeS)
                        )

                        Spacer(modifier = Modifier.width(Dimensions.spacingXS))

                        LabelText(
                            text = dateRange,
                            color = TripPlannerTheme.colors.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(TripPlannerTheme.colors.surfaceVariant.copy(alpha = 0.8f))
                            .padding(6.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_chevron_right_24),
                            contentDescription = null,
                            tint = TripPlannerTheme.colors.onSurfaceVariant,
                            modifier = Modifier.width(Dimensions.spacingL)
                        )
                    }
                }
            }

        }
    }
}

@Composable
private fun StatusBadge(
    status: TripCardStatus,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (status) {
        TripCardStatus.InProgress -> TripPlannerTheme.colors.primary
        TripCardStatus.Ended -> TripPlannerTheme.colors.tertiaryContainer
        TripCardStatus.Upcoming -> TripPlannerTheme.colors.primaryContainer
        else -> TripPlannerTheme.colors.surface
    }

    val textColor = when (status) {
        TripCardStatus.InProgress -> TripPlannerTheme.colors.onPrimary
        TripCardStatus.Ended -> TripPlannerTheme.colors.onTertiaryContainer
        TripCardStatus.Upcoming -> TripPlannerTheme.colors.onPrimaryContainer
        else -> TripPlannerTheme.colors.onSurface
    }
    val text = when (status) {
        TripCardStatus.InProgress -> stringResource(id = R.string.trip_status_in_progress)
        TripCardStatus.Ended -> stringResource(id = R.string.trip_status_ended)
        TripCardStatus.Upcoming -> stringResource(id = R.string.home_status_upcoming)
        else -> ""
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Dimensions.radiusS))
            .background(backgroundColor)
            .padding(horizontal = Dimensions.spacingS, vertical = Dimensions.spacingXS)
    ) {
        LabelText(
            text = text,
            color = textColor
        )
    }
}

@Preview
@Composable
private fun TripCardPreview() {
    TripPlannerTheme {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .background(TripPlannerTheme.colors.background)
        ) {
            TripCard(
                title = "Osaka Castle",
                dateRange = "Jan 12 - Jan 20",
                coverImageUri = null,
                status = TripCardStatus.None
            )
            Spacer(modifier = Modifier.height(16.dp))
            TripCard(
                title = "Tokyo Trip",
                dateRange = "Feb 10 - Feb 15",
                coverImageUri = null,
                status = TripCardStatus.InProgress
            )
            Spacer(modifier = Modifier.height(16.dp))
            TripCard(
                title = "Kyoto Zen",
                dateRange = "Dec 01 - Dec 05",
                coverImageUri = null,
                status = TripCardStatus.Ended
            )
        }
    }
}
