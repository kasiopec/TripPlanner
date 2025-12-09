package com.project.tripplanner.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import com.project.tripplanner.ui.components.text.BodyRegular
import com.project.tripplanner.ui.components.text.MetaText
import com.project.tripplanner.ui.theme.Dimensions
import com.project.tripplanner.ui.theme.TripPlannerTheme

@Composable
fun CompactHero(
    modifier: Modifier = Modifier,
    @StringRes labelRes: Int,
    title: String,
    subtitle: String
) {
    val colors = TripPlannerTheme.colors
    val brush = Brush.linearGradient(
        colors = listOf(colors.primaryStrong, colors.primary)
    )

    Column(
        modifier = modifier
            .background(brush)
            .fillMaxWidth()
            .padding(
                horizontal = Dimensions.spacingL,
                vertical = Dimensions.spacingM
            ),
        verticalArrangement = Arrangement.spacedBy(Dimensions.spacingXS)
    ) {
        MetaText(
            text = stringResource(id = labelRes),
            color = colors.onPrimary.copy(alpha = 0.8f)
        )
        BodyRegular(
            text = title,
            color = colors.onPrimary
        )
        BodyRegular(
            text = subtitle,
            color = colors.onPrimary.copy(alpha = 0.9f)
        )
    }
}
