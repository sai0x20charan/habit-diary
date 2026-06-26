package com.charan.habitdiary.presentation.habitstats

import com.charan.habitdiary.presentation.common.model.ToastMessage

sealed class HabitStatsEffect {
    data class ShowToast(val message: ToastMessage) : HabitStatsEffect()

    data object OnNavigateBack : HabitStatsEffect()

    data object AnimateToNextMonth : HabitStatsEffect()

    data object AnimateToPreviousMonth : HabitStatsEffect()

    data class OnNavigateToAddLogScreen(val logId : Long) : HabitStatsEffect()

    data class OnNavigateToEditHabitScreen(val habitId : Long) : HabitStatsEffect()


}