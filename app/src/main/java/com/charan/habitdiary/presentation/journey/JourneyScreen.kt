package com.charan.habitdiary.presentation.journey

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.rounded.Collections
import androidx.compose.material.icons.rounded.ImportContacts
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material.icons.rounded.Star
import androidx.compose.ui.graphics.Color
import com.charan.habitdiary.presentation.habitstats.components.StreakStatCard
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.charan.habitdiary.R
import com.charan.habitdiary.core.utils.DateUtil.toFormattedString
import com.charan.habitdiary.presentation.common.components.CustomCarouselImageItem
import com.charan.habitdiary.presentation.common.components.CustomMediumTopBar
import com.charan.habitdiary.presentation.common.components.SectionHeader
import com.charan.habitdiary.presentation.common.components.toScreenContentPadding
import com.charan.habitdiary.presentation.common.model.MediaItemUIModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JourneyScreen(
    onImageClick: (List<MediaItemUIModel>, MediaItemUIModel, Boolean) -> Unit
) {
    val viewModel = hiltViewModel<JourneyViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val activeFlashbacks = state.flashbackMedia
    val mediaPaths = state.carouselMediaPaths

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is JourneyEffect.NavigateToImageViewer -> {
                    onImageClick(effect.allImages, effect.currentImage, true)
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CustomMediumTopBar(
                title = stringResource(R.string.journey),
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = innerPadding.toScreenContentPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if(activeFlashbacks.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = stringResource(R.string.flashback),
                    )
                }

                item {
                    CustomCarouselImageItem(
                        mediaPaths = mediaPaths,
                        onRemoveClick = {},
                        isEdit = false,
                        onImageOpen = { clickedPath ->
                            viewModel.onEvent(JourneyEvent.OnImageClick(clickedPath))
                        },
                        overlayContent = { index ->
                            val flashback = activeFlashbacks.getOrNull(index)
                            if (flashback != null) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp),
                                    contentAlignment = Alignment.BottomStart
                                ) {
                                    Text(
                                        text = stringResource(id = flashback.titleRes),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier
                                            .background(
                                                color = MaterialTheme.colorScheme.secondaryContainer.copy(
                                                    alpha = 0.85f
                                                ),
                                                shape = MaterialTheme.shapes.small
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    )

                }
            }

            item {
                SectionHeader(
                    title = stringResource(R.string.diary_stats),
                )
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StreakStatCard(
                            modifier = Modifier.weight(1f),
                            title = stringResource(R.string.current),
                            value = state.diaryStats.currentStreak.toString(),
                            icon = Icons.Default.LocalFireDepartment,
                            iconTint = MaterialTheme.colorScheme.primary,
                            unit = stringResource(R.string.days)
                        )

                        StreakStatCard(
                            modifier = Modifier.weight(1f),
                            title = stringResource(R.string.best),
                            value = state.diaryStats.bestStreak.toString(),
                            icon = Icons.Default.EmojiEvents,
                            iconTint = MaterialTheme.colorScheme.tertiary,
                            unit = stringResource(R.string.days)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StreakStatCard(
                            modifier = Modifier.weight(1f),
                            title = stringResource(R.string.total_logs),
                            value = state.diaryStats.totalLogs.toString(),
                            icon = Icons.Rounded.ImportContacts,
                            iconTint = MaterialTheme.colorScheme.secondary,
                            unit = stringResource(R.string.logs_unit)
                        )

                        StreakStatCard(
                            modifier = Modifier.weight(1f),
                            title = stringResource(R.string.total_media),
                            value = state.diaryStats.totalMedia.toString(),
                            icon = Icons.Rounded.Collections,
                            iconTint = MaterialTheme.colorScheme.error,
                            unit = stringResource(R.string.media_unit)
                        )
                    }
                }
            }

            item {
                SectionHeader(
                    title = stringResource(R.string.habit_stats),
                )
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StreakStatCard(
                            modifier = Modifier.weight(1f),
                            title = stringResource(R.string.total_habits),
                            value = state.habitsStats.totalHabits.toString(),
                            icon = Icons.Rounded.Spa,
                            iconTint = Color(0xFF388E3C),
                            unit = ""
                        )

                        StreakStatCard(
                            modifier = Modifier.weight(1f),
                            title = stringResource(R.string.total_completions),
                            value = state.habitsStats.totalCompletions.toString(),
                            icon = Icons.Rounded.CheckCircle,
                            iconTint = Color(0xFF00796B),
                            unit = ""
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StreakStatCard(
                            modifier = Modifier.weight(1f),
                            title = stringResource(R.string.current),
                            value = state.habitsStats.currentStreak.toString(),
                            icon = Icons.Default.LocalFireDepartment,
                            iconTint = Color(0xFFE64A19),
                            unit = stringResource(R.string.days)
                        )

                        StreakStatCard(
                            modifier = Modifier.weight(1f),
                            title = stringResource(R.string.best),
                            value = state.habitsStats.bestStreak.toString(),
                            icon = Icons.Default.EmojiEvents,
                            iconTint = Color(0xFFFFB300),
                            unit = stringResource(R.string.days)
                        )
                    }
                }
            }
        }
    }
}