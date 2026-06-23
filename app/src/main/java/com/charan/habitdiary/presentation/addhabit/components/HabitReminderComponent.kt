package com.charan.habitdiary.presentation.addhabit.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.Watch
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.charan.habitdiary.R

@Composable
fun HabitReminderComponent(
    isReminderEnabled: Boolean,
    onReminderToggle: (Boolean) -> Unit,
    reminderTime: String,
    onSelectReminderTime: () -> Unit
) {
    SectionContainer(title = stringResource(R.string.reminder)) {

        LabelActionListItem(
            label = stringResource(R.string.set_reminder_time),
            icon = { Icon(Icons.Rounded.Alarm, contentDescription = null) },
            tailingContent = {
                Switch(
                    checked = isReminderEnabled,
                    onCheckedChange = onReminderToggle
                )
            }
        )

        if (isReminderEnabled) {
            LabelActionListItem(
                label = stringResource(R.string.remind_me_at),
                icon = { Icon(Icons.Rounded.Watch, contentDescription = null) },
                tailingContent = {
                    TextButton(onClick = onSelectReminderTime) {
                        Text(reminderTime.ifEmpty { stringResource(R.string.select_time) } )
                    }
                },
                modifier = Modifier.padding(start = 12.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HabitReminderComponentPreview() {
    HabitReminderComponent(
        onReminderToggle = {},
        onSelectReminderTime = {},
        isReminderEnabled = true,
        reminderTime = "08:00 AM"
    )
}