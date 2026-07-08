package com.charan.habitdiary.presentation.allentries

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TonalToggleButton
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.charan.habitdiary.R
import com.charan.habitdiary.data.model.enums.DailyLogSortType
import com.charan.habitdiary.presentation.common.components.CustomMediumTopBar
import com.charan.habitdiary.presentation.common.components.DayLogEntryItem
import com.charan.habitdiary.presentation.common.components.toScreenContentPadding
import com.charan.habitdiary.presentation.common.mapper.toResId
import com.charan.habitdiary.presentation.common.components.SectionHeader
import com.charan.habitdiary.presentation.common.components.LogSortButton
import com.charan.habitdiary.presentation.common.model.MediaItemUIModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AllEntriesScreen(
    onBack: () -> Unit,
    onNavigateToDailyLog: (Long) -> Unit,
    onNavigateToImageViewer: (List<MediaItemUIModel>, MediaItemUIModel, Boolean) -> Unit
) {
    val viewModel = hiltViewModel<AllEntriesViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scroll = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val allImages = state.allMedia

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                AllEntriesEffect.NavigateBack -> onBack()
                is AllEntriesEffect.NavigateToDailyLog -> onNavigateToDailyLog(
                    effect.id
                )

                is AllEntriesEffect.NavigateToImageViewer -> onNavigateToImageViewer(
                    effect.allImages,
                    effect.currentImage,
                    true
                )
            }
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scroll.nestedScrollConnection),
        topBar = {
            CustomMediumTopBar(
                title = stringResource(R.string.all_entries),
                scrollBehavior = scroll,
                showBackButton = true,
                onBackClick = {
                    viewModel.onEvent(AllEntriesEvent.OnBackClick)
                }
            )
        }
    ) { innerPadding ->

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            contentPadding = innerPadding.toScreenContentPadding(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    EntriesTab.entries.forEachIndexed { index, tab ->
                        TonalToggleButton(
                            checked = state.selectedTab == tab,
                            onCheckedChange = {
                                viewModel.onEvent(AllEntriesEvent.OnTabSelected(tab))
                            },
                            shapes = when (index) {
                                0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                EntriesTab.entries.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                            },
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(
                                text = stringResource(tab.titleResId),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    LogSortButton(
                        sortTypeRes = state.sortType.toResId(),
                        icon = when (state.sortType) {
                            DailyLogSortType.NEWEST_FIRST -> R.drawable.new_first_sort
                            DailyLogSortType.OLDEST_FIRST -> R.drawable.old_first_sort
                        },
                        onToggleSort = { viewModel.onEvent(AllEntriesEvent.OnSortToggle) }
                    )
                }
            }

            if (state.isLoading) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        ContainedLoadingIndicator()
                    }
                }
            } else {
                when (state.selectedTab) {
                    EntriesTab.ALL_ENTRIES -> {
                        state.entries.forEach { (dateStr, logs) ->
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                SectionHeader(
                                    title = dateStr,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                            items(items = logs, span = { GridItemSpan(maxLineSpan) }) { log ->
                                DayLogEntryItem(
                                    note = log.logNote,
                                    time = log.createdAt,
                                    mediaPath = log.mediaPaths,
                                    onClick = {
                                        viewModel.onEvent(
                                            AllEntriesEvent.OnEntryClick(
                                                log.id
                                            )
                                        )
                                    },
                                    habitName = log.habitName ?: "",
                                    onImageClick = { imagePath ->
                                        val mediaItems = log.mediaPaths.map { path ->
                                            MediaItemUIModel(mediaPath = path, logId = log.id)
                                        }
                                        viewModel.onEvent(
                                            AllEntriesEvent.OnImageClick(
                                                mediaItems,
                                                MediaItemUIModel(mediaPath = imagePath, logId = log.id)
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }

                    EntriesTab.GALLERY -> {
                        items(allImages) { mediaItem ->
                            AsyncImage(
                                model = mediaItem.mediaPath,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .clip(MaterialTheme.shapes.medium)
                                    .clickable {
                                        viewModel.onEvent(
                                            AllEntriesEvent.OnImageClick(
                                                allImages,
                                                mediaItem
                                            )
                                        )
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}
