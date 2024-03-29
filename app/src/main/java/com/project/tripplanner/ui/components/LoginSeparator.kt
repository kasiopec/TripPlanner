package com.project.tripplanner.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.tripplanner.R
import com.project.tripplanner.ui.components.text.BodyRegular

@Composable
fun LoginSeparator(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Divider(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
        )
        BodyRegular(
            text = context.resources.getString(R.string.login_or_label),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Divider(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
        )
    }
}

@Preview(apiLevel = 29)
@Composable
fun LoginSeparatorPreview() {
    Column {
        LoginSeparator()
    }
}
