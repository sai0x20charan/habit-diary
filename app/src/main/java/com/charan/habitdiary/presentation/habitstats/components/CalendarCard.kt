package com.charan.habitdiary.presentation.habitstats.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.NavigateBefore
import androidx.compose.material.icons.automirrored.rounded.NavigateNext
import androidx.compose.material.icons.rounded.NavigateBefore
import androidx.compose.material.icons.rounded.NavigateNext
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.charan.habitdiary.presentation.common.components.MonthCalendarView
import com.charan.habitdiary.core.utils.DateUtil
import com.charan.habitdiary.core.utils.DateUtil.toLocale
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.rememberCalendarState
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CalendarCard(
    modifier: Modifier = Modifier,
    state: CalendarState,
    currentDate: LocalDate,
    selectedDate: LocalDate,
    onClick: (LocalDate) -> Unit,
    visibleMonth: Month,
    onNextMonthClick : () -> Unit = {},
    onPreviousMonthClick : () -> Unit = {},
    habitDoneDates : Set<LocalDate>
) {
    val interactionSources = remember { List(2) { MutableInteractionSource() } }

    ElevatedCard(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = visibleMonth.toLocale(),
                    style = MaterialTheme.typography.titleMediumEmphasized,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                ButtonGroup(
                    overflowIndicator = {

                    }
                ) {
                    customItem(
                        buttonGroupContent = {
                            FilledTonalIconButton(
                                onClick = onPreviousMonthClick,
                                interactionSource = interactionSources[0],
                                shapes = IconButtonDefaults.shapes(),

                                modifier = Modifier
                                    .size(IconButtonDefaults.largeIconSize)
                                    .animateWidth(interactionSources[0])
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.NavigateBefore,
                                    contentDescription = "Previous month"
                                )
                            }
                        },
                        menuContent = {}
                    )

                    customItem(
                        buttonGroupContent = {
                            FilledTonalIconButton(
                                onClick = onNextMonthClick,
                                interactionSource = interactionSources[1],
                                shapes = IconButtonDefaults.shapes(),
                                modifier = Modifier
                                    .size(IconButtonDefaults.largeIconSize)
                                    .animateWidth(interactionSources[1])
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.NavigateNext,
                                    contentDescription = "Next month"
                                )
                            }
                        },
                        menuContent = {}
                    )
                }
            }

            MonthCalendarView(
                modifier = Modifier.padding(top = 12.dp),
                state = state,
                currentDate = currentDate,
                selectedDate = selectedDate,
                onClick = onClick,
                visibleMonth = visibleMonth,
                datesWithLogs = emptySet(),
                habitDoneDates = habitDoneDates
            )
        }
    }
}


@Preview
@Composable
private fun CalendarCardPreview() {
    CalendarCard(
        state = rememberCalendarState(),
        currentDate = DateUtil.getCurrentDate(),
        selectedDate = DateUtil.getCurrentDate(),
        visibleMonth = DateUtil.getCurrentDate().month,
        onClick = {},
        habitDoneDates = setOf(DateUtil.getCurrentDate())
    )

}