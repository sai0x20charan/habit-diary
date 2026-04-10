package com.charan.habitdiary.presentation.root

sealed class AppEvents {

    data class OnAuthResult(val isSuccess : Boolean) : AppEvents()

    data object OnCloseChangeLog : AppEvents()
}