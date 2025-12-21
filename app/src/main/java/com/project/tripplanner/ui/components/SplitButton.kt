package com.project.tripplanner.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.tripplanner.R
import com.project.tripplanner.ui.components.text.Headline3
import com.project.tripplanner.ui.theme.Dimensions
import com.project.tripplanner.ui.theme.TripPlannerTheme

@Composable
fun SplitButton(
    onAddPlacesClick: () -> Unit,
    onReorderClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .heightIn(min = Dimensions.buttonMinHeight)
            .wrapContentWidth(),
        shape = RoundedCornerShape(Dimensions.radiusButton),
        color = TripPlannerTheme.colors.primary,
        contentColor = TripPlannerTheme.colors.onPrimary,
        shadowElevation = Dimensions.elevationMedium
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .clip(
                        RoundedCornerShape(
                            topStart = Dimensions.radiusButton,
                            bottomStart = Dimensions.radiusButton,
                            topEnd = Dimensions.radiusButton,
                            bottomEnd = Dimensions.radiusButton
                        )
                    )
                    .clickable(
                        onClick = onAddPlacesClick,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = TripPlannerTheme.colors.onPrimary)
                    )
                    .padding(horizontal = Dimensions.spacingL),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_plus_24),
                    contentDescription = null,
                    tint = TripPlannerTheme.colors.onPrimary
                )
                Spacer(modifier = Modifier.width(Dimensions.spacingS))
                Headline3(
                    text = stringResource(id = R.string.add_places_button_label),
                    color = TripPlannerTheme.colors.onPrimary
                )
            }

            VerticalDivider(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(Dimensions.strokeThin),
                color = TripPlannerTheme.colors.onPrimary.copy(alpha = 0.2f)
            )

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .clip(
                        RoundedCornerShape(
                            topStart = Dimensions.radiusButton,
                            bottomStart = Dimensions.radiusButton,
                            topEnd = Dimensions.radiusButton,
                            bottomEnd = Dimensions.radiusButton
                        )
                    )
                    .clickable(
                        onClick = onReorderClick,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = TripPlannerTheme.colors.onPrimary)
                    )
                    .padding(horizontal = Dimensions.spacingL),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_menu_24),
                    contentDescription = stringResource(id = R.string.add_places_reorder_button_label),
                    tint = TripPlannerTheme.colors.onPrimary
                )
            }
        }
    }
}

@Preview
@Composable
fun SplitButtonPreview() {
    TripPlannerTheme {
        Box(
            modifier = Modifier
                .padding(20.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            SplitButton(
                onAddPlacesClick = {},
                onReorderClick = {}
            )
        }
    }
}
