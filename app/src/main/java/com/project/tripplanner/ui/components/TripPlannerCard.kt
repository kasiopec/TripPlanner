package com.project.tripplanner.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.tripplanner.R
import com.project.tripplanner.ui.components.text.BodyMedium
import com.project.tripplanner.ui.components.text.Headline3
import com.project.tripplanner.ui.components.text.MetaText
import com.project.tripplanner.ui.theme.TripPlannerTheme
import java.util.Locale

@Composable
fun TripPlannerCard(
    modifier: Modifier = Modifier,
    title: String,
    description: String? = null,
    badgeText: String? = null,
    leadingIcon: Painter? = null,
    accent: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: (@Composable ColumnScope.() -> Unit)? = null
) {
    val shape = RoundedCornerShape(14.dp)
    val colors = TripPlannerTheme.colors
    val containerColor = if (accent) colors.mutedSurface else colors.surface
    val border = if (accent) BorderStroke(1.dp, colors.primary.copy(alpha = 0.1f)) else null
    val iconContainerColor = Color(0xFFF0F3FA)
    val badgeContainerColor = colors.primaryContainer

    val cardContent: @Composable () -> Unit = {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (leadingIcon != null || !badgeText.isNullOrBlank()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (leadingIcon != null) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(color = iconContainerColor, shape = RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = leadingIcon,
                                contentDescription = null,
                                tint = colors.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    if (!badgeText.isNullOrBlank()) {
                        MetaText(
                            text = badgeText.uppercase(Locale.getDefault()),
                            color = colors.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .background(color = badgeContainerColor, shape = RoundedCornerShape(16.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Headline3(
                text = title,
                color = colors.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (!description.isNullOrBlank()) {
                BodyMedium(
                    text = description,
                    color = colors.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            content?.invoke(this)
        }
    }

    Surface(
        modifier = modifier,
        shape = shape,
        color = containerColor,
        shadowElevation = 2.dp,
        border = border
    ) {
        cardContent()
    }
}

@Preview(showBackground = true)
@Composable
private fun TripPlannerCardPreview() {
    TripPlannerTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TripPlannerCard(
                title = "Keep everything organized",
                description = "Plan, track, and adjust your upcoming itineraries with smart suggestions and reminders.",
                badgeText = "New",
                leadingIcon = painterResource(id = R.drawable.ic_calendar_24)
            )
            TripPlannerCard(
                title = "Saved spots for Lisbon",
                description = "See your day-by-day plan, notes, and bookings in one place.",
                badgeText = "Day 01",
                accent = true,
                leadingIcon = painterResource(id = R.drawable.ic_user_24)
            )
        }
    }
}
