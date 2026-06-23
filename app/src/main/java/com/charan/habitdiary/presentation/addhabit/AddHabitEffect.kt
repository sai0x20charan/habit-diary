package com.charan.habitdiary.presentation.addhabit

import com.charan.habitdiary.presentation.common.model.ToastMessage

sealed interface AddHabitEffect {

    data class ShowToast(val message: ToastMessage) : AddHabitEffect

    data class OnNavigateBack(val isHabitDeleted : Boolean = false) : AddHabitEffect

    data object RequestNotificationPermission : AddHabitEffect

}