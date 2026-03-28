package com.charan.habitdiary.presentation.navigation

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldDefaults
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldDestinationItem
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.calculateThreePaneScaffoldValue
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldPredictiveBackHandler
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND

import com.charan.habitdiary.presentation.navigation.ListDetailScene.Companion.DETAIL_KEY
import com.charan.habitdiary.presentation.navigation.ListDetailScene.Companion.LIST_KEY


class ListDetailScene<T : Any>(
    override val key: Any,
    override val previousEntries: List<NavEntry<T>>,
    val listEntry: NavEntry<T>,
    val detailEntry: NavEntry<T>?,
) : Scene<T> {
    override val entries: List<NavEntry<T>> = listOfNotNull(listEntry, detailEntry)
    override val content: @Composable (() -> Unit) = {
        val isTwoPaneVisible = detailEntry != null

        CompositionLocalProvider(
            LocalTwoPaneVisibility provides isTwoPaneVisible
        ) {
            Row(modifier = Modifier.fillMaxSize()) {

                Column(
                    modifier = if (detailEntry != null) {
                        Modifier.weight(0.4f).animateContentSize()
                    } else {
                        Modifier
                    }
                ) {
                    listEntry.Content()
                }

                if (detailEntry != null) {
                    Column(
                        modifier = Modifier
                            .weight(0.6f)
                            .animateContentSize()
                    ) {
                        AnimatedContent(
                            targetState = detailEntry,
                            contentKey = { entry -> entry?.contentKey },
                        ) { entry ->
                            entry?.Content()
                        }
                    }
                }

            }
        }
    }

    companion object {
        internal const val LIST_KEY = "ListDetailScene-List"
        internal const val DETAIL_KEY = "ListDetailScene-Detail"


        fun listPane() = mapOf(LIST_KEY to true)


        fun detailPane() = mapOf(DETAIL_KEY to true)
    }
}


val LocalTwoPaneVisibility = compositionLocalOf{ false }

@Composable
fun <T : Any> rememberListDetailSceneStrategy(): ListDetailSceneStrategy<T> {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    return remember(windowSizeClass) {
        ListDetailSceneStrategy(windowSizeClass)
    }
}



class ListDetailSceneStrategy<T : Any>(val windowSizeClass: WindowSizeClass) : SceneStrategy<T> {

    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {

        if (!windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)) {
            return null
        }

        val lastEntry = entries.lastOrNull() ?: return null
        if (!lastEntry.metadata.containsKey(DETAIL_KEY) &&
            !lastEntry.metadata.containsKey(LIST_KEY)
        ) {
            return null
        }
        val detailEntry = lastEntry.takeIf { it.metadata.containsKey(DETAIL_KEY) }
        val listEntry = entries.findLast { it.metadata.containsKey(LIST_KEY) } ?: return null


        val sceneKey = listEntry.contentKey

        return ListDetailScene(
            key = sceneKey,
            previousEntries = entries.dropLast(1),
            listEntry = listEntry,
            detailEntry = detailEntry
        )
    }
}

