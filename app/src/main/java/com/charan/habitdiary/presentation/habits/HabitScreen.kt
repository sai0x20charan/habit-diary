package com.charan.habitdiary.presentation.habits

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.platform.LocalContext
import com.charan.habitdiary.R
import com.charan.habitdiary.core.utils.showToast
import com.charan.habitdiary.presentation.common.mapper.toResId
import com.charan.habitdiary.presentation.common.components.CustomDropDown
import com.charan.habitdiary.presentation.common.components.CustomMediumTopBar
import com.charan.habitdiary.presentation.common.components.SectionHeading
import com.charan.habitdiary.presentation.habits.components.EmptyStateItem
import com.charan.habitdiary.presentation.habits.components.HabitItemCard
import com.charan.habitdiary.presentation.habits.components.SortButton
import com.charan.habitdiary.core.utils.DateUtil.toLocale
import kotlinx.coroutines.flow.collectLatest
import java.time.format.TextStyle

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalTextApi::class
)
@Composable
fun HabitScreen(
    onHabitDetails : (id : Long?) -> Unit,
    onAddDailyLog : (id : Long?) -> Unit,
    onHabitStats : (id : Long)-> Unit

    ) {
    val viewModel = hiltViewModel<HabitViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when(effect){

                is HabitEffect.OnNavigateToAddHabitScreen -> {
                    onHabitDetails(effect.id)
                }

                is HabitEffect.OnNavigateToAddDailyLogScreen -> {
                    onAddDailyLog(effect.id)
                }

                is HabitEffect.OnNavigateToHabitStatsScreen -> {
                    onHabitStats(effect.habitId)
                }
                is HabitEffect.ShowToast -> {
                    context.showToast(effect.message)
                }
            }
        }
    }
    Scaffold(
        topBar = {
            CustomMediumTopBar(
                title = stringResource(R.string.today),
                subTitle = state.todayDate,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.onEvent(HabitEvent.OnAddHabitClick)
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = stringResource(R.string.add_habit)
                )
            }
        }
    ) { innerPadding->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            item {
                SortButton(
                    onClick = {
                        viewModel.onEvent(HabitEvent.OnSortDropDownToggle)
                    },
                    onSortSelected = {
                        viewModel.onEvent(HabitEvent.OnSortTypeChange(it))
                    },
                    selectedSortTypeRes = state.habitSortType.toResId(),
                    isExpanded = state.isSortDropDownExpanded,
                )
            }

            if (state.habits.isEmpty()) {
                item {
                    EmptyStateItem(
                        text = stringResource(R.string.no_habits_for_today),
                        modifier = Modifier.fillParentMaxSize()
                    )
                }
            }

            items(state.habits.size){
                val habit = state.habits[it]
                HabitItemCard(
                 title = habit.habitName,
                    description = habit.habitDescription,
                    onCompletedChange = { checked->
                        viewModel.onEvent(
                            HabitEvent.OnHabitCheckToggle(
                                habit = habit,
                                isChecked = checked
                            )
                        )
                    },
                    isCompleted = habit.isDone,
                    onClick = {
                        viewModel.onEvent(
                            HabitEvent.OnHabitStatsScreen(
                                habit.id
                            )
                        )
                    },
                    time = habit.habitTime,
                    reminder = "",
                    habitDays = habit.habitFrequency

                    )
                }

        }

    }
}
