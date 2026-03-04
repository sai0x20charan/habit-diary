package com.charan.habitdiary.presentation.navigation

import android.net.Uri
import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.charan.habitdiary.presentation.add_daily_log.AddDailyLogScreen
import com.charan.habitdiary.presentation.add_habit.AddHabitScreen
import com.charan.habitdiary.presentation.habit_stats.HabitStatState
import com.charan.habitdiary.presentation.habit_stats.HabitStatsScreen
import com.charan.habitdiary.presentation.media_viewer.ImageViewerScreen
import com.charan.habitdiary.presentation.on_boarding.OnBoardingScreen
import com.charan.habitdiary.presentation.settings.about_libraries.AboutLibrariesScreen

@Composable
fun RootNavigation(
    onBoardingCompleted : Boolean = true,
    deepLinkNavKey : List<NavKey>? = null,
    mediaList : List<Uri>? = null
) {
    val backStack = rememberNavBackStack(Destinations.BottomBarNav)
    LaunchedEffect(deepLinkNavKey, onBoardingCompleted, mediaList) {
        if (deepLinkNavKey != null) {
            backStack.clear()
            deepLinkNavKey.forEach { backStack.add(it) }
        } else {
            val currentIsOnboarding = backStack.contains(Destinations.OnBoardingScreenNav)
            if (!onBoardingCompleted && !currentIsOnboarding) {
                backStack.clear()
                backStack.add(Destinations.OnBoardingScreenNav)
            }
        }
    }
    NavDisplay(
        backStack = backStack,
        onBack = {
            backStack.removeLastOrNull()
        },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = { key->
            when(key){
                is Destinations.BottomBarNav -> NavEntry(key){
                    BottomBarNavigation(
                        onAddHabitNav = {
                            backStack.add(Destinations.AddHabit(id = it))
                        },
                        onAddDailyLogNav = { id, date->
                            backStack.add(Destinations.AddDailyLog(id = id, date = date))
                        },
                        onNavigateToAboutLibraries = {
                            backStack.add(Destinations.LibrariesScreenNav)
                        },
                        onImageOpen = { allImages, currentImage ->
                            backStack.add(Destinations.ImageViewerScreenNav(allImages,currentImage))
                        },
                        onHabitStats = { habitId ->
                            backStack.add(Destinations.HabitStatsScreeNav(habitId))

                        }

                    )
                }
                is Destinations.AddHabit -> NavEntry(key){
                    AddHabitScreen(
                        onNavigateBack = { isDeleted ->
                            if (isDeleted) {
                                backStack.removeIf { it is Destinations.HabitStatsScreeNav }
                            }
                            backStack.removeLastOrNull()
                        },
                        key.id
                    )
                }
                is Destinations.AddDailyLog -> NavEntry(key){
                    AddDailyLogScreen(
                        onNavigateBack = {
                            backStack.removeLastOrNull()
                        },
                        logId = key.id,
                        onImageOpen = { allImagesPaths, currentImage ->
                            backStack.add(Destinations.ImageViewerScreenNav(
                                allImagesPaths,
                                currentImage
                            ))
                        },
                        onHabitOpen = {
                            backStack.add(Destinations.HabitStatsScreeNav(it))
                        },
                        date = key.date,
                        openImageCaptureOnLaunch = key.openCaptureImageOnLaunch,
                        openVideoRecordingOnLaunch = key.openCaptureVideoOnLaunch,
                        sharedMedia = key.mediaList

                    )
                }
                is Destinations.LibrariesScreenNav -> NavEntry(key){
                    AboutLibrariesScreen(
                        onBack = {
                            backStack.removeLastOrNull()
                        }
                    )
                }

                is Destinations.OnBoardingScreenNav -> NavEntry(key){
                    OnBoardingScreen {
                        backStack.removeLastOrNull()
                        backStack.add(Destinations.BottomBarNav)

                    }
                }

                is Destinations.ImageViewerScreenNav -> NavEntry(key){
                    ImageViewerScreen(
                        allImages = key.allImagePaths,
                        currentImage = key.currentImage,
                        onBack = {
                            backStack.removeLastOrNull()
                        }
                    )
                }

                is Destinations.HabitStatsScreeNav -> NavEntry(key){
                    HabitStatsScreen(
                        habitId = key.habitId,
                        onNavigateBack = {
                            backStack.removeLastOrNull()
                        },
                        onAddLog = {
                            backStack.add(Destinations.AddDailyLog(it, null))
                        },
                        onEditHabit = {
                            backStack.add(Destinations.AddHabit(it))
                        }
                    )
                }
                else -> NavEntry(key) { Text("Unknown route") }
            }

        }
    )
}

