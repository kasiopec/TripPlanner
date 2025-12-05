package com.project.tripplanner.features.tripform


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.tripplanner.R
import com.project.tripplanner.ui.components.LargeRoundedButton
import com.project.tripplanner.ui.components.PlannerOutlinedTextField
import com.project.tripplanner.ui.components.TripCoverPicker
import com.project.tripplanner.ui.components.TripDateField
import com.project.tripplanner.ui.components.text.BodyMedium
import com.project.tripplanner.ui.components.text.Headline2
import com.project.tripplanner.ui.theme.Dimensions
import com.project.tripplanner.ui.theme.TripPlannerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripFormScreen(
    uiState: TripFormUiState,
    onDestinationChange: (String) -> Unit,
    onStartDateClick: () -> Unit,
    onEndDateClick: () -> Unit,
    onSingleDayChange: (Boolean) -> Unit,
    onCoverImageClick: () -> Unit,
    onNotesChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = TripPlannerTheme.colors

    Scaffold(
        modifier = modifier,
        containerColor = colors.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Headline2(
                        text = stringResource(id = R.string.trip_form_title_new),
                        color = colors.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back),
                            tint = colors.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colors.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = Dimensions.spacingL)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Dimensions.spacingL)
        ) {
            Spacer(modifier = Modifier.height(Dimensions.spacingXS))

            PlannerOutlinedTextField(
                label = stringResource(id = R.string.trip_form_destination_label),
                value = uiState.destination,
                onValueChange = onDestinationChange,
                placeholder = stringResource(id = R.string.trip_form_destination_placeholder),
                isError = uiState.destinationError != null,
                errorMessage = uiState.destinationError,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingM)
            ) {
                TripDateField(
                    label = stringResource(id = R.string.trip_form_start_date_label),
                    value = uiState.startDate,
                    onClick = onStartDateClick,
                    placeholder = stringResource(id = R.string.trip_form_date_placeholder),
                    isError = uiState.startDateError != null,
                    errorMessage = uiState.startDateError,
                    modifier = Modifier.weight(1f)
                )

                TripDateField(
                    label = stringResource(id = R.string.trip_form_end_date_label),
                    value = uiState.endDate,
                    onClick = onEndDateClick,
                    placeholder = stringResource(id = R.string.trip_form_date_placeholder),
                    isError = uiState.endDateError != null,
                    errorMessage = uiState.endDateError,
                    enabled = !uiState.isSingleDay,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    BodyMedium(
                        text = stringResource(id = R.string.trip_form_single_day_label),
                        color = colors.onBackground,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Switch(
                    checked = uiState.isSingleDay,
                    onCheckedChange = onSingleDayChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = colors.primary,
                        uncheckedThumbColor = colors.onSurfaceVariant,
                        uncheckedTrackColor = colors.surfaceVariant
                    )
                )
            }

            PlannerOutlinedTextField(
                label = stringResource(id = R.string.trip_form_notes_label),
                value = uiState.notes,
                onValueChange = onNotesChange,
                placeholder = stringResource(id = R.string.trip_form_notes_placeholder),
                singleLine = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                BodyMedium(
                    text = stringResource(id = R.string.trip_form_cover_image_label),
                    color = colors.onBackground,
                    fontWeight = FontWeight.Bold
                )
                TripCoverPicker(
                    selectedImageUri = uiState.coverImageUri,
                    onClick = onCoverImageClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(Dimensions.spacingM))

            LargeRoundedButton(
                text = stringResource(id = R.string.trip_form_save_button),
                onClick = onSaveClick,
                isEnabled = uiState.isSaveEnabled,
                modifier = Modifier.padding(bottom = Dimensions.spacingL)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TripFormScreenPreview() {
    TripPlannerTheme {
        TripFormScreen(
            uiState = TripFormUiState(
                destination = "Paris",
                startDate = "Dec 20, 2024",
                endDate = "Dec 25, 2024",
                isSingleDay = false
            ),
            onDestinationChange = {},
            onStartDateClick = {},
            onEndDateClick = {},
            onSingleDayChange = {},
            onCoverImageClick = {},
            onNotesChange = {},
            onSaveClick = {},
            onBackClick = {}
        )
    }
}
