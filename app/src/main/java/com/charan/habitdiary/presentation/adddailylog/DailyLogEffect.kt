package com.charan.habitdiary.presentation.adddailylog

import com.charan.habitdiary.presentation.common.model.ToastMessage


sealed class DailyLogEffect {

    data object OnNavigateBack : DailyLogEffect()

    data object OnOpenMediaPicker : DailyLogEffect()

    data object OnTakePhoto : DailyLogEffect()

    data object OnRequestCameraPermission : DailyLogEffect()

    data object OnTakeVideo : DailyLogEffect()

    data class OnNavigateToHabitScreen(val habitId : Long) : DailyLogEffect()

    data class ShowToast(val message : ToastMessage) : DailyLogEffect()

}