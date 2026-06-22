package com.charan.habitdiary.presentation.habit_stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charan.habitdiary.data.mapper.toDailyLogEntity
import com.charan.habitdiary.data.repository.DiaryRepository
import com.charan.habitdiary.data.repository.HabitRepository
import com.charan.habitdiary.presentation.common.model.ToastMessage
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.atTime

@HiltViewModel(assistedFactory = HabitStatsViewModel.Factory::class)
class HabitStatsViewModel @AssistedInject constructor(
    @Assisted val habitId : Long,
    private val habitRepository: HabitRepository,
    private val diaryRepository: DiaryRepository
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
        val logId = diaryRepository.getLoggedHabitFromIdForRange(
            habitId = _state.value.habitId,
            startOfDay = _state.value.selectedDate.atTime(LocalTime(0,0)),
            endOfDay = _state.value.selectedDate.atTime(LocalTime(23,59))
        ).onFailure { error ->
            sendEffect(HabitStatsEffect.ShowToast(ToastMessage.Text(error.message ?: "Failed to find existing log")))
        }.getOrNull()
        logId?.let {
            sendEffect(HabitStatsEffect.OnNavigateToAddLogScreen(it.id))
        }
    }

    private fun handleCompleteTask(date : LocalDate) = viewModelScope.launch {
        val habitLogExists = _state.value.datesWithHabitDone.contains(date)
        if (!habitLogExists){
            val habit = habitRepository.getHabitWithId(_state.value.habitId).onFailure { error ->
                sendEffect(HabitStatsEffect.ShowToast(ToastMessage.Text(error.message ?: "Failed to load habit details")))
            }.getOrNull() ?: return@launch
            val createdTime = date.atTime(DateUtil.getCurrentTime())
            diaryRepository.upsetDailyLog(
                habit.toDailyLogEntity(date = createdTime)
            ).onFailure { error ->
                sendEffect(HabitStatsEffect.ShowToast(ToastMessage.Text(error.message ?: "Failed to log habit")))
            }
        } else{
            val existingLog = diaryRepository.getLoggedHabitFromIdForRange(
                habitId = _state.value.habitId,
                startOfDay = date.atTime(LocalTime(0,0)),
                endOfDay = date.atTime(LocalTime(23,59))
            ).onFailure { error ->
                sendEffect(HabitStatsEffect.ShowToast(ToastMessage.Text(error.message ?: "Failed to check existing logs")))
            }.getOrNull()
            existingLog?.let {
                diaryRepository.deleteDailyLog(it.id).onFailure { error ->
                    sendEffect(HabitStatsEffect.ShowToast(ToastMessage.Text(error.message ?: "Failed to delete log")))
                }
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
            habitRepository.getHabitWithIdFlow(habitId)
                .onEach { result -> result.onFailure { error -> sendEffect(HabitStatsEffect.ShowToast(ToastMessage.Text(error.message ?: "Failed to observe habit"))) } }
                .map { it.getOrNull() },
            diaryRepository.getAllLogsWithHabitId(habitId)
                .onEach { result -> result.onFailure { error -> sendEffect(HabitStatsEffect.ShowToast(ToastMessage.Text(error.message ?: "Failed to observe logs"))) } }
                .map { it.getOrNull() ?: emptyList() }
        ) { habit, logs ->
            habit to logs
        }.collectLatest { (habit, logs) ->
            if (habit == null) return@collectLatest

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