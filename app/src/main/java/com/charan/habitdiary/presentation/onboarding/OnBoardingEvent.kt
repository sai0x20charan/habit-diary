package com.charan.habitdiary.presentation.onboarding

sealed class OnBoardingEvent {
    object NextPage : OnBoardingEvent()
    object GetStarted : OnBoardingEvent()
    data class PageChanged(val page: Int) : OnBoardingEvent()
}
