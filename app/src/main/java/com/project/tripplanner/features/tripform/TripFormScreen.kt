package com.project.tripplanner.features.tripform

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.project.tripplanner.R
import com.project.tripplanner.ui.components.LargeRoundedButton
import com.project.tripplanner.ui.components.PlannerOutlinedTextField
import com.project.tripplanner.ui.components.TripCoverPicker
import com.project.tripplanner.ui.components.TripDateField
import com.project.tripplanner.ui.components.text.BodyMedium
import com.project.tripplanner.ui.components.text.Headline2
import com.project.tripplanner.ui.theme.Dimensions
import com.project.tripplanner.ui.theme.TripPlannerTheme
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripFormScreen(
    uiState: TripFormUiState.Form,
    onDestinationChange: (String) -> Unit,
    onStartDateClick: () -> Unit,
    onEndDateClick: () -> Unit,
    onStartDateSelected: (Long) -> Unit,
    onEndDateSelected: (Long) -> Unit,
    onDatePickerDismissed: () -> Unit,
    onSingleDayChange: (Boolean) -> Unit,
    onCoverImageSelected: (Uri) -> Unit,
    onNotesChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = TripPlannerTheme.colors

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onCoverImageSelected(it) }
    }

    val todayMillis = LocalDate.now()
        .atTime(LocalTime.MIDNIGHT)
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()

    if (uiState.showStartDatePicker) {
        TripDatePickerDialog(
            initialSelectedDateMillis = uiState.startDateMillis,
            initialDisplayedMonthMillis = uiState.startDateMillis,
            minDateMillis = todayMillis,
            onDateSelected = onStartDateSelected,
            onDismiss = onDatePickerDismissed
        )
    }

    if (uiState.showEndDatePicker) {
        TripDatePickerDialog(
            initialSelectedDateMillis = uiState.endDateMillis,
            initialDisplayedMonthMillis = uiState.startDateMillis ?: todayMillis,
            minDateMillis = uiState.startDateMillis ?: todayMillis,
            onDateSelected = onEndDateSelected,
            onDismiss = onDatePickerDismissed
        )
    }

    Scaffold(
        modifier = modifier,
        containerColor = colors.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Headline2(
                        text = stringResource(
                            id = if (uiState.isEditMode) R.string.trip_form_title_edit
                            else R.string.trip_form_title_new
                        ),
                        color = colors.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.action_back),
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
                    placeholder = stringResource(id = R.string.trip_form_start_date_placeholder),
                    isError = uiState.startDateError != null,
                    errorMessage = uiState.startDateError,
                    modifier = Modifier.weight(1f)
                )

                TripDateField(
                    label = stringResource(id = R.string.trip_form_end_date_label),
                    value = uiState.endDate,
                    onClick = onEndDateClick,
                    placeholder = stringResource(id = R.string.trip_form_end_date_placeholder),
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
                    .height(Dimensions.notesFieldHeight)
            )

            Column(verticalArrangement = Arrangement.spacedBy(Dimensions.spacingXS)) {
                BodyMedium(
                    text = stringResource(id = R.string.trip_form_cover_label),
                    color = colors.onBackground,
                    fontWeight = FontWeight.Bold
                )
                TripCoverPicker(
                    selectedImageUri = uiState.coverImageUri,
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(Dimensions.spacingM))

            LargeRoundedButton(
                text = stringResource(id = R.string.trip_form_save_button),
                onClick = onSaveClick,
                isEnabled = uiState.isSaveEnabled && !uiState.isSaving,
                modifier = Modifier.padding(bottom = Dimensions.spacingL)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TripDatePickerDialog(
    initialSelectedDateMillis: Long?,
    initialDisplayedMonthMillis: Long?,
    minDateMillis: Long,
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedDateMillis,
        initialDisplayedMonthMillis = initialDisplayedMonthMillis,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= minDateMillis
            }

            override fun isSelectableYear(year: Int): Boolean {
                return year >= LocalDate.now().year
            }
        }
    )
    val colors = TripPlannerTheme.colors

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { onDateSelected(it) }
                }
            ) {
                Text(
                    text = stringResource(id = R.string.action_ok),
                    color = colors.primary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(id = R.string.action_cancel),
                    color = colors.secondary
                )
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Preview(showBackground = true)
@Composable
private fun TripFormScreenPreview() {
    TripPlannerTheme {
        TripFormScreen(
            uiState = TripFormUiState.Form(
                destination = "Paris",
                startDateMillis = 1734652800000,
                endDateMillis = 1735084800000,
                isSingleDay = false
            ),
            onDestinationChange = {},
            onStartDateClick = {},
            onEndDateClick = {},
            onStartDateSelected = {},
            onEndDateSelected = {},
            onDatePickerDismissed = {},
            onSingleDayChange = {},
            onCoverImageSelected = {},
            onNotesChange = {},
            onSaveClick = {},
            onBackClick = {}
        )
    }
}
