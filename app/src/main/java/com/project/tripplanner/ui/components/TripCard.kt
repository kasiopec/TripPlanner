package com.project.tripplanner.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.project.tripplanner.ui.components.text.LabelText
import com.project.tripplanner.ui.components.text.MetaText
import com.project.tripplanner.ui.theme.Dimensions
import com.project.tripplanner.ui.theme.TripPlannerTheme
import com.project.tripplanner.R
import com.project.tripplanner.ui.components.text.Headline2
import com.project.tripplanner.utils.capitalize


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
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val cardShape = RoundedCornerShape(Dimensions.radiusCard)
    val imageShape = RoundedCornerShape(Dimensions.radiusM)

    Card(
        modifier = modifier
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
        colors = CardDefaults.cardColors(containerColor = TripPlannerTheme.colors.surface)
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.cardPadding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                if (coverImageUri != null) {
                    AsyncImage(
                        model = coverImageUri,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(imageShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(imageShape)
                            .background(TripPlannerTheme.colors.mutedSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_image_placeholder_48),
                            contentDescription = null,
                            tint = TripPlannerTheme.colors.iconMuted,
                            modifier = Modifier.height(Dimensions.iconSizeM)
                        )
                    }
                }

                if (status != TripCardStatus.None) {
                    StatusBadge(
                        status = status,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(Dimensions.spacingS)
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimensions.spacingM))

            Headline2(
                text = title.capitalize(),
                color = TripPlannerTheme.colors.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(Dimensions.spacingXS))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_calendar_24),
                    contentDescription = null,
                    tint = TripPlannerTheme.colors.onSurfaceVariant,
                    modifier = Modifier
                        .width(14.dp)
                        .height(14.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                LabelText(
                    text = dateRange,
                    color = TripPlannerTheme.colors.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.W500
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(
    status: TripCardStatus,
    modifier: Modifier = Modifier
) {
    val text = when (status) {
        TripCardStatus.InProgress -> stringResource(id = R.string.trip_status_in_progress)
        TripCardStatus.Ended -> stringResource(id = R.string.trip_status_ended)
        TripCardStatus.Upcoming -> stringResource(id = R.string.home_status_upcoming)
        else -> ""
    }

    val textColor = when (status) {
        TripCardStatus.Upcoming -> TripPlannerTheme.colors.primary
        TripCardStatus.InProgress -> TripPlannerTheme.additionalColors.success
        TripCardStatus.Ended -> TripPlannerTheme.colors.secondary
        else -> TripPlannerTheme.colors.onSurfaceVariant
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(Dimensions.radiusL),
        colors = CardDefaults.cardColors(
            containerColor = TripPlannerTheme.colors.surface.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        MetaText(
            modifier = Modifier.padding(horizontal = Dimensions.spacingS, vertical = Dimensions.spacingXS),
            text = text.uppercase(),
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
                .padding(Dimensions.spacingL)
                .background(TripPlannerTheme.colors.background)
        ) {
            TripCard(
                title = "Adventure 2",
                dateRange = "Dec 16, 2025",
                coverImageUri = null,
                status = TripCardStatus.Upcoming
            )
        }
    }
}
