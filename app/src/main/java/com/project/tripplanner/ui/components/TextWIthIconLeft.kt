package com.project.tripplanner.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.tripplanner.ui.IcError24
import com.project.tripplanner.ui.Icons
import com.project.tripplanner.ui.components.text.BodyMedium
import com.project.tripplanner.ui.theme.TripPlannerTheme

@Composable
fun TextWithLeftIcon(
    modifier: Modifier = Modifier,
    icon: @Composable BoxScope.() -> Unit,
    text: String,
    textColor: Color = TripPlannerTheme.colors.onBackground
) {
    Row(modifier = modifier) {
        Box(
            content = icon
        )
        BodyMedium(
            modifier = Modifier.padding(start = 8.dp),
            text = text,
            color = textColor
        )
    }
}

@Preview(
    name = "TextWithIconLeftPreview (light)"
)
@Preview(
    name = "TextWithIconLeftPreview (dark)",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun TextWithIconLeftPreview() {
    Column() {
        TextWithLeftIcon(
            icon = {
                Icons.IcError24(tint = TripPlannerTheme.colors.error)
            },
            text = "Some error text",
            textColor = TripPlannerTheme.colors.error
        )
    }
}
