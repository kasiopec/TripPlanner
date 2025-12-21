package com.project.tripplanner.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
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
    val cardShape = RoundedCornerShape(Dimensions.radiusM)
    val contentAnimationSpec: FiniteAnimationSpec<IntSize> = if (isExpanded) {
        spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        )
    } else {
        spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        )
    }
    val cardInteractionSource = remember { MutableInteractionSource() }


    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = cardShape,
        colors = CardDefaults.cardColors(
            containerColor = colors.surface,
            disabledContainerColor = colors.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = Dimensions.strokeThick,
            disabledElevation = Dimensions.strokeThick
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(cardShape)
                .animateContentSize(animationSpec = contentAnimationSpec)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = cardInteractionSource,
                        indication = ripple(bounded = true)
                    ) { onExpandedChange(!isExpanded) }
                    .padding(Dimensions.spacingM),
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
                    thickness = Dimensions.strokeThin,
                    color = colors.divider
                )

                Row(
                    modifier = Modifier
                        .background(color = colors.mutedSurface)
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
    val colors = TripPlannerTheme.colors
    val iconInteractionSource = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier
            .padding(horizontal = Dimensions.spacingL),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(Dimensions.touchTarget)
                .clip(CircleShape)
                .clickable(
                    interactionSource = iconInteractionSource,
                    indication = ripple(bounded = true),
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = label,
                tint = iconTint ?: Color.Unspecified,
                modifier = Modifier.size(Dimensions.iconSize)
            )
        }
        Spacer(modifier = Modifier.height(Dimensions.spacingXS))
        MetaText(
            text = label,
            color = colors.onSurfaceVariant
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
