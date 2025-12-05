package com.project.tripplanner.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.tripplanner.R
import com.project.tripplanner.ui.components.text.Headline2
import com.project.tripplanner.ui.theme.TripPlannerTheme

@Composable
fun GoogleSignInButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val colors = TripPlannerTheme.colors
    OutlinedButton(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        onClick = onClick,
        shape = RoundedCornerShape(5.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Unspecified,
            contentColor = colors.onBackground,
            disabledContainerColor = colors.tertiaryContainer,
            disabledContentColor = colors.onTertiaryContainer
        ),
        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                modifier = Modifier.padding(start = 16.dp),
                painter = painterResource(id = R.drawable.ic_google_colored_32),
                contentDescription = context.resources.getString(R.string.login_google_button_label)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Headline2(text = context.resources.getString(R.string.login_google_button_label))
            }
        }
    }

}


@Preview(showBackground = true)
@Composable
private fun GoogleSignInButtonPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(
                horizontal = 16.dp,
                vertical = 10.dp
            )
    ) {
        GoogleSignInButton(
            onClick = {
                // do nothing
            }
        )
    }
}
