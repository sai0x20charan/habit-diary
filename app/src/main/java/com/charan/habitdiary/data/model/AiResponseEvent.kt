package com.charan.habitdiary.data.model

sealed class AiResponseEvent<out T> {
    data class Success(val data: String) : AiResponseEvent<Nothing>()
    data class Error(val exception: String) : AiResponseEvent<Nothing>()
    data class Loading(
        val progress : Float = 0f,
        val total : Long = 0L,
        val current : Long = 0L
    ) : AiResponseEvent<Nothing>()
    data class TextGenerated(val text : String) : AiResponseEvent<Nothing>()
    object NotDetermined : AiResponseEvent<Nothing>()
}
