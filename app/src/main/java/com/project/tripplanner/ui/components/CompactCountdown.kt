package com.project.tripplanner.ui.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.tripplanner.R
import com.project.tripplanner.ui.components.text.BodyRegular
import com.project.tripplanner.ui.components.text.Headline3
import com.project.tripplanner.ui.theme.Dimensions
import com.project.tripplanner.ui.theme.TripPlannerTheme
import com.project.tripplanner.utils.capitalize
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun CompactCountdown(
    modifier: Modifier = Modifier,
    tripTitle: String,
    dateRangeText: String,
    countdownTargetEpochMillis: Long,
) {
    val colors = TripPlannerTheme.colors
    val context = LocalContext.current
    val cardShape = RoundedCornerShape(Dimensions.radiusCard)
    val leadingIconShape = RoundedCornerShape(Dimensions.radiusS)
    val pillShape = RoundedCornerShape(Dimensions.radiusL)

    val countdownText by produceState(
        initialValue = formatCountdownText(
            context = context,
            nowEpochMillis = System.currentTimeMillis(),
            targetEpochMillis = countdownTargetEpochMillis
        ),
        key1 = countdownTargetEpochMillis
    ) {
        while (isActive) {
            val now = System.currentTimeMillis()
            val remainingMillis = countdownTargetEpochMillis - now
            if (remainingMillis <= 0L) {
                value = context.getString(R.string.home_countdown_now)
                return@produceState
            }

            value = formatCountdownText(
                context = context,
                nowEpochMillis = now,
                targetEpochMillis = countdownTargetEpochMillis
            )

            val delayMillis = if (remainingMillis >= TimeUnit.DAYS.toMillis(1)) {
                val oneHourMillis = TimeUnit.HOURS.toMillis(1)
                (oneHourMillis - (now % oneHourMillis)).coerceIn(1L, oneHourMillis)
            } else {
                val oneMinuteMillis = TimeUnit.MINUTES.toMillis(1)
                (oneMinuteMillis - (now % oneMinuteMillis)).coerceIn(1L, oneMinuteMillis)
            }
            delay(delayMillis)
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface),
        shape = cardShape,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface)
    ) {
        Row(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(Dimensions.cardPadding),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingM),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(leadingIconShape)
                    .background(colors.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_plane_24),
                    contentDescription = null,
                    tint = colors.primary
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Dimensions.spacingXS)
            ) {
                Headline3(
                    text = tripTitle.capitalize(),
                    color = colors.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                BodyRegular(
                    text = dateRangeText,
                    color = colors.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            CountdownPill(
                text = countdownText,
                iconTint = colors.primary,
                backgroundColor = colors.primaryContainer,
                textColor = colors.primary,
                shape = pillShape
            )
        }
    }
}

@Composable
private fun CountdownPill(
    modifier: Modifier = Modifier,
    text: String,
    iconTint: Color,
    backgroundColor: Color,
    textColor: Color,
    shape: RoundedCornerShape
) {
    Row(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_clock_24),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = iconTint
        )
        Text(
            text = text,
            color = textColor,
            maxLines = 1,
            overflow = TextOverflow.Clip,
            style = TripPlannerTheme.typography.label.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

private fun formatCountdownText(
    context: Context,
    nowEpochMillis: Long,
    targetEpochMillis: Long
): String {
    val remainingMillis = targetEpochMillis - nowEpochMillis
    if (remainingMillis <= 0L) return context.getString(R.string.home_countdown_now)

    val oneDayMillis = TimeUnit.DAYS.toMillis(1)
    if (remainingMillis >= oneDayMillis) {
        val days = ((remainingMillis + oneDayMillis - 1) / oneDayMillis).toInt()
        return context.getString(R.string.home_compact_countdown_days_format, days)
    }

    val oneMinuteMillis = TimeUnit.MINUTES.toMillis(1)
    val totalMinutes = ((remainingMillis + oneMinuteMillis - 1) / oneMinuteMillis).toInt()
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return String.format("%02d:%02d", hours, minutes)
}

@Preview(showBackground = true)
@Composable
private fun CompactCountdownPreview() {
    TripPlannerTheme {
        Box(modifier = Modifier.background(TripPlannerTheme.colors.background)) {
            CompactCountdown(
                tripTitle = "Trip to Bali",
                dateRangeText = "Dec 14 - Dec 28",
                countdownTargetEpochMillis = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(2) + TimeUnit.MINUTES.toMillis(14)
            )
        }
    }
}
