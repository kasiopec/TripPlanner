package com.project.tripplanner.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.tripplanner.R
import com.project.tripplanner.ui.components.text.BodyMedium
import com.project.tripplanner.ui.components.text.BodyRegular
import com.project.tripplanner.ui.components.text.LabelText
import com.project.tripplanner.ui.theme.Dimensions
import com.project.tripplanner.ui.theme.TripPlannerTheme

@Composable
fun PlannerOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String = "",
    supportingText: String? = null,
    errorMessage: String? = null,
    isError: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val colors = TripPlannerTheme.colors
    val borderColor = when {
        isError && isFocused -> colors.error
        isError -> colors.error
        isFocused -> colors.primaryStrong
        else -> TripPlannerTheme.additionalColors.inactive
    }

    Column(modifier = modifier) {
        if (label != null) {
            LabelText(
                modifier = Modifier.padding(bottom = 6.dp),
                text = label,
                color = if (enabled) colors.onBackground else TripPlannerTheme.additionalColors.inactive,
                fontWeight = FontWeight.W700
            )
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (singleLine) {
                        Modifier.height(Dimensions.textFieldHeight)
                    } else {
                        Modifier.heightIn(min = Dimensions.textFieldHeight)
                    }
                )
                .background(
                    color = TripPlannerTheme.colors.background,
                    shape = RoundedCornerShape(Dimensions.radiusM)
                )
                .border(
                    width = Dimensions.strokeThin,
                    color = borderColor,
                    shape = RoundedCornerShape(Dimensions.radiusM)
                ),
            textStyle = TripPlannerTheme.typography.body.copy(
                color = if (enabled) TripPlannerTheme.colors.onBackground else TripPlannerTheme.colors.secondary
            ),
            singleLine = singleLine,
            enabled = enabled,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            interactionSource = interactionSource,
            cursorBrush = SolidColor(TripPlannerTheme.colors.primary),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier.padding(
                        horizontal = Dimensions.spacingM,
                        vertical = if (singleLine) 0.dp else Dimensions.spacingM
                    ),
                    verticalAlignment = if (singleLine) Alignment.CenterVertically else Alignment.Top
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        if (value.isEmpty()) {
                            BodyMedium(
                                text = placeholder,
                                color = TripPlannerTheme.additionalColors.inactive
                            )
                        }
                        innerTextField()
                    }

                    if (trailingIcon != null) {
                        Spacer(modifier = Modifier.width(Dimensions.spacingS))
                        Box(contentAlignment = Alignment.Center) {
                            trailingIcon()
                        }
                    }
                }
            }
        )


        val feedbackText = if (isError) errorMessage else supportingText
        if (feedbackText != null) {
            BodyRegular(
                modifier = Modifier.padding(top = Dimensions.spacingS, start = Dimensions.spacingS),
                text = feedbackText,
                color = if (isError) TripPlannerTheme.colors.error else TripPlannerTheme.colors.secondary
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun CalmTextFieldPreview() {
    var text by remember { mutableStateOf("") }
    TripPlannerTheme {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .background(Color(0xFFF0F1F6)) //
        ) {
            PlannerOutlinedTextField(
                label = "Destination",
                value = text,
                onValueChange = { text = it },
                placeholder = "Where to?",
                trailingIcon = {
                    // Icon style:
                    Icon(
                        painter = painterResource(R.drawable.ic_hide_pass_32),
                        contentDescription = null,
                        tint = Color(0xFF9DA3AE)
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            PlannerOutlinedTextField(
                label = "Email Address",
                value = "invalid-email",
                onValueChange = { },
                isError = true,
                errorMessage = "Please enter a valid email",
                trailingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_error_24),
                        contentDescription = "Error",
                        tint = TripPlannerTheme.colors.error
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            PlannerOutlinedTextField(
                label = "Notes",
                value = "Random text",
                onValueChange = { text = it },
                placeholder = "Add details...",
                trailingIcon = null
            )

            Spacer(modifier = Modifier.height(16.dp))

            PlannerOutlinedTextField(
                label = "Notes",
                enabled = false,
                value = text,
                onValueChange = { text = it },
                placeholder = "I am disabled",
                trailingIcon = null
            )
        }
    }
}