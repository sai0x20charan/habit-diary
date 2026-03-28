package com.charan.habitdiary.presentation.habits
import com.charan.habitdiary.data.model.enums.HabitSortType
import com.charan.habitdiary.presentation.common.model.DailyLogItemUIState
import kotlinx.datetime.DayOfWeek

data class HabitScreenState(
    val habits : List<HabitItemUIState> = emptyList(),
    val dailyLogs : List<DailyLogItemUIState> = emptyList(),
    val isLoading : Boolean = false,
    val isFabExpanded : Boolean = false,
    val todayDate : String = "",
    val is24HourFormat : Boolean = false,
    val isSortDropDownExpanded : Boolean = false,
    val habitSortType : HabitSortType = HabitSortType.ALL_HABITS,
)

data class HabitItemUIState(
    val id : Long,
    val habitName : String,
    val habitDescription : String,
    val habitTime : String,
    val isDone : Boolean = false,
    val logId  : Long?,
    val habitReminderTime : String?,
    val habitFrequency : List<DayOfWeek> = emptyList(),
    val isSelected : Boolean = false,
)


