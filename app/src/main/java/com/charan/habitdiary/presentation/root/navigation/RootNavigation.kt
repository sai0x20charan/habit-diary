package com.charan.habitdiary.presentation.root.navigation

import android.net.Uri
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.charan.habitdiary.presentation.adddailylog.AddDailyLogScreen
import com.charan.habitdiary.presentation.addhabit.AddHabitScreen
import com.charan.habitdiary.presentation.habitstats.HabitStatsScreen
import com.charan.habitdiary.presentation.mediaviewer.MediaViewerScreen
import com.charan.habitdiary.presentation.onboarding.OnBoardingScreen
import com.charan.habitdiary.presentation.settings.aboutlibraries.AboutLibrariesScreen

@Composable
fun RootNavigation(
    onBoardingCompleted : Boolean = true,
    deepLinkNavKey : List<NavKey>? = null,
    mediaList : List<Uri>? = null
) {
    val backStack = rememberNavBackStack(Destinations.BottomBarNav)
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()
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
        sceneStrategies = listOf(listDetailStrategy),

        entryProvider = { key->
            when(key){
                is Destinations.BottomBarNav -> NavEntry(
                    key,
                    metadata = ListDetailScene.listPane()
                ){
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
                is Destinations.AddHabit -> NavEntry(
                    key,
                    metadata = ListDetailScene.detailPane()
                ){
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
                is Destinations.AddDailyLog -> NavEntry(
                    key,
                    metadata = ListDetailScene.detailPane()
                    ){
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
                is Destinations.LibrariesScreenNav -> NavEntry(
                    key,
                    metadata = ListDetailScene.detailPane()
                    ){
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
                    MediaViewerScreen(
                        allImages = key.allImagePaths,
                        currentImage = key.currentImage,
                        onBack = {
                            backStack.removeLastOrNull()
                        }
                    )
                }

                is Destinations.HabitStatsScreeNav -> NavEntry(
                    key,
                    metadata = ListDetailScene.detailPane()
                    ){
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

