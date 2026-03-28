package com.charan.habitdiary.presentation.diary

import DayLogEntryItem
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CalendarViewWeek
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import com.charan.habitdiary.R
import com.charan.habitdiary.data.model.enums.DailyLogSortType
import com.charan.habitdiary.presentation.common.components.CustomMediumTopBar
import com.charan.habitdiary.presentation.diary.components.CustomWeekCalendar
import com.charan.habitdiary.presentation.common.components.MonthCalendarView
import com.charan.habitdiary.presentation.diary.components.LogSortButton
import com.charan.habitdiary.presentation.navigation.LocalTwoPaneVisibility
import com.charan.habitdiary.utils.DateUtil.toLocale
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import kotlinx.coroutines.flow.collectLatest
import kotlinx.datetime.LocalDate
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3AdaptiveApi::class
)
@Composable
fun DiaryScreen(
    onNavigateToDailyLogScreen : (id : Long?,date : LocalDate?) -> Unit,
    onImageOpen  : (allImages : List<String>, currentImage : String) -> Unit,
) {
    val viewModel = hiltViewModel<DiaryScreenViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val scaffoldNavigator = rememberSupportingPaneScaffoldNavigator()
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isDetailPaneVisible = LocalTwoPaneVisibility.current
    val showSidePane = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)
            && !isDetailPaneVisible
    val weekCalendarState = rememberWeekCalendarState(
        startDate = state.startOfDate,
        endDate = state.endOfDate,
        firstDayOfWeek = firstDayOfWeekFromLocale(),
    )
    val monthCalendarState = rememberCalendarState(
        startMonth = state.startOfMonth,
        endMonth = state.endOfMonth,
        firstVisibleMonth = state.currentMonth,
        firstDayOfWeek = firstDayOfWeekFromLocale(),
    )
    val currentMonthTitle by remember(
        state.selectedCalendarView,
        weekCalendarState,
        monthCalendarState
    ) {
        derivedStateOf {
            when (state.selectedCalendarView) {
                CalendarViewType.WEEK -> {
                    weekCalendarState
                        .lastVisibleWeek.days.last().date.month
                        .toLocale()
                }

                CalendarViewType.MONTH -> {
                    monthCalendarState
                        .lastVisibleMonth.yearMonth.month
                        .toLocale()
                }
            }
        }
    }
    LaunchedEffect(
        weekCalendarState.firstVisibleWeek,
        monthCalendarState.firstVisibleMonth,
        state.selectedCalendarView
    ) {
        viewModel.onEvent(
            DiaryScreenEvents.OnVisibleDateRangeChange(
                startDate = when (state.selectedCalendarView) {
                    CalendarViewType.WEEK -> weekCalendarState.firstVisibleWeek.days.first().date
                    CalendarViewType.MONTH -> monthCalendarState.firstVisibleMonth.yearMonth.days.first
                },
                endDate = when (state.selectedCalendarView) {
                    CalendarViewType.WEEK -> weekCalendarState.lastVisibleWeek.days.last().date
                    CalendarViewType.MONTH -> monthCalendarState.lastVisibleMonth.yearMonth.days.last
                }
            )
        )
    }



    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                DiaryScreenEffect.ScrollToCurrentDate -> {
                    when (state.selectedCalendarView) {
                        CalendarViewType.WEEK -> {

                            weekCalendarState.animateScrollToWeek(state.currentDate)
                        }

                        CalendarViewType.MONTH -> {
                            monthCalendarState.animateScrollToMonth(state.currentMonth)
                        }
                    }
                }

                is DiaryScreenEffect.OnNavigateToAddDailyLogScreen -> {
                    onNavigateToDailyLogScreen(effect.id, state.selectedDate)
                }

                else -> {}
            }
        }
    }
    Scaffold(
        topBar = {
            CustomMediumTopBar(
                title = currentMonthTitle,
                actions = {
                    ResetCalendarButton {
                        viewModel.onEvent(DiaryScreenEvents.OnScrollToCurrentDate)
                    }
                    CalendarViewToggleButton(
                        selectedView = state.selectedCalendarView,
                        onToggle = {
                            viewModel.onEvent(
                                DiaryScreenEvents.OnDiaryViewTypeChange(
                                    if (state.selectedCalendarView == CalendarViewType.WEEK)
                                        CalendarViewType.MONTH
                                    else
                                        CalendarViewType.WEEK
                                )
                            )
                        }
                    )

                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.onEvent(DiaryScreenEvents.OnNavigateToAddDailyLogScreen(null))
                }
            ) {
                Icon(
                    Icons.Rounded.Add,
                    contentDescription = stringResource(R.string.add_daily_log)
                )
            }
        }
    ) { innerPadding ->
        SupportingPaneScaffold(
            modifier = Modifier.padding(innerPadding),
            directive = scaffoldNavigator.scaffoldDirective,
            value = scaffoldNavigator.scaffoldValue,
            mainPane = {
                AnimatedPane {
                    Column(
                        modifier = Modifier

                    ) {
                        AnimatedVisibility(
                            visible = state.selectedCalendarView == CalendarViewType.WEEK,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            CustomWeekCalendar(
                                calendarState = weekCalendarState,
                                onClick = { date ->
                                    viewModel.onEvent(DiaryScreenEvents.OnDateSelected(date))
                                },
                                currentDate = state.currentDate,
                                selectedDate = state.selectedDate,
                                visibleMonth = weekCalendarState.lastVisibleWeek.days.last().date.month,
                                datesWithLogs = state.datesWithLogs
                            )
                        }
                        AnimatedVisibility(
                            visible = state.selectedCalendarView == CalendarViewType.MONTH,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            MonthCalendarView(
                                state = monthCalendarState,
                                currentDate = state.currentDate,
                                selectedDate = state.selectedDate,
                                onClick = { date ->
                                    viewModel.onEvent(DiaryScreenEvents.OnDateSelected(date))
                                },
                                visibleMonth = monthCalendarState.lastVisibleMonth.yearMonth.month,
                                datesWithLogs = state.datesWithLogs
                            )
                        }

                        if(!showSidePane){
                            DiaryListContent(
                                state = state,
                                onSortToggle = {
                                    viewModel.onEvent(DiaryScreenEvents.OnSortTypeChange)
                                },
                                onItemClick = {
                                    viewModel.onEvent(
                                        DiaryScreenEvents.OnNavigateToAddDailyLogScreen(it)
                                    )
                                },
                                onImageClick = onImageOpen,
                                modifier = Modifier
                                    .padding(16.dp)
                                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                            )

                        }


                    }
                }

            },
            supportingPane = {
                if (showSidePane) {
                    AnimatedPane(
                        modifier = Modifier.preferredWidth(0.5f)
                    ) {
                        DiaryListContent(
                            state = state,
                            onSortToggle = {
                                viewModel.onEvent(DiaryScreenEvents.OnSortTypeChange)
                            },
                            onItemClick = {
                                viewModel.onEvent(
                                    DiaryScreenEvents.OnNavigateToAddDailyLogScreen(it)
                                )
                            },
                            onImageClick = onImageOpen,
                            modifier = Modifier
                                .padding(16.dp)
                                .nestedScroll(scrollBehavior.nestedScrollConnection)
                        )
                    }

                }
            }
        )



    }

}



@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun CalendarViewToggleButton(
    selectedView: CalendarViewType,
    onToggle: () -> Unit
) {
    IconButton(
        onClick = onToggle,
        shapes = IconButtonDefaults.shapes()
    ) {
        Icon(
            imageVector = when (selectedView) {
                CalendarViewType.WEEK -> Icons.Rounded.CalendarViewWeek
                CalendarViewType.MONTH -> Icons.Rounded.CalendarMonth
            },
            contentDescription = "Change Calendar View"
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ResetCalendarButton(
    onResetClick : () ->Unit
){
    IconButton(
        onClick = onResetClick,
        shapes = IconButtonDefaults.shapes()
    ) {
        Icon(
            imageVector = Icons.Rounded.Event,
            contentDescription = "Reset Calendar to Current Date"
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DiaryListContent(
    modifier: Modifier,
    state: DiaryScreenState,

    onSortToggle: () -> Unit,
    onItemClick: (Long) -> Unit,
    onImageClick: (List<String>, String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .then(modifier)
    ) {

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                LogSortButton(
                    sortTypeRes = state.sortType.toLocaleString(),
                    icon = when (state.sortType) {
                        DailyLogSortType.NEWEST_FIRST -> R.drawable.new_first_sort
                        DailyLogSortType.OLDEST_FIRST -> R.drawable.old_first_sort
                    },
                    onToggleSort = onSortToggle
                )
            }
        }

        items(state.dailyLogItem.size) { index ->
            val log = state.dailyLogItem[index]

            DayLogEntryItem(
                note = log.logNote,
                time = log.createdAt,
                mediaPath = log.mediaPaths,
                onClick = { onItemClick(log.id) },
                habitName = log.habitName ?: "",
                onImageClick = { imagePath ->
                    onImageClick(log.mediaPaths, imagePath)
                }
            )
        }
    }
}