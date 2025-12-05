package com.project.tripplanner.ui.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.size.Dimension
import com.project.tripplanner.R
import com.project.tripplanner.ui.components.text.BodyMedium
import com.project.tripplanner.ui.components.text.BodyRegular
import com.project.tripplanner.ui.components.text.LabelText
import com.project.tripplanner.ui.theme.Dimensions
import com.project.tripplanner.ui.theme.TripPlannerTheme


@Composable
fun TripCoverPicker(
    selectedImageUri: Uri?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = TripPlannerTheme.colors

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimensions.coverPickerHeight)
                .dashedBorder(
                    color = colors.iconMuted,
                    strokeWidth = Dimensions.strokeThin,
                    dashLength = Dimensions.spacingS,
                    gapLength = Dimensions.spacingXS
                )
                .clip(RoundedCornerShape(Dimensions.radiusM))
                .background(colors.primaryContainer.copy(alpha = 0.3f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            if (selectedImageUri != null) {
                SelectedImageContent(
                    imageUri = selectedImageUri,
                    onChangeClick = onClick
                )
            } else {
                EmptyStateContent(onBrowseClick = onClick)
            }
        }
    }
}

@Composable
private fun EmptyStateContent(onBrowseClick: () -> Unit) {
    val colors = TripPlannerTheme.colors

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_image_placeholder_48),
            contentDescription = null,
            modifier = Modifier.size(Dimensions.iconSizeM),
            tint = colors.primary.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(Dimensions.spacingS))

        BodyMedium(
            text = stringResource(R.string.cover_picker_tap_to_add),
            color = colors.primary.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun SelectedImageContent(
    imageUri: Uri,
    onChangeClick: () -> Unit
) {
    val colors = TripPlannerTheme.colors

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = rememberAsyncImagePainter(imageUri),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(Dimensions.radiusM)),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(Dimensions.spacingS)
        ) {
            Button(
                onClick = onChangeClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.surface.copy(alpha = 0.9f),
                    contentColor = colors.onSurface
                ),
                shape = RoundedCornerShape(Dimensions.radiusS)
            ) {
                BodyMedium(
                    text = stringResource(R.string.cover_picker_change),
                    color = colors.onSurface
                )
            }
        }
    }
}

private fun Modifier.dashedBorder(
    color: Color,
    strokeWidth: Dp,
    dashLength: Dp,
    gapLength: Dp
): Modifier = this.drawWithContent {
    drawContent()
    val stroke = Stroke(
        width = strokeWidth.toPx(),
        pathEffect = PathEffect.dashPathEffect(
            floatArrayOf(dashLength.toPx(), gapLength.toPx())
        )
    )
    val radiusPx = Dimensions.radiusM.toPx()
    drawRoundRect(
        color = color,
        style = stroke,
        cornerRadius = CornerRadius(radiusPx, radiusPx)
    )
}


@Preview(showBackground = true)
@Composable
private fun TripCoverPickerEmptyPreview() {
    TripPlannerTheme {
        Column(
            modifier = Modifier
                .padding(Dimensions.spacingL)
                .background(TripPlannerTheme.colors.background)
        ) {
            TripCoverPicker(
                selectedImageUri = null,
                onClick = {}
            )
        }
    }
}
