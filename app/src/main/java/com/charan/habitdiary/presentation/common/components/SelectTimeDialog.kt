package com.charan.habitdiary.presentation.common.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.charan.habitdiary.R
import kotlinx.datetime.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectTimeDialog(
    onDismiss : () -> Unit,
    onTimeSelected : (LocalTime) -> Unit,
    selectedTime : LocalTime,
    is24HourFormat : Boolean = false

) {
    val timePickerState = rememberTimePickerState(
        initialHour = selectedTime.hour,
        initialMinute = selectedTime.minute,
        is24Hour = is24HourFormat
    )

    TimePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onTimeSelected(
                    LocalTime(
                        hour = timePickerState.hour,
                        minute = timePickerState.minute
                    )
                )
            }) {
                Text(stringResource(R.string.ok))
            }

        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },

        title = {

        }

    ) {
        MaterialTheme(
            typography = MaterialTheme.typography.copy(
                displayLarge = MaterialTheme.typography.displayLarge.copy(
                    fontSize = MaterialTheme.typography.displayMedium.fontSize
                )
            )
        ) {
            TimePicker(
                state = timePickerState
            )
        }
    }

}