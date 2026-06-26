package com.charan.habitdiary.presentation.diary.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.charan.habitdiary.presentation.common.components.CalendarDayItem
import com.charan.habitdiary.presentation.common.components.CalendarHeaderItem
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.weekcalendar.WeekCalendarState
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month

@Composable
fun CustomWeekCalendar(
    calendarState : WeekCalendarState,
    selectedDate : LocalDate,
    onClick : (LocalDate) -> Unit,
    currentDate : LocalDate,
    visibleMonth : Month,
    datesWithLogs : Set<LocalDate>,
    showWeekHeader: Boolean = true,
    contentPadding : PaddingValues = PaddingValues(0.dp),
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        calendarState.animateScrollToWeek(selectedDate)
    }
    WeekCalendar(
        modifier = modifier,
        state = calendarState,
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
            )
        },
        contentPadding = contentPadding
    )
}
