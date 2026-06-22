package com.charan.habitdiary.presentation.habit_stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.recalculateWindowInsets
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconButtonDefaults.IconButtonWidthOption
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.platform.LocalContext
import com.charan.habitdiary.R
import com.charan.habitdiary.utils.showToast
import com.charan.habitdiary.presentation.common.components.BackButton
import com.charan.habitdiary.presentation.common.components.CustomMediumTopBar
import com.charan.habitdiary.presentation.habit_stats.components.CalendarCard
import com.charan.habitdiary.presentation.habit_stats.components.SelectedDateContentItem
import com.charan.habitdiary.presentation.habit_stats.components.StreakStatCard
import com.charan.habitdiary.presentation.habits.components.HabitItemCard
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.core.minusMonths
import com.kizitonwose.calendar.core.plusMonths

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HabitStatsScreen(
    habitId : Long,
    onAddLog : (habitId : Long)-> Unit,
    onNavigateBack : () ->Unit,
    onEditHabit : (habitId : Long) -> Unit
) {
    val viewModel = hiltViewModel<HabitStatsViewModel, HabitStatsViewModel.Factory>(
        creationCallback = { factory ->
            factory.create(habitId)

        }

    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scaffoldState = rememberBottomSheetScaffoldState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val monthCalendarState = rememberCalendarState(
        startMonth = state.startOfMonth,
        endMonth = state.endOfMonth,
        firstVisibleMonth = state.currentMonth,
        firstDayOfWeek = firstDayOfWeekFromLocale(),
    )

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                HabitStatsEffect.AnimateToNextMonth -> {
                    monthCalendarState.animateScrollToMonth(
                        month = monthCalendarState.lastVisibleMonth.yearMonth.plusMonths(1)
                    )
                }
                HabitStatsEffect.AnimateToPreviousMonth -> {
                    monthCalendarState.animateScrollToMonth(
                        month = monthCalendarState.lastVisibleMonth.yearMonth.minusMonths(1)
                    )
                }
                HabitStatsEffect.OnNavigateBack -> {
                    onNavigateBack()

                }

                is HabitStatsEffect.OnNavigateToAddLogScreen -> {
                    onAddLog(effect.logId)

                }
                is HabitStatsEffect.ShowToast -> {
                    context.showToast(effect.message)
                }

                is HabitStatsEffect.OnNavigateToEditHabitScreen -> {
                    onEditHabit(effect.habitId)
                }
            }
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContainerColor = Color.Transparent,
        sheetShadowElevation = 0.dp,
        sheetTonalElevation = 0.dp,

        sheetContent = {
                SelectedDateContentItem(
                    date = state.selectedDate,
                    isDone = state.datesWithHabitDone.contains(state.selectedDate),
                    hasLog = false,
                    onCompleteHabit = {
                        viewModel.onEvent(HabitStatsEvent.OnCompleteTaskClick(state.selectedDate))
                    },
                    onCreateJournal = {
                        viewModel.onEvent(HabitStatsEvent.OnAddLog)
                    }
                )
        },
        sheetDragHandle = {},

        sheetPeekHeight = 150.dp,
        sheetShape = MaterialTheme.shapes.largeIncreased,
        topBar = {
            CustomMediumTopBar(
                title = state.habitName,
                scrollBehavior = scrollBehavior,
                showBackButton = true,
                onBackClick = {
                    viewModel.onEvent(HabitStatsEvent.OnNavigateBackClick)
                },

                actions = {
                    FilledTonalIconButton(
                        onClick = {
                            viewModel.onEvent(HabitStatsEvent.OnEditHabitClick)
                        },
                        modifier = Modifier.size(
                            IconButtonDefaults.
                            smallContainerSize(IconButtonWidthOption.Wide)
                        ),
                        shapes = IconButtonDefaults.shapes()
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = stringResource(R.string.edit_habit)
                        )
                    }
                }


            )
        }

    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(16.dp).nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = innerPadding
        ) {

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StreakStatCard(
                        modifier = Modifier.weight(1f),
                        title = stringResource(R.string.current),
                        value = state.currentStreak,
                        icon = Icons.Default.LocalFireDepartment,
                        iconTint = MaterialTheme.colorScheme.primary
                    )

                    StreakStatCard(
                        modifier = Modifier.weight(1f),
                        title = stringResource(R.string.best),
                        value = state.bestStreak,
                        icon = Icons.Default.EmojiEvents,
                        iconTint = MaterialTheme.colorScheme.tertiary
                    )
                }

            }

            item {
                CalendarCard(
                    modifier = Modifier.padding(top = 20.dp),
                    state = monthCalendarState,
                    currentDate = state.currentDate,
                    selectedDate = state.selectedDate,
                    onClick = {
                        viewModel.onEvent(HabitStatsEvent.OnDateSelected(it))

                    },
                    onPreviousMonthClick = {
                        viewModel.onEvent(HabitStatsEvent.OnPreviousMonthClick)
                    },
                    onNextMonthClick = {
                        viewModel.onEvent(HabitStatsEvent.OnNextMonthClick)

                    },
                    visibleMonth = monthCalendarState.lastVisibleMonth.yearMonth.month,
                    habitDoneDates = state.datesWithHabitDone
                )
            }
        }


    }

}