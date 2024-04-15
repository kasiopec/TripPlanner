package com.project.tripplanner.ui

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.project.tripplanner.R

object Icons

@Composable
fun Icons.IcBackArrow24(
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    tint: Color = Color.Unspecified
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_arrow_back_24),
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint
    )
}

@Composable
fun Icons.IcError24(
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    tint: Color = Color.Unspecified
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_error_24),
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint
    )
}