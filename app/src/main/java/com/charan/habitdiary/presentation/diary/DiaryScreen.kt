package com.charan.habitdiary.presentation.diary

import com.charan.habitdiary.presentation.common.components.DayLogEntryItem
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.gestures.detectVerticalDragGestures
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.platform.LocalContext
import com.charan.habitdiary.core.utils.showToast
import androidx.window.core.layout.WindowSizeClass
import com.charan.habitdiary.R
import com.charan.habitdiary.data.model.enums.DailyLogSortType
import com.charan.habitdiary.presentation.common.mapper.toResId
import com.charan.habitdiary.presentation.common.components.CalendarHeaderItem
import com.charan.habitdiary.presentation.common.components.CustomMediumTopBar
import com.charan.habitdiary.presentation.diary.components.CustomWeekCalendar
import com.charan.habitdiary.presentation.common.components.MonthCalendarView
import com.charan.habitdiary.presentation.diary.components.LogSortButton
import com.charan.habitdiary.presentation.root.navigation.LocalTwoPaneVisibility
import com.charan.habitdiary.core.utils.DateUtil.toLocale
import com.kizitonwose.calendar.core.daysOfWeek
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
    val viewModel = hiltViewModel<DiaryViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val scaffoldNavigator = rememberSupportingPaneScaffoldNavigator()
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isDetailPaneVisible = LocalTwoPaneVisibility.current
    val showSidePane = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)
            && !isDetailPaneVisible
    val swipeThresholdPx = with(LocalDensity.current) { 48.dp.toPx() }
    var verticalDragAmount by remember { mutableFloatStateOf(0f) }
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
            DiaryEvent.OnVisibleDateRangeChange(
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
                DiaryEffect.ScrollToCurrentDate -> {
                    when (state.selectedCalendarView) {
                        CalendarViewType.WEEK -> {

                            weekCalendarState.animateScrollToWeek(state.currentDate)
                        }

                        CalendarViewType.MONTH -> {
                            monthCalendarState.animateScrollToMonth(state.currentMonth)
                        }
                    }
                }

                DiaryEffect.ScrollToSelectedDate -> {
                    when (state.selectedCalendarView) {
                        CalendarViewType.WEEK -> {
                            weekCalendarState.animateScrollToWeek(state.selectedDate)
                        }
                        CalendarViewType.MONTH -> {
                            val selectedMonth = kotlinx.datetime.YearMonth(state.selectedDate.year, state.selectedDate.month)
                            monthCalendarState.animateScrollToMonth(selectedMonth)
                        }
                    }
                }

                is DiaryEffect.OnNavigateToAddDailyLogScreen -> {
                    onNavigateToDailyLogScreen(effect.id, state.selectedDate)
                }
                is DiaryEffect.ShowToast -> {
                    context.showToast(effect.message)
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
                        viewModel.onEvent(DiaryEvent.OnScrollToCurrentDate)
                    }
                    CalendarViewToggleButton(
                        selectedView = state.selectedCalendarView,
                        onToggle = {
                            viewModel.onEvent(
                                DiaryEvent.OnDiaryViewTypeChange(
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
                    viewModel.onEvent(DiaryEvent.OnNavigateToAddDailyLogScreen(null))
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

                        Column(
                            modifier = Modifier.pointerInput(state.selectedCalendarView) {
                                detectVerticalDragGestures(
                                    onVerticalDrag = { _, dragAmount ->
                                        verticalDragAmount += dragAmount
                                    },
                                    onDragEnd = {
                                        when {
                                            verticalDragAmount <= -swipeThresholdPx &&
                                                    state.selectedCalendarView != CalendarViewType.WEEK -> {
                                                viewModel.onEvent(
                                                    DiaryEvent.OnDiaryViewTypeChange(
                                                        CalendarViewType.WEEK
                                                    )
                                                )
                                            }

                                            verticalDragAmount >= swipeThresholdPx &&
                                                    state.selectedCalendarView != CalendarViewType.MONTH -> {
                                                viewModel.onEvent(
                                                    DiaryEvent.OnDiaryViewTypeChange(
                                                        CalendarViewType.MONTH
                                                    )
                                                )
                                            }
                                        }
                                        verticalDragAmount = 0f
                                    },
                                    onDragCancel = {
                                        verticalDragAmount = 0f
                                    }
                                )
                            }
                        ) {
                            CalendarHeaderItem(daysOfWeek())
                            AnimatedVisibility(
                                visible = state.selectedCalendarView == CalendarViewType.WEEK,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                CustomWeekCalendar(
                                    calendarState = weekCalendarState,
                                    onClick = { date ->
                                        viewModel.onEvent(DiaryEvent.OnDateSelected(date))
                                    },
                                    currentDate = state.currentDate,
                                    selectedDate = state.selectedDate,
                                    visibleMonth = weekCalendarState.lastVisibleWeek.days.last().date.month,
                                    datesWithLogs = state.datesWithLogs,
                                    showWeekHeader = false
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
                                        viewModel.onEvent(DiaryEvent.OnDateSelected(date))
                                    },
                                    visibleMonth = monthCalendarState.lastVisibleMonth.yearMonth.month,
                                    datesWithLogs = state.datesWithLogs,
                                    showWeekHeader = false
                                )
                            }
                        }

                        if(!showSidePane){
                            DiaryListContent(
                                state = state,
                                onSortToggle = {
                                    viewModel.onEvent(DiaryEvent.OnSortTypeChange)
                                },
                                onItemClick = {
                                    viewModel.onEvent(
                                        DiaryEvent.OnNavigateToAddDailyLogScreen(it)
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
                                 viewModel.onEvent(DiaryEvent.OnSortTypeChange)
                             },
                             onItemClick = {
                                 viewModel.onEvent(
                                     DiaryEvent.OnNavigateToAddDailyLogScreen(it)
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
    state: DiaryState,

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
                    sortTypeRes = state.sortType.toResId(),
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