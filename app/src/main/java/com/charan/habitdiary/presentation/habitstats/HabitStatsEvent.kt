package com.charan.habitdiary.presentation.habitstats

import kotlinx.datetime.LocalDate

sealed class HabitStatsEvent {

    data class OnDateSelected(val date : LocalDate) : HabitStatsEvent()

    data object OnNextMonthClick: HabitStatsEvent()

    data object OnPreviousMonthClick : HabitStatsEvent()

    data class OnCompleteTaskClick(val date : LocalDate) : HabitStatsEvent()

    data object OnAddLog : HabitStatsEvent()

    data object OnNavigateBackClick : HabitStatsEvent()

    data object OnEditHabitClick : HabitStatsEvent()



}