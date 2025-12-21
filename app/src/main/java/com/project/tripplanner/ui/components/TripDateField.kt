package com.project.tripplanner.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.tripplanner.R
import com.project.tripplanner.ui.components.text.BodyMedium
import com.project.tripplanner.ui.components.text.BodyRegular
import com.project.tripplanner.ui.components.text.LabelText
import com.project.tripplanner.ui.theme.Dimensions
import com.project.tripplanner.ui.theme.TripPlannerTheme

@Composable
fun TripDateField(
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String = "",
    errorMessage: String? = null,
    isError: Boolean = false,
    enabled: Boolean = true
) {
    val colors = TripPlannerTheme.colors

    val borderColor = when {
        !enabled -> colors.tertiaryContainer
        isError -> colors.error
        else -> TripPlannerTheme.additionalColors.inactive
    }

    val contentColor = when {
        !enabled -> TripPlannerTheme.additionalColors.inactive
        value.isEmpty() -> TripPlannerTheme.additionalColors.inactive
        else -> colors.onBackground
    }

    val iconColor = when {
        !enabled -> colors.iconMuted
        else -> TripPlannerTheme.additionalColors.inactive
    }

    Column(modifier = modifier) {
        if (label != null) {
            LabelText(
                modifier = Modifier.padding(bottom = 6.dp),
                text = label,
                color = if (enabled) colors.onBackground else TripPlannerTheme.additionalColors.inactive,
                fontWeight = FontWeight.W700
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimensions.textFieldHeight)
                .clip(RoundedCornerShape(Dimensions.radiusM))
                .background(
                    color = colors.background,
                    shape = RoundedCornerShape(Dimensions.radiusM)
                )
                .border(
                    width = Dimensions.strokeThin,
                    color = borderColor,
                    shape = RoundedCornerShape(Dimensions.radiusM)
                )
                .then(
                    if (enabled) {
                        Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onClick
                        )
                    } else {
                        Modifier
                    }
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.padding(horizontal = Dimensions.spacingL),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    BodyMedium(
                        text = value.ifEmpty { placeholder },
                        color = contentColor
                    )
                }

                Spacer(modifier = Modifier.width(Dimensions.spacingS))

                Icon(
                    painter = painterResource(R.drawable.ic_calendar_24),
                    contentDescription = null,
                    modifier = Modifier.size(Dimensions.iconSize24),
                    tint = iconColor
                )
            }
        }

        if (isError && errorMessage != null) {
            BodyRegular(
                modifier = Modifier.padding(top = Dimensions.spacingS, start = Dimensions.spacingS),
                text = errorMessage,
                color = colors.error
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TripDateFieldPreview() {
    TripPlannerTheme {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .background(TripPlannerTheme.colors.background)
        ) {
            TripDateField(
                label = "Start Date",
                value = "",
                placeholder = "Select date",
                onClick = {}
            )

            Spacer(modifier = Modifier.height(16.dp))

            TripDateField(
                label = "End Date",
                value = "Dec 15, 2025",
                placeholder = "Select date",
                onClick = {}
            )

            Spacer(modifier = Modifier.height(16.dp))

            TripDateField(
                label = "Start Date",
                value = "",
                placeholder = "Select date",
                isError = true,
                errorMessage = "Start date is required",
                onClick = {}
            )

            Spacer(modifier = Modifier.height(16.dp))

            TripDateField(
                label = "End Date",
                value = "Dec 20, 2025",
                placeholder = "Select date",
                enabled = false,
                onClick = {}
            )
        }
    }
}
