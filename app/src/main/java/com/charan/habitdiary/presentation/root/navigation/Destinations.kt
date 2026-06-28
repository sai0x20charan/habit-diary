package com.charan.habitdiary.presentation.root.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import com.charan.habitdiary.presentation.common.model.MediaItemUIModel

sealed class BottomBarNavDestinations : NavKey{
    @Serializable
    object Home : BottomBarNavDestinations()

    @Serializable
    object Calender : BottomBarNavDestinations()

    @Serializable
    object Settings : BottomBarNavDestinations()
}

sealed class Destinations : NavKey {

    @Serializable
    data object BottomBarNav : Destinations()

    @Serializable
    data class AddHabit(val id: Long?) : Destinations()

    @Serializable
    data class AddDailyLog(
        val id: Long?,
        val date: LocalDate?,
        val openCaptureImageOnLaunch: Boolean = false,
        val openCaptureVideoOnLaunch: Boolean = false,
        val mediaList: List<String>? = null
    ) : Destinations()

    @Serializable
    data object LibrariesScreenNav : Destinations()

    @Serializable
    data object OnBoardingScreenNav : Destinations()

    @Serializable
    data class ImageViewerScreenNav(
        val allMedia: List<MediaItemUIModel>, 
        val currentMedia: MediaItemUIModel,
        val showLogEntryButton: Boolean = true
    ) : Destinations()

    @Serializable
    data class HabitStatsScreeNav(val habitId: Long) : Destinations()

    @Serializable
    data object AllEntriesScreenNav : Destinations()
}