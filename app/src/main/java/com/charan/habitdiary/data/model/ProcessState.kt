package com.charan.habitdiary.data.model

sealed class ProcessState<out T> {
    data class Success<out T>(val data: T) : ProcessState<T>()
    data class Error(val exception: String) : ProcessState<Nothing>()
    data class Loading(
        val progress : Float = 0f,
        val total : Long = 0L,
        val current : Long = 0L
    ) : ProcessState<Nothing>()
    object NotDetermined : ProcessState<Nothing>()
}
