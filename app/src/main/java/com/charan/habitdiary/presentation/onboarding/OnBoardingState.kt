package com.charan.habitdiary.presentation.onboarding

import com.charan.habitdiary.R

data class OnBoardingState(
    val currentPage : Int = 0,
    val onBoardingPage : List<OnBoardingPage> = pages
)

private val pages = listOf(
    OnBoardingPage(
        title = R.string.onboarding_welcome_title,
        description = R.string.onboarding_welcome_description,
        imageRes = R.drawable.app_logo
    ),
    OnBoardingPage(
        title = R.string.onboarding_habit_tracking_title,
        description = R.string.onboarding_habit_tracking_description,
        imageRes = R.drawable.habit
    ),
    OnBoardingPage(
        title = R.string.onboarding_daily_log_title,
        description = R.string.onboarding_daily_log_description,
        imageRes = R.drawable.book
    )
)

data class OnBoardingPage(
    val title: Int,
    val description: Int,
    val imageRes: Int
)
