package com.project.tripplanner.features.tripdetails.mapbottomsheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.project.tripplanner.R
import com.project.tripplanner.ui.components.PlannerOutlinedTextField
import com.project.tripplanner.ui.components.text.BodyMedium
import com.project.tripplanner.ui.components.text.Headline3
import com.project.tripplanner.ui.theme.Dimensions
import com.project.tripplanner.ui.theme.TripPlannerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailsLocationSheet(
    sheetState: SheetState,
    locationQuery: String,
    isActionEnabled: Boolean,
    onLocationQueryChange: (String) -> Unit,
    onSaveLocation: () -> Unit,
    onSearchInMaps: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = TripPlannerTheme.colors

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        containerColor = colors.surface,
        contentColor = colors.onSurface
    ) {
        TripDetailsLocationSheetContent(
            modifier = modifier,
            locationQuery = locationQuery,
            onLocationQueryChange = onLocationQueryChange,
            onSaveLocation = onSaveLocation,
            onSearchInMaps = onSearchInMaps,
            isActionEnabled = isActionEnabled
        )
    }
}

@Composable
private fun TripDetailsLocationSheetContent(
    locationQuery: String,
    onLocationQueryChange: (String) -> Unit,
    onSaveLocation: () -> Unit,
    onSearchInMaps: () -> Unit,
    isActionEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = TripPlannerTheme.colors

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimensions.spacingL)
            .padding(bottom = Dimensions.spacingXL)
            .imePadding(),
        verticalArrangement = Arrangement.spacedBy(Dimensions.spacingM)
    ) {
        Headline3(
            text = stringResource(R.string.trip_details_location_sheet_title),
            color = colors.onSurface
        )

        PlannerOutlinedTextField(
            value = locationQuery,
            onValueChange = onLocationQueryChange,
            label = stringResource(R.string.trip_details_location_sheet_label),
            placeholder = stringResource(R.string.trip_details_location_sheet_placeholder)
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = Dimensions.buttonMinHeight),
            onClick = onSaveLocation,
            enabled = isActionEnabled,
            shape = RoundedCornerShape(Dimensions.radiusButton),
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.primary,
                contentColor = colors.onPrimary,
                disabledContainerColor = colors.tertiaryContainer,
                disabledContentColor = colors.onTertiaryContainer
            )
        ) {
            BodyMedium(
                text = stringResource(R.string.trip_details_location_sheet_save),
                color = if (isActionEnabled) colors.onPrimary else colors.onTertiaryContainer
            )
        }

        TextButton(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = Dimensions.buttonMinHeight),
            onClick = onSearchInMaps,
            enabled = isActionEnabled
        ) {
            BodyMedium(
                text = stringResource(R.string.trip_details_location_sheet_search_maps),
                color = if (isActionEnabled) colors.primary else TripPlannerTheme.additionalColors.inactive
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TripDetailsLocationSheetContentPreview() {
    TripPlannerTheme {
        TripDetailsLocationSheetContent(
            modifier = Modifier.padding(Dimensions.spacingL),
            locationQuery = "",
            onLocationQueryChange = {},
            onSaveLocation = {},
            onSearchInMaps = {},
            isActionEnabled = false
        )
    }
}
