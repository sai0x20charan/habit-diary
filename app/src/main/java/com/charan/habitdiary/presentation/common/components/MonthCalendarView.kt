package com.charan.habitdiary.presentation.common.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.yearMonth

@Composable
fun MonthCalendarView(
    modifier: Modifier = Modifier,
    state : CalendarState,
    currentDate : LocalDate,
    selectedDate : LocalDate,
    onClick :(LocalDate) -> Unit,
    visibleMonth : Month,
    datesWithLogs : Set<LocalDate>,
    habitDoneDates : Set<LocalDate> = emptySet(),
    showWeekHeader: Boolean = true,
) {
    LaunchedEffect(Unit) {
        state.animateScrollToMonth(selectedDate.yearMonth)
    }
    HorizontalCalendar(
        modifier = modifier,
        state = state,
        dayContent = {
            CalendarDayItem(
                date = it.date.day.toString(),
                isSelected = selectedDate == it.date,
                isToday = currentDate == it.date,
                onClick = {
                    onClick(it.date)
                },
                isCurrentMonth = it.date.month == visibleMonth,
                hasContent = datesWithLogs.contains(it.date),
                isHabitDone = habitDoneDates.contains(it.date)
            )
        },
        monthHeader = if (showWeekHeader) {
            { CalendarHeaderItem(it.weekDays.first().map { it.date.dayOfWeek }) }
        } else {
            null
        }
    )
}
