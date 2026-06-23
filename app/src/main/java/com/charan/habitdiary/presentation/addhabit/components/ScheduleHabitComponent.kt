package com.charan.habitdiary.presentation.addhabit.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.charan.habitdiary.core.utils.DateUtil
import com.charan.habitdiary.core.utils.DateUtil.toLocale
import kotlinx.datetime.DayOfWeek
import java.time.format.TextStyle

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Suppress("NonSkippableComposable")
@Composable
fun ScheduleHabitComponent(
    selectedTime: String,
    onTimeClick: () -> Unit,
    selectedDays: List<DayOfWeek>,
    onDayToggle: (DayOfWeek) -> Unit,
    daysInitials: List<DayOfWeek> = DateUtil.getDaysOfWeek()
) {
    SectionContainer(title = stringResource(com.charan.habitdiary.R.string.when_will_you_do_this)) {

        LabelActionListItem(
            label = stringResource(com.charan.habitdiary.R.string.time),
            icon = { Icon(Icons.Rounded.Schedule, contentDescription = null) },
            tailingContent = {
                TextButton(onClick = onTimeClick) {
                    Text(selectedTime)
                }
            }
        )

        Spacer(Modifier.height(20.dp))

        Text(
            stringResource(com.charan.habitdiary.R.string.repeat),
            style = MaterialTheme.typography.bodyLargeEmphasized)
        Spacer(Modifier.height(8.dp))

        SelectDaysItem(
            daysInitials = daysInitials,
            selectedDays = selectedDays,
            onDayToggle = onDayToggle
        )
    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SelectDaysItem(
    daysInitials: List<DayOfWeek>,
    selectedDays: List<DayOfWeek>,
    onDayToggle: (DayOfWeek) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Rounded.Repeat,
            contentDescription = stringResource(com.charan.habitdiary.R.string.repeat_icon)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = stringResource(com.charan.habitdiary.R.string.days))
    }

    Spacer(modifier = Modifier.height(8.dp))

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement
            .spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween, Alignment.CenterHorizontally)

    ) {
        daysInitials.forEachIndexed { index, label ->
            ToggleButton(
                checked = selectedDays.contains(label),
                onCheckedChange = { onDayToggle(label) },
                shapes = when (index) {
                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                    daysInitials.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                }
            ) {
                Text(label.toLocale(TextStyle.NARROW_STANDALONE))
            }
        }
    }
}
