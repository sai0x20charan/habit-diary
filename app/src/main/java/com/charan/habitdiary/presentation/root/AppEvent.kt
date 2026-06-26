package com.charan.habitdiary.presentation.root

sealed class AppEvent {

    data class OnAuthResult(val isSuccess : Boolean) : AppEvent()

    data object OnCloseChangeLog : AppEvent()
}