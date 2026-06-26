package com.charan.habitdiary.presentation.onboarding

sealed class OnBoardingEffect {
    data class OnScrollToPage(val page: Int) : OnBoardingEffect()
    object NavigateToHome : OnBoardingEffect()
}