package com.charan.habitdiary.presentation.root

import com.charan.habitdiary.presentation.common.model.ToastMessage

sealed class AppEffects {

    data class ShowToast(val message : ToastMessage) : AppEffects()
}