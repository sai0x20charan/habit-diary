package com.charan.habitdiary.presentation.diary

import com.charan.habitdiary.data.model.enums.DailyLogSortType
import com.charan.habitdiary.presentation.common.model.DailyLogItemUIModel
import com.charan.habitdiary.core.utils.DateUtil
import com.kizitonwose.calendar.core.minusMonths
import com.kizitonwose.calendar.core.minusYears
import com.kizitonwose.calendar.core.now
import com.kizitonwose.calendar.core.plusMonths
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import kotlin.time.ExperimentalTime
@OptIn(ExperimentalTime::class)
data class DiaryState(
    val selectedDate : LocalDate = DateUtil.getCurrentDate(),
    val selectedCalendarView : CalendarViewType = CalendarViewType.WEEK,
    val currentMonth : YearMonth  = YearMonth.now(),
    val startOfMonth : YearMonth = currentMonth.minusYears(100),
    val endOfMonth : YearMonth = currentMonth.plusMonths(1),
    val currentDate : LocalDate = LocalDate.now(),
    val startOfDate : LocalDate = currentDate.minusMonths(100),
    val endOfDate : LocalDate = currentDate.plusMonths(1),
    val dailyLogItem : List<DailyLogItemUIModel> = emptyList(),
    val datesWithLogs: Set<LocalDate> = emptySet(),
    val visibleStartOfDate : LocalDate = startOfDate,
    val visibleEndOfDate : LocalDate = endOfDate,
    val sortType : DailyLogSortType = DailyLogSortType.NEWEST_FIRST,
)



enum class CalendarViewType{
    WEEK,
    MONTH
}
