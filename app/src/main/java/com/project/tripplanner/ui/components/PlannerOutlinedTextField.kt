package com.project.tripplanner.ui.components

import android.R
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.tripplanner.ui.theme.Dimensions
import com.project.tripplanner.ui.theme.TripPlannerTheme

@Composable
fun PlannerOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null, // External label support
    placeholder: String = "",
    supportingText: String? = null,
    errorMessage: String? = null,
    isError: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null, // Nullable to allow removal
    singleLine: Boolean = true,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val colors = TripPlannerTheme.colors
    val borderColor = remember(isError, isFocused, enabled) {
        when {
            isError && isFocused -> colors.error
            isError -> colors.error
            isFocused -> colors.onBackground
            !enabled -> Color(0xFFE6E9F0)
            else -> Color(0xFFD5DBE6)
        }
    }

    Column(modifier = modifier) {
        // Label (Optional per patterns.forms: "labels above fields")
        if (label != null) {
            Text(
                text = label,
                style = TripPlannerTheme.typography.bodyMedium,
                color = colors.secondary,
                modifier = Modifier.padding(bottom = 6.dp) //
            )
        }

        // Input Container
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(
                    color = TripPlannerTheme.colors.background,
                    shape = RoundedCornerShape(Dimensions.radiusM) //
                )
                .border(
                    width = 1.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(10.dp)
                ),
            textStyle = TripPlannerTheme.typography.bodyMedium.copy(
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
                    modifier = Modifier.padding(horizontal = 12.dp), //
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = TripPlannerTheme.typography.bodyMedium,
                                color = TripPlannerTheme.additionalColors.inactive
                            )
                        }
                        innerTextField()
                    }

                    // Trailing Icon (Optional)
                    if (trailingIcon != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(contentAlignment = Alignment.Center) {
                            trailingIcon()
                        }
                    }
                }
            }
        )

        // Supporting Text or Error Message
        val feedbackText = if (isError) errorMessage else supportingText
        if (feedbackText != null) {
            Text(
                text = feedbackText,
                style = TripPlannerTheme.typography.bodySmall,
                color = if (isError) TripPlannerTheme.colors.error else TripPlannerTheme.colors.secondary,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun CalmTextFieldPreview() {
    var text by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .background(Color(0xFFF0F1F6)) //
    ) {
        // 1. Standard State with Icon
        PlannerOutlinedTextField(
            label = "Destination",
            value = text,
            onValueChange = { text = it },
            placeholder = "Where to?",
            trailingIcon = {
                // Icon style:
                Icon(
                    painter = painterResource(R.drawable.ic_menu_search),
                    contentDescription = null,
                    tint = Color(0xFF9DA3AE)
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Error State
        PlannerOutlinedTextField(
            label = "Email Address",
            value = "invalid-email",
            onValueChange = { },
            isError = true,
            errorMessage = "Please enter a valid email",
            trailingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_delete),
                    contentDescription = "Error",
                    tint = TripPlannerTheme.colors.error
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 3. No Trailing Icon (Clean state)
        PlannerOutlinedTextField(
            label = "Notes",
            value = text,
            onValueChange = { text = it },
            placeholder = "Add details...",
            trailingIcon = null // Icon removed as requested
        )
    }
}