package com.project.tripplanner.ui.components.text

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.project.tripplanner.ui.theme.TripPlannerTheme
import com.project.tripplanner.ui.theme.scaledSp

@Composable
fun BodyRegular(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = TripPlannerTheme.colors.onBackground,
    maxLines: Int = Int.MAX_VALUE,
    textAlign: TextAlign = TextAlign.Start,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    scalable: Boolean = true
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        maxLines = maxLines,
        style = if (scalable) {
            TripPlannerTheme.typography.bodyRegular
        } else {
            TripPlannerTheme.typography.bodyRegular.copy(fontSize = 14.scaledSp())
        },
        textAlign = textAlign,
        overflow = overflow
    )
}
