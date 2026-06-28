package com.charan.habitdiary.presentation.diary

import com.charan.habitdiary.presentation.common.model.ToastMessage

sealed class DiaryEffect {

    data object ScrollToCurrentDate : DiaryEffect()

    data object ScrollToSelectedDate : DiaryEffect()

    data class OnNavigateToAddDailyLogScreen(val id : Long?) : DiaryEffect()

    data class ShowToast(val message: ToastMessage) : DiaryEffect()

    data object NavigateToAllEntries : DiaryEffect()
}