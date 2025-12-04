package com.project.tripplanner.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.tripplanner.R
import com.project.tripplanner.ui.theme.TripPlannerTheme

@Composable
fun FullScreenError(
    modifier: Modifier = Modifier,
    titleText: String,
    messageText: String? = null,
    primaryActionText: String,
    onPrimaryActionClick: () -> Unit,
    secondaryActionText: String? = null,
    onSecondaryActionClick: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 96.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ErrorStateImage()

                // Title (H4 style from Figma)
                Text(
                    text = titleText,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 44.2.sp,
                    color = colorResource(id = R.color.error_title_text),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                if (!messageText.isNullOrBlank()) {
                    Text(
                        text = messageText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 27.6.sp,
                        letterSpacing = 0.15.sp,
                        color = colorResource(id = R.color.error_body_text),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!secondaryActionText.isNullOrBlank() && onSecondaryActionClick != null) {
                OutlinedButton(
                    onClick = onSecondaryActionClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, colorResource(id = R.color.error_button_secondary_border)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = colorResource(id = R.color.error_button_secondary_text)
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = secondaryActionText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }
            }

            // Primary action (Continue button - Contained style)
            Button(
                onClick = onPrimaryActionClick,
                modifier = Modifier
                    .weight(1f)
                    .shadow(
                        elevation = 2.dp,
                        shape = RoundedCornerShape(8.dp),
                        ambientColor = Color(0x33DA100B),
                        spotColor = Color(0x33DA100B)
                    ),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.error_button_primary_bg),
                    contentColor = colorResource(id = R.color.error_button_primary_text)
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = primaryActionText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
            }
        }
    }
}

@Composable
private fun ErrorStateImage() {
    Box(
        modifier = Modifier.size(100.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(colorResource(id = R.color.error_placeholder_bg))
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_close_24),
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            tint = colorResource(id = R.color.error_close_icon)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FullScreenErrorPreviewLight() {
    val resources = LocalResources.current
    TripPlannerTheme(darkTheme = false) {
        Surface(color = TripPlannerTheme.colors.background) {
            FullScreenError(
                titleText = resources.getString(R.string.error_unknown_title),
                messageText = resources.getString(R.string.error_unknown_message),
                onPrimaryActionClick = {},
                primaryActionText = resources.getString(R.string.error_unknown_primary_button_title),
                onSecondaryActionClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FullScreenErrorPreviewDark() {
    val resources = LocalResources.current
    TripPlannerTheme(darkTheme = true) {
        Surface(color = TripPlannerTheme.colors.background) {
            FullScreenError(
                titleText = resources.getString(R.string.error_unknown_title),
                messageText = resources.getString(R.string.error_unknown_message),
                onPrimaryActionClick = {},
                primaryActionText = resources.getString(R.string.error_unknown_primary_button_title),
                onSecondaryActionClick = {}
            )
        }
    }
}
