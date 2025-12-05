package com.project.tripplanner.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.project.tripplanner.ui.components.text.BodyRegular

@Composable
fun BaseOutlinedTextField(
    modifier: Modifier = Modifier,
    text: () -> String,
    onTextChanged: (String) -> Unit,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    isError: Boolean = false,
    labelText: String,
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = text(),
        onValueChange = { onTextChanged(it) },
        singleLine = true,
        isError = isError,
        label = {
            BodyRegular(
                text = labelText
            )
        },
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions
    )
}

@Composable
fun BaseOutlinedTextFieldPreview() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        BaseOutlinedTextField(
            text = {
                "test"
            },
            onTextChanged = {},
            labelText = "Preview"
        )
    }

}