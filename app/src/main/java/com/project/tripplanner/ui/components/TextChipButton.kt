package com.project.tripplanner.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.tripplanner.ui.components.text.BodyMedium
import com.project.tripplanner.ui.theme.Dimensions
import com.project.tripplanner.ui.theme.TripPlannerTheme

@Composable
fun TextChipButton(
    modifier: Modifier = Modifier,
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    val colors = TripPlannerTheme.colors
    val contentColor = if (selected) colors.primaryStrong else colors.onBackground

    Surface(
        modifier = modifier
            .heightIn(min = Dimensions.touchTarget)
            .widthIn(min = Dimensions.touchTarget),
        color = Color.Transparent,
        contentColor = contentColor,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        onClick = onClick,
        enabled = enabled
    ) {
        Column(
            modifier = Modifier.padding(horizontal = Dimensions.spacingS),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BodyMedium(
                modifier = Modifier.padding(top = Dimensions.spacingM),
                text = text,
                color = contentColor,
                scalable = false
            )
            Box(
                modifier = Modifier
                    .padding(bottom = Dimensions.spacingS)
                    .size(Dimensions.spacingXS),
                contentAlignment = Alignment.Center
            ) {
                if (selected) {
                    Box(
                        modifier = Modifier
                            .size(Dimensions.spacingXS)
                            .background(color = colors.primaryStrong, shape = CircleShape)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TextChipButtonPreview() {
    TripPlannerTheme {
        Column(modifier = Modifier.padding(Dimensions.spacingL)) {
            TextChipButton(
                text = "Itinerary",
                selected = true,
                onClick = {}
            )
            TextChipButton(
                text = "Places",
                selected = false,
                onClick = {}
            )
        }
    }
}

