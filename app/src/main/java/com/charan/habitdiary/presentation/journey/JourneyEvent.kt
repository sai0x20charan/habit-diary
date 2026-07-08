package com.charan.habitdiary.presentation.journey

sealed class JourneyEvent {
    data class OnImageClick(val clickedPath: String) : JourneyEvent()
}