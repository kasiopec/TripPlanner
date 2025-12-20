package com.project.tripplanner.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.project.tripplanner.R
import com.project.tripplanner.data.model.ItineraryType
import com.project.tripplanner.ui.components.text.Headline3
import com.project.tripplanner.ui.components.text.LabelText
import com.project.tripplanner.ui.components.text.MetaText
import com.project.tripplanner.ui.theme.Dimensions
import com.project.tripplanner.ui.theme.TripPlannerTheme

private const val CHEVRON_ROTATION_COLLAPSED = 90f
private const val CHEVRON_ROTATION_EXPANDED = -90f

@Composable
fun ItineraryItemCard(
    modifier: Modifier = Modifier,
    itinerary: ItineraryUiModel,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onMapClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onDocsClick: () -> Unit = {}
) {
    val colors = TripPlannerTheme.colors

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onExpandedChange(!isExpanded) }
            .animateContentSize(),
        shape = RoundedCornerShape(Dimensions.radiusM),
        color = colors.surface,
        shadowElevation = Dimensions.strokeThick
    ) {
        Column {
            Row(
                modifier = Modifier
                    .padding(Dimensions.spacingM)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(Dimensions.iconContainerSize)
                        .background(
                            color = itinerary.type.getTintColor(),
                            shape = RoundedCornerShape(Dimensions.iconContainerRadius)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = itinerary.type.getIcon()),
                        contentDescription = null,
                        tint = itinerary.type.getIconColor(),
                        modifier = Modifier.size(Dimensions.iconSizeMd)
                    )
                }

                Spacer(modifier = Modifier.width(Dimensions.spacingS))

                Column(modifier = Modifier.weight(1f)) {
                    Headline3(
                        text = itinerary.title,
                        color = colors.onSurface
                    )
                    LabelText(
                        text = stringResource(
                            R.string.itinerary_subtitle_format,
                            itinerary.categoryName,
                            itinerary.durationText
                        ),
                        color = colors.onSurfaceVariant
                    )
                }

                Icon(
                    painter = painterResource(id = R.drawable.ic_chevron_right_24),
                    contentDescription = null,
                    modifier = Modifier
                        .rotate(if (isExpanded) CHEVRON_ROTATION_EXPANDED else CHEVRON_ROTATION_COLLAPSED)
                        .size(Dimensions.iconSizeMd),
                    tint = colors.onSurfaceVariant
                )
            }

            if (isExpanded) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = Dimensions.spacingM),
                    thickness = Dimensions.strokeThin,
                    color = colors.divider
                )

                Row(
                    modifier = Modifier
                        .background(colors.mutedSurface)
                        .fillMaxWidth()
                        .padding(vertical = Dimensions.spacingM),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ItineraryActionItem(
                        icon = if (itinerary.hasMap) R.drawable.ic_map_pin_24 else R.drawable.ic_plus_dashed_32,
                        label = stringResource(R.string.itinerary_action_map),
                        onClick = onMapClick,
                        iconTint = if (itinerary.hasMap) colors.primary else null
                    )
                    ItineraryActionItem(
                        icon = R.drawable.ic_edit_24,
                        label = stringResource(R.string.itinerary_action_edit),
                        onClick = onEditClick,
                        iconTint = colors.primaryStrong
                    )
                    ItineraryActionItem(
                        icon = if (itinerary.hasDocs) R.drawable.ic_document_24 else R.drawable.ic_plus_dashed_32,
                        label = stringResource(R.string.itinerary_action_docs),
                        onClick = onDocsClick,
                        iconTint = if (itinerary.hasDocs) colors.primary else null
                    )
                }
            }
        }
    }
}

@Composable
private fun ItineraryActionItem(
    @DrawableRes icon: Int,
    label: String,
    onClick: () -> Unit,
    iconTint: Color? = null
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = Dimensions.spacingL),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = label,
            tint = iconTint ?: Color.Unspecified,
            modifier = Modifier.size(Dimensions.iconSize)
        )
        Spacer(modifier = Modifier.height(Dimensions.spacingXS))
        MetaText(
            text = label,
            color = TripPlannerTheme.colors.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ItineraryItemCardCollapsedPreview() {
    TripPlannerTheme {
        Box(modifier = Modifier.padding(Dimensions.spacingL)) {
            ItineraryItemCard(
                itinerary = ItineraryUiModel(
                    id = "1",
                    title = "Colosseum",
                    categoryName = "Sightseeing",
                    durationText = "2h",
                    type = ItineraryType.Activity,
                    hasMap = false,
                    hasDocs = false
                ),
                isExpanded = false,
                onExpandedChange = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ItineraryItemCardExpandedPreview() {
    TripPlannerTheme {
        Box(modifier = Modifier.padding(Dimensions.spacingL)) {
            ItineraryItemCard(
                itinerary = ItineraryUiModel(
                    id = "1",
                    title = "Colosseum",
                    categoryName = "Sightseeing",
                    durationText = "2h",
                    type = ItineraryType.Activity,
                    hasMap = true,
                    hasDocs = false
                ),
                isExpanded = true,
                onExpandedChange = {},
            )
        }
    }
}

