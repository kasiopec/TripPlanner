package com.project.tripplanner.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.size.Dimension
import com.project.tripplanner.R
import com.project.tripplanner.ui.components.text.Headline2
import com.project.tripplanner.ui.components.text.MetaText
import com.project.tripplanner.ui.theme.Dimensions
import com.project.tripplanner.ui.theme.TripPlannerTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Immutable
data class DayItem(
    val dayIndex: Int,
    val date: LocalDate,
    val isSelected: Boolean
)

@Composable
fun CalendarRow(
    modifier: Modifier = Modifier,
    days: List<DayItem>,
    onDaySelected: (LocalDate) -> Unit
) {
    val listState = rememberLazyListState()

    LazyRow(
        modifier = modifier,
        state = listState,
        contentPadding = PaddingValues(horizontal = Dimensions.spacingL),
        horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingS)
    ) {
        items(
            items = days,
            key = { it.date.toEpochDay() }
        ) { dayItem ->
            DayPill(
                dayItem = dayItem,
                onClick = { onDaySelected(dayItem.date) }
            )
        }
    }
}

@Composable
private fun DayPill(
    modifier: Modifier = Modifier,
    dayItem: DayItem,
    onClick: () -> Unit
) {
    val colors = TripPlannerTheme.colors
    val backgroundColor = if (dayItem.isSelected) colors.primary else colors.inactiveContainer
    val labelTextColor = if (dayItem.isSelected) colors.onPrimary else colors.secondary
    val dateTextColor = if (dayItem.isSelected) colors.onPrimary else colors.onSurface

    val accessibilityDescription = stringResource(
        R.string.calendar_day_accessibility,
        dayItem.dayIndex,
        dayItem.date.month.getDisplayName(TextStyle.FULL, Locale.getDefault()),
        dayItem.date.dayOfMonth
    )

    Surface(
        modifier = modifier
            .width(Dimensions.touchTarget)
            .height(60.dp)
            .semantics { contentDescription = accessibilityDescription },
        shape = RoundedCornerShape(Dimensions.radiusButton),
        color = backgroundColor,
        shadowElevation = if (dayItem.isSelected) 2.dp else 0.dp,
        onClick = onClick
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            MetaText(
                text = stringResource(R.string.calendar_day_label, dayItem.dayIndex),
                color = labelTextColor,
                textAlign = TextAlign.Center,
                scalable = false
            )
            Headline2(
                text = dayItem.date.dayOfMonth.toString(),
                color = dateTextColor,
                textAlign = TextAlign.Center,
                scalable = false
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF0F1F6)
@Composable
private fun CalendarRowPreview() {
    val startDate = LocalDate.of(2024, 5, 12)
    val sampleDays = (0..6).map { index ->
        DayItem(
            dayIndex = index + 1,
            date = startDate.plusDays(index.toLong()),
            isSelected = index == 0
        )
    }

    TripPlannerTheme {
        Box(modifier = Modifier.background(color = TripPlannerTheme.colors.onSurfaceVariant)) {
            CalendarRow(
                modifier = Modifier.padding(vertical = Dimensions.spacingS),
                days = sampleDays,
                onDaySelected = {}
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF0F1F6)
@Composable
private fun DayPillActivePreview() {
    TripPlannerTheme {
        DayPill(
            dayItem = DayItem(
                dayIndex = 1,
                date = LocalDate.of(2024, 5, 12),
                isSelected = true
            ),
            onClick = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF0F1F6)
@Composable
private fun DayPillInactivePreview() {
    TripPlannerTheme {
        DayPill(
            dayItem = DayItem(
                dayIndex = 2,
                date = LocalDate.of(2024, 5, 13),
                isSelected = false
            ),
            onClick = {}
        )
    }
}
