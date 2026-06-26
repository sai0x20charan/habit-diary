package com.charan.habitdiary.presentation.habits
import com.charan.habitdiary.data.model.enums.HabitSortType
import com.charan.habitdiary.presentation.common.model.DailyLogItemUIModel
import kotlinx.datetime.DayOfWeek

data class HabitState(
    val habits : List<HabitItemUIModel> = emptyList(),
    val dailyLogs : List<DailyLogItemUIModel> = emptyList(),
    val isLoading : Boolean = false,
    val isFabExpanded : Boolean = false,
    val todayDate : String = "",
    val is24HourFormat : Boolean = false,
    val isSortDropDownExpanded : Boolean = false,
    val habitSortType : HabitSortType = HabitSortType.ALL_HABITS,
)

data class HabitItemUIModel(
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


