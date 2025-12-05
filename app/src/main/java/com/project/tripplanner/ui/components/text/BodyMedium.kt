package com.project.tripplanner.ui.components.text

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import com.project.tripplanner.ui.theme.TripPlannerTheme
import com.project.tripplanner.ui.theme.scaledSp

@Composable
fun BodyMedium(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = TripPlannerTheme.colors.onBackground,
    maxLines: Int = Int.MAX_VALUE,
    textAlign: TextAlign = TextAlign.Start,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    scalable: Boolean = true,
    textDecoration: TextDecoration? = null,
    fontWeight: FontWeight? = null
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        maxLines = maxLines,
        textDecoration = textDecoration,
        fontWeight = fontWeight,
        style = if (scalable) TripPlannerTheme.typography.body else TripPlannerTheme.typography.body.copy(fontSize = 14.scaledSp()),
        textAlign = textAlign,
        overflow = overflow
    )
}
