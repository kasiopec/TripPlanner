package com.project.tripplanner.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.tripplanner.ui.components.text.Headline2
import com.project.tripplanner.ui.theme.TripPlannerTheme

@Composable
fun LargeGhostButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {
    TextButton(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        onClick = onClick
    ) {
        Headline2(
            text = text,
            color = TripPlannerTheme.colors.primary
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LargeGhostButtonPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(
                horizontal = 16.dp,
                vertical = 10.dp
            )
    ) {
        LargeGhostButton(
            onClick = {
                // do nothing
            },
            text = "Register"
        )
    }
}
