package com.project.tripplanner.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.tripplanner.R
import com.project.tripplanner.ui.components.text.TitleSmallBold

@Composable
fun LargeRoundedButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    isEnabled: Boolean = true,
    icon: @Composable() (BoxScope.() -> Unit)? = null
) {
    val textColor = if (isEnabled) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onTertiaryContainer
    }
    Button(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        onClick = onClick,
        shape = RoundedCornerShape(5.dp),
        enabled = isEnabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
            disabledContentColor = MaterialTheme.colorScheme.onTertiaryContainer
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            icon?.let { Box(content = icon) }
            TitleSmallBold(
                text = text,
                color = textColor
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
private fun LargeRoundedButtonPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(
                horizontal = 16.dp,
                vertical = 10.dp
            )
    ) {
        LargeRoundedButton(
            onClick = {
                // do nothing
            },
            text = "Login"
        )
        LargeRoundedButton(
            modifier = Modifier.padding(top = 4.dp),
            onClick = {
                // do nothing
            },
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_google_colored_32),
                    contentDescription = ""
                )
            },
            text = "Login"
        )
    }
}