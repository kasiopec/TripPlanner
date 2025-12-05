package com.project.tripplanner.features.tripform

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.tripplanner.R
import com.project.tripplanner.ui.components.LargeRoundedButton
import com.project.tripplanner.ui.components.PlannerOutlinedTextField
import com.project.tripplanner.ui.components.TripCoverPicker
import com.project.tripplanner.ui.components.TripDateField
import com.project.tripplanner.ui.components.text.BodyMedium
import com.project.tripplanner.ui.theme.Dimensions
import com.project.tripplanner.ui.theme.TripPlannerTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripFormScreen(
    isEditMode: Boolean = false,
    onBackClick: () -> Unit = {},
    onSaveClick: (destination: String, startDate: LocalDate?, endDate: LocalDate?, isSingleDay: Boolean, notes: String, coverImageUri: Uri?) -> Unit = { _, _, _, _, _, _ -> }
) {
    val colors = TripPlannerTheme.colors
    val scrollState = rememberScrollState()

    var destination by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }
    var isSingleDay by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf("") }
    var coverImageUri by remember { mutableStateOf<Uri?>(null) }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    val dateFormatter = remember { DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM) }

    val effectiveEndDate by remember {
        derivedStateOf {
            if (isSingleDay) startDate else endDate
        }
    }

    val screenTitle = if (isEditMode) {
        stringResource(R.string.trip_form_title_edit)
    } else {
        stringResource(R.string.trip_form_title_new)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = screenTitle,
                        style = TripPlannerTheme.typography.h2,
                        color = colors.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back_24),
                            contentDescription = stringResource(R.string.action_back),
                            modifier = Modifier.size(Dimensions.iconSize),
                            tint = colors.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.background
                )
            )
        },
        containerColor = colors.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = Dimensions.spacingL)
        ) {
            Spacer(modifier = Modifier.height(Dimensions.spacingS))

            PlannerOutlinedTextField(
                value = destination,
                onValueChange = { destination = it },
                label = stringResource(R.string.trip_form_destination_label),
                placeholder = stringResource(R.string.trip_form_destination_placeholder),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Dimensions.spacingL))

            TripDateField(
                value = startDate?.format(dateFormatter) ?: "",
                onClick = { showStartDatePicker = true },
                label = stringResource(R.string.trip_form_start_date_label),
                placeholder = stringResource(R.string.trip_form_start_date_placeholder),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Dimensions.spacingL))

            TripDateField(
                value = effectiveEndDate?.format(dateFormatter) ?: "",
                onClick = { showEndDatePicker = true },
                label = stringResource(R.string.trip_form_end_date_label),
                placeholder = stringResource(R.string.trip_form_end_date_placeholder),
                enabled = !isSingleDay,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Dimensions.spacingL))

            SingleDayToggleRow(
                isSingleDay = isSingleDay,
                onToggleChange = { newValue ->
                    isSingleDay = newValue
                    if (newValue && startDate != null) {
                        endDate = startDate
                    }
                }
            )

            Spacer(modifier = Modifier.height(Dimensions.spacingL))

            PlannerOutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = stringResource(R.string.trip_form_notes_label),
                placeholder = stringResource(R.string.trip_form_notes_placeholder),
                singleLine = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimensions.notesFieldHeight)
            )

            Spacer(modifier = Modifier.height(Dimensions.spacingL))

            TripCoverPicker(
                selectedImageUri = coverImageUri,
                onClick = { },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Dimensions.spacingXL))

            LargeRoundedButton(
                text = stringResource(R.string.trip_form_save_button),
                onClick = {
                    onSaveClick(
                        destination,
                        startDate,
                        effectiveEndDate,
                        isSingleDay,
                        notes,
                        coverImageUri
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Dimensions.spacingXL))
        }
    }

    if (showStartDatePicker) {
        TripDatePickerDialog(
            initialDate = startDate,
            onDismiss = { showStartDatePicker = false },
            onDateSelected = { selectedDate ->
                startDate = selectedDate
                if (isSingleDay) {
                    endDate = selectedDate
                }
                showStartDatePicker = false
            }
        )
    }

    if (showEndDatePicker) {
        TripDatePickerDialog(
            initialDate = endDate,
            onDismiss = { showEndDatePicker = false },
            onDateSelected = { selectedDate ->
                endDate = selectedDate
                showEndDatePicker = false
            }
        )
    }
}

@Composable
private fun SingleDayToggleRow(
    isSingleDay: Boolean,
    onToggleChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = TripPlannerTheme.colors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface, shape = androidx.compose.foundation.shape.RoundedCornerShape(Dimensions.radiusM))
            .padding(horizontal = Dimensions.spacingL, vertical = Dimensions.spacingM),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BodyMedium(
            text = stringResource(R.string.trip_form_single_day_label),
            color = colors.onSurface
        )

        Switch(
            checked = isSingleDay,
            onCheckedChange = onToggleChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = colors.surface,
                checkedTrackColor = colors.primary,
                uncheckedThumbColor = colors.surface,
                uncheckedTrackColor = colors.tertiaryContainer
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TripDatePickerDialog(
    initialDate: LocalDate?,
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    val colors = TripPlannerTheme.colors
    
    val initialMillis = initialDate?.let {
        it.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
    
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialMillis
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        onDateSelected(date)
                    }
                }
            ) {
                Text(
                    text = stringResource(R.string.action_ok),
                    color = colors.primary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.action_cancel),
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
            isEditMode = false,
            onBackClick = {},
            onSaveClick = { _, _, _, _, _, _ -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TripFormScreenEditModePreview() {
    TripPlannerTheme {
        TripFormScreen(
            isEditMode = true,
            onBackClick = {},
            onSaveClick = { _, _, _, _, _, _ -> }
        )
    }
}
