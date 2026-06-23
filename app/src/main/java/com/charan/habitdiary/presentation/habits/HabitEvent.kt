package com.charan.habitdiary.presentation.habits

import com.charan.habitdiary.data.model.enums.HabitSortType

sealed class HabitEvent {
    data object OnFabExpandToggle : HabitEvent()

    data object OnAddHabitClick : HabitEvent()
    data object OnAddDailyLogClick : HabitEvent()

    data class OnHabitStatsScreen(val id : Long) : HabitEvent()

    data class OnHabitCheckToggle(val habit : HabitItemUIModel, val isChecked : Boolean) : HabitEvent()

    data class OnDailyLogEdit(val id : Long) : HabitEvent()

    data class OnSortTypeChange(val sortType : HabitSortType) : HabitEvent()

    data object OnSortDropDownToggle : HabitEvent()


}