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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.tripplanner.R
import com.project.tripplanner.ui.components.text.DisplayText
import com.project.tripplanner.ui.components.text.Headline1
import com.project.tripplanner.ui.components.text.MetaText
import com.project.tripplanner.ui.theme.Dimensions
import com.project.tripplanner.ui.theme.TripPlannerTheme
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CountdownCard(
    destination: String,
    until: ZonedDateTime,
    modifier: Modifier = Modifier,
    heroStyle: Boolean = false
) {
    val colors = TripPlannerTheme.colors

    var now by remember { mutableStateOf(ZonedDateTime.now(until.zone)) }

    LaunchedEffect(Unit) {
        while (true) {
            now = ZonedDateTime.now(until.zone)
            delay(60000)
        }
    }

    val displayUnits = remember(now, until) {
        calculateDisplayUnits(now, until)
    }

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            colors.primary,
            colors.primaryStrong
        )
    )

    val content: @Composable () -> Unit = {
        Box(
            modifier = Modifier
                .background(backgroundBrush)
                .fillMaxWidth()
                .padding(Dimensions.spacingL)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                MetaText(
                    text = stringResource(id = R.string.home_countdown_title),
                    color = colors.onPrimary.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = Dimensions.spacingXS)
                )

                Headline1(
                    text = destination,
                    color = colors.onPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = Dimensions.spacingXL)
                )

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CountdownUnit(
                        value = displayUnits.unit1Value,
                        label = stringResource(displayUnits.unit1Label),
                        textColor = colors.primaryStrong
                    )
                    TimeUnitSeparator(color = colors.onPrimary)
                    CountdownUnit(
                        value = displayUnits.unit2Value,
                        label = stringResource(displayUnits.unit2Label),
                        textColor = colors.primaryStrong
                    )
                    TimeUnitSeparator(color = colors.onPrimary)
                    CountdownUnit(
                        value = displayUnits.unit3Value,
                        label = stringResource(displayUnits.unit3Label),
                        textColor = colors.primaryStrong
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
                .clip(RoundedCornerShape(Dimensions.radiusL)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            )
        ) {
            content()
        }
    }
}

data class DisplayUnits(
    val unit1Value: Int, val unit1Label: Int,
    val unit2Value: Int, val unit2Label: Int,
    val unit3Value: Int, val unit3Label: Int
)

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
            remainingDays, R.string.home_countdown_days,
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

@Composable
fun TimeUnitSeparator(color: Color) {
    DisplayText(
        text = ":",
        color = color.copy(alpha = 0.6f),
        modifier = Modifier
            .padding(horizontal = Dimensions.spacingS)
            .padding(bottom = Dimensions.spacingL),
        scalable = false
    )
}

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CountdownUnit(
    value: Int,
    label: String,
    textColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            color = TripPlannerTheme.colors.surface.copy(alpha = 0.9f),
            shape = RoundedCornerShape(Dimensions.radiusM),
            modifier = Modifier
                .width(64.dp)
                .height(68.dp),
            tonalElevation = 4.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
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
                        color = textColor,
                        scalable = false
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Dimensions.spacingXS))

        MetaText(
            text = label,
            color = TripPlannerTheme.colors.onPrimary.copy(alpha = 0.8f)
        )
    }
}

@Preview
@Composable
private fun CountdownCardMonthsPreview() {
    TripPlannerTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            CountdownCard(
                destination = "Santorini, Greece",
                until = ZonedDateTime.now().plusMonths(2).plusDays(5).plusHours(4)
            )
        }
    }
}

@Preview
@Composable
private fun CountdownCardUrgentPreview() {
    TripPlannerTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            CountdownCard(
                destination = "Tokyo",
                until = ZonedDateTime.now().plusHours(2).plusMinutes(45).plusSeconds(10)
            )
        }
    }
}

