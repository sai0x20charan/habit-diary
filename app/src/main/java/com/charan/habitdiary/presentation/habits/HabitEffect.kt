package com.charan.habitdiary.presentation.habits

import com.charan.habitdiary.presentation.common.model.ToastMessage

sealed class HabitEffect {
    data class ShowToast(val message: ToastMessage) : HabitEffect()

    data class OnNavigateToAddHabitScreen(val id : Long?) : HabitEffect()

    data class OnNavigateToAddDailyLogScreen(val id : Long?) : HabitEffect()

    data class OnNavigateToHabitStatsScreen(val habitId : Long) : HabitEffect()
}