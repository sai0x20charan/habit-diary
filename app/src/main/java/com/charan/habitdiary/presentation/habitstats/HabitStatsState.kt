package com.charan.habitdiary.presentation.habitstats

import com.kizitonwose.calendar.core.minusYears
import com.kizitonwose.calendar.core.now
import com.kizitonwose.calendar.core.plusMonths
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.YearMonth
import kotlin.time.ExperimentalTime
@OptIn(ExperimentalTime::class)
data class HabitStatsState(
    val habitId : Long = -1,
    val habitName : String = "",
    val habitDescription : String = "",
    val habitFrequency : List<DayOfWeek> = emptyList(),
    val habitTime : LocalTime? = null,
    val isHabitDeleted : Boolean = false,
    val currentStreak : Int = 0,
    val bestStreak : Int = 0,
    val totalCompletions : Int = 0,
    val currentMonth : YearMonth  = YearMonth.now(),
    val startOfMonth : YearMonth = currentMonth.minusYears(100),
    val endOfMonth : YearMonth = currentMonth.plusMonths(1),
    val currentDate : LocalDate = LocalDate.now(),
    val selectedDate : LocalDate = LocalDate.now(),
    val datesWithHabitDone : Set<LocalDate> = emptySet()

)
