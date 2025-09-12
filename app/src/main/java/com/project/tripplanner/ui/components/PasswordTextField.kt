package com.project.tripplanner.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.project.tripplanner.R
import com.project.tripplanner.ui.components.text.BodyRegular

@Composable
fun PasswordTextField(
    modifier: Modifier = Modifier,
    password: () -> String,
    onPasswordTextChanged: (String) -> Unit,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    keyboardOptions: KeyboardOptions,
    labelText: String,
    isError: Boolean = false
) {
    var isPasswordVisible by remember { mutableStateOf(false) }
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth(),
        value = password(),
        onValueChange = { onPasswordTextChanged(it) },
        singleLine = true,
        keyboardOptions = keyboardOptions,
        isError = isError,
        colors = OutlinedTextFieldDefaults.colors(
            errorBorderColor = MaterialTheme.colorScheme.error,
            errorLabelColor = MaterialTheme.colorScheme.error,
        ),
        label = {
            BodyRegular(
                text = labelText,
                color = if (isError) MaterialTheme.colorScheme.error else Color.Unspecified
            )
        },
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val iconId = if (isPasswordVisible) R.drawable.ic_hide_pass_32 else R.drawable.ic_show_pass_32
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = null,
                modifier = Modifier
                    .wrapContentSize()
                    .clip(CircleShape)
                    .clickable(
                        onClick = {
                            isPasswordVisible = when (isPasswordVisible) {
                                true -> false
                                false -> true
                            }
                        }
                    )
            )
            // TODO: Add ripple effect for the icon
//            CompositionLocalProvider(LocalRippleTheme provides CustomRippleTheme(rippleColor = MaterialTheme.colorScheme.outline)) {
//
//            }
        },
        keyboardActions = keyboardActions
    )
}