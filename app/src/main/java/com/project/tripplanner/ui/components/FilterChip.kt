package com.project.tripplanner.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.tripplanner.ui.components.text.BodyMedium
import com.project.tripplanner.ui.theme.Dimensions
import com.project.tripplanner.ui.theme.TripPlannerTheme

@Composable
fun FilterChip(
    modifier: Modifier = Modifier,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    leadingIcon: (@Composable (() -> Unit))? = null,
    trailingIcon: (@Composable (() -> Unit))? = null
) {
    val colors = TripPlannerTheme.colors
    val backgroundColor = if (selected) colors.primary else colors.surface
    val contentColor = if (selected) colors.onPrimary else colors.onSurface
    val border = if (selected) null else BorderStroke(Dimensions.strokeThin, colors.outline)

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(Dimensions.radiusM),
        color = backgroundColor,
        contentColor = contentColor,
        border = border,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .heightIn(min = Dimensions.chipHeight)
                .padding(
                    horizontal = Dimensions.spacingM,
                    vertical = Dimensions.spacingXS
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingXS)
        ) {
            leadingIcon?.invoke()
            BodyMedium(
                text = label,
                color = contentColor,
                scalable = false
            )
            trailingIcon?.invoke()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FilterChipPreview() {
    TripPlannerTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingS),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(Dimensions.spacingM)
        ) {
            FilterChip(
                label = "Ended",
                selected = false,
                onClick = {}
            )
            FilterChip(
                label = "All",
                selected = true,
                onClick = {}
            )
        }
    }
}
