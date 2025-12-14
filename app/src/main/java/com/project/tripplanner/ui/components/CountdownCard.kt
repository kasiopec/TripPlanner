package com.project.tripplanner.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.project.tripplanner.R
import com.project.tripplanner.ui.components.text.DisplayText
import com.project.tripplanner.ui.components.text.MetaText
import com.project.tripplanner.ui.theme.Dimensions
import com.project.tripplanner.ui.theme.TripPlannerTheme
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.min

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CountdownCard(
    modifier: Modifier = Modifier,
    destination: String,
    until: ZonedDateTime,
    heroStyle: Boolean = false
) {
    val colors = TripPlannerTheme.colors

    var now by remember { mutableStateOf(ZonedDateTime.now(until.zone)) }

    LaunchedEffect(Unit) {
        // Update the time only once per minute to save battery and match the display unit
        while (true) {
            now = ZonedDateTime.now(until.zone)
            delay(60000)
        }
    }

    val displayUnits = remember(now, until) {
        calculateDisplayUnits(now, until)
    }

    val backgroundColor = colors.surface
    val content: @Composable () -> Unit = {
        Box(
            modifier = Modifier
                .background(backgroundColor) // Use solid light background
                .fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = Dimensions.spacingL,
                        bottom = Dimensions.spacingXL
                    )
            ) {
                MetaText(
                    text = stringResource(id = R.string.home_countdown_title),
                    color = colors.secondary,
                    modifier = Modifier.padding(bottom = Dimensions.spacingXS)
                )

                DisplayText(
                    text = destination.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                    color = colors.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = Dimensions.spacingL)
                )

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CountdownUnit(
                        value = displayUnits.unit1Value,
                        label = stringResource(displayUnits.unit1Label)
                    )
                    TimeUnitSeparator()
                    CountdownUnit(
                        value = displayUnits.unit2Value,
                        label = stringResource(displayUnits.unit2Label)
                    )
                    TimeUnitSeparator()
                    CountdownUnit(
                        value = displayUnits.unit3Value,
                        label = stringResource(displayUnits.unit3Label)
                    )
                }
            }
        }
    }

    if (heroStyle) {
        Box(
            modifier = modifier
                .fillMaxWidth()
        ) {
            content()
        }
    } else {
        Card(
            modifier = modifier
                .fillMaxWidth()
                // RECOMMENDATION (from DS): Use radiusL (12dp) for cards
                .clip(RoundedCornerShape(Dimensions.radiusL)),
            // RECOMMENDATION (from DS): Use low elevation
            elevation = CardDefaults.cardElevation(defaultElevation = Dimensions.strokeThick),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            )
        ) {
            content()
        }
    }
}

private fun calculateDisplayUnits(now: ZonedDateTime, until: ZonedDateTime): DisplayUnits {
    val duration = Duration.between(now, until)

    if (duration.isNegative) {
        return DisplayUnits(
            0, R.string.home_countdown_days,
            0, R.string.home_countdown_hours,
            0, R.string.home_countdown_mins
        )
    }

    val totalSeconds = duration.seconds
    val days = duration.toDays()

    return if (days > 30) {
        val months = ChronoUnit.MONTHS.between(now.toLocalDate(), until.toLocalDate())
        val displayMonths = months.toInt()
        val remainingDays = ChronoUnit.DAYS.between(now.plusMonths(months).toLocalDate(), until.toLocalDate()).toInt()
        val hours = ((totalSeconds % (24 * 3600)) / 3600).toInt()

        DisplayUnits(
            displayMonths, R.string.home_countdown_months,
            min(remainingDays, 30), R.string.home_countdown_days, // Cap days to 30 for display clarity
            hours, R.string.home_countdown_hours
        )
    } else {
        val displayDays = days.toInt()
        val hours = ((totalSeconds % (24 * 3600)) / 3600).toInt()
        val minutes = ((totalSeconds % 3600) / 60).toInt()

        DisplayUnits(
            displayDays, R.string.home_countdown_days,
            hours, R.string.home_countdown_hours,
            minutes, R.string.home_countdown_mins
        )
    }
}

// Renamed from TimeUnitSeparator2 to TimeUnitSeparator and simplified
@Composable
fun TimeUnitSeparator() {
    DisplayText(
        text = ":",
        color = TripPlannerTheme.colors.onSurfaceVariant.copy(alpha = 0.35f),
        modifier = Modifier
            .padding(horizontal = Dimensions.spacingS)
            // Adjust vertical alignment to match the DisplayText number's baseline
            .padding(bottom = Dimensions.spacingL),
        scalable = false
    )
}

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalAnimationApi::class)
// Renamed from CountdownUnit2 to CountdownUnit and simplified signature
@Composable
fun CountdownUnit(
    value: Int,
    label: String,
) {
    // RECOMMENDATION 2: Removed Surface, making the timer text-only (airier UI)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Box is still useful for AnimatedContent alignment but no longer draws the box surface
        Box(
            contentAlignment = Alignment.Center,
            // Adjust modifier to maintain proper spacing without the fixed size of the old box
            modifier = Modifier.padding(horizontal = Dimensions.spacingXS)
        ) {
            AnimatedContent(
                targetState = value,
                transitionSpec = {
                    (slideInVertically { height -> height } + fadeIn() togetherWith
                            slideOutVertically { height -> -height } + fadeOut()).using(
                        SizeTransform(clip = true)
                    )
                },
                label = "Unit"
            ) { targetValue ->
                DisplayText(
                    text = String.format("%02d", targetValue),
                    // RECOMMENDATION 3: Use Primary Brand color for numbers
                    color = TripPlannerTheme.colors.primary,
                    scalable = false
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimensions.spacingXS))

        MetaText(
            text = label,
            // RECOMMENDATION 3: Use dark secondary color for labels
            color = TripPlannerTheme.colors.secondary
        )
    }
}

@Composable
@Preview
fun previewCT() {
    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
    ) {
        CountdownCard(
            destination = "Santorini, Greece",
            until = ZonedDateTime.now().plusMonths(2).plusDays(
                5
            )
        )
    }
}
