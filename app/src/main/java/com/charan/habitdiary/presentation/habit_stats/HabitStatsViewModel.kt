package com.charan.habitdiary.presentation.habit_stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charan.habitdiary.data.mapper.toDailyLogEntity
import com.charan.habitdiary.data.repository.HabitRepository
import com.charan.habitdiary.utils.DateUtil
import com.charan.habitdiary.utils.getBestHabitStreak
import com.charan.habitdiary.utils.getHabitStreak
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.atTime

@HiltViewModel(assistedFactory = HabitStatsViewModel.Factory::class)
class HabitStatsViewModel @AssistedInject constructor(
    @Assisted val habitId : Long,
    private val habitRepository: HabitRepository

) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(habitId : Long): HabitStatsViewModel
    }

    private val _state = MutableStateFlow(HabitStatsState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<HabitStatsEffect>()
    val effect  = _effect.asSharedFlow()
    init {
        observeHabitStats()
    }

    fun onEvent(event: HabitStatsEvent) {

        when(event){
            is HabitStatsEvent.OnDateSelected -> {
                handleSelectedDate(event.date)
            }
            HabitStatsEvent.OnNextMonthClick -> {
                sendEffect(HabitStatsEffect.AnimateToNextMonth)
            }
            HabitStatsEvent.OnPreviousMonthClick -> {
                sendEffect(HabitStatsEffect.AnimateToPreviousMonth)
            }

            is HabitStatsEvent.OnCompleteTaskClick -> {
                handleCompleteTask(event.date)
            }

            HabitStatsEvent.OnAddLog -> {
                handleAddLog()
            }

            HabitStatsEvent.OnNavigateBackClick -> {
                sendEffect(HabitStatsEffect.OnNavigateBack)
            }

            HabitStatsEvent.OnEditHabitClick -> {
                sendEffect(HabitStatsEffect.OnNavigateToEditHabitScreen(_state.value.habitId))
            }
        }

    }

    private fun handleAddLog() = viewModelScope.launch {
        val logId = habitRepository.getLoggedHabitFromIdForRange(
            habitId = _state.value.habitId,
            startOfDay = _state.value.selectedDate.atTime(LocalTime(0,0)),
            endOfDay = _state.value.selectedDate.atTime(LocalTime(23,59))
        )
        logId?.let {
            sendEffect(HabitStatsEffect.OnNavigateToAddLogScreen(it.id))
        }
    }

    private fun handleCompleteTask(date : LocalDate) = viewModelScope.launch {
        val habitLogExists = _state.value.datesWithHabitDone.contains(date)
        if (!habitLogExists){
            val habit = habitRepository.getHabitWithId(_state.value.habitId)
            val createdTime = date.atTime(DateUtil.getCurrentTime())
            habitRepository.upsetDailyLog(
                habit.toDailyLogEntity(date = createdTime)
            )
        } else{
            val existingLog = habitRepository.getLoggedHabitFromIdForRange(
                habitId = _state.value.habitId,
                startOfDay = date.atTime(LocalTime(0,0)),
                endOfDay = date.atTime(LocalTime(23,59))
            )
            existingLog?.let {
                habitRepository.deleteDailyLog(it.id)
            }
        }
    }

    private fun handleSelectedDate(date : LocalDate){
        _state.update {
            it.copy(
                selectedDate = date
            )
        }

    }

    private fun observeHabitStats() = viewModelScope.launch {
        combine(
            habitRepository.getHabitWithIdFlow(habitId),
            habitRepository.getAllLogsWithHabitId(habitId)
        ) { habit, logs ->
            habit to logs
        }.collectLatest { (habit, logs) ->

            _state.update { state ->
                state.copy(
                    habitId = habitId,
                    habitName = habit.habitName,
                    isHabitDeleted = habit.isDeleted,
                    habitFrequency = habit.habitFrequency,
                    habitDescription = habit.habitDescription,
                    habitTime = habit.habitTime,
                    currentStreak = logs.getHabitStreak(habit.habitFrequency),
                    bestStreak = logs.getBestHabitStreak(habit.habitFrequency),
                    datesWithHabitDone = logs.map { it.createdAt.date }.toSet()
                )
            }
        }
    }


    private fun sendEffect(effect : HabitStatsEffect) = viewModelScope.launch {
        _effect.emit(effect)
    }

}