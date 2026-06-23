package com.charan.habitdiary.presentation.habits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charan.habitdiary.data.local.model.HabitWithDone
import com.charan.habitdiary.data.model.enums.HabitSortType
import com.charan.habitdiary.data.repository.DataStoreRepository
import com.charan.habitdiary.data.repository.DiaryRepository
import com.charan.habitdiary.data.repository.HabitRepository
import com.charan.habitdiary.presentation.habits.HabitEffect.*
import com.charan.habitdiary.presentation.common.model.ToastMessage
import com.charan.habitdiary.core.utils.DateUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HabitViewModel @Inject constructor(
    private val habitRepository: HabitRepository,
    private val diaryRepository: DiaryRepository,
    private val dataStoreRepo : DataStoreRepository
): ViewModel() {
    private val _state = MutableStateFlow(HabitState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<HabitEffect>()
    val effect  = _effect.asSharedFlow()
    init {
        getHabits()
        //getDailyLogs()
        _state.update {
            it.copy(
                todayDate = DateUtil.getTodayDayAndDate()
            )
        }
        observeIs24HourFormat()
        observeHabitSortType()

    }

    fun onEvent(event : HabitEvent){
        when(event){
            is HabitEvent.OnFabExpandToggle -> {
                _state.value = state.value.copy(
                    isFabExpanded = !state.value.isFabExpanded
                )
            }

            HabitEvent.OnAddDailyLogClick -> {
                sendEffect(OnNavigateToAddDailyLogScreen(null))

            }

            HabitEvent.OnAddHabitClick ->{
                sendEffect(OnNavigateToAddHabitScreen(null))

            }

            is HabitEvent.OnHabitCheckToggle -> {
                onAddHabitClick(event.habit,event.isChecked)

            }

            is HabitEvent.OnDailyLogEdit -> {
                sendEffect(OnNavigateToAddDailyLogScreen(event.id))
            }

            is HabitEvent.OnHabitStatsScreen -> {
                sendEffect(OnNavigateToHabitStatsScreen(event.id))
            }

            is HabitEvent.OnSortTypeChange -> {
                handleSortTypeChange(event.sortType)
            }

            HabitEvent.OnSortDropDownToggle -> {
                toggleSortDropDown()
            }
        }
    }

    private fun toggleSortDropDown() {
        _state.update {
            it.copy(
                isSortDropDownExpanded  = !it.isSortDropDownExpanded
            )
        }
    }

    private fun handleSortTypeChange(sortType : HabitSortType) = viewModelScope.launch {
        dataStoreRepo.setHabitSortType(sortType)
        toggleSortDropDown()

    }
    fun observeHabits(sortType: HabitSortType): Flow<List<HabitWithDone>> {
        return when (sortType) {
            HabitSortType.ALL_HABITS -> {
                habitRepository.getActiveHabits()
                    .onEach { result -> result.onFailure { error -> sendEffect(HabitEffect.ShowToast(ToastMessage.Text(error.message ?: "Failed to observe habits"))) } }
                    .map { it.getOrNull() ?: emptyList() }
            }
            HabitSortType.TODAY_HABITS -> {
                habitRepository.getTodayHabits()
                    .onEach { result -> result.onFailure { error -> sendEffect(HabitEffect.ShowToast(ToastMessage.Text(error.message ?: "Failed to observe habits"))) } }
                    .map { it.getOrNull() ?: emptyList() }
            }
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getHabits() = viewModelScope.launch {
        combine(
            _state.map { it.habitSortType }.distinctUntilChanged().flatMapLatest {
                observeHabits(it)
            },
            _state.map { it.is24HourFormat }.distinctUntilChanged()
        ) { habits, is24Hours ->
            habits.toHabitUIState(is24Hours)
        }.collectLatest { habitsUIState ->
            _state.update {
                it.copy(habits = habitsUIState)
            }
        }
    }




    private fun sendEffect(effect : HabitEffect) = viewModelScope.launch {
        _effect.emit(effect)
    }

    private fun onAddHabitClick(habitUI : HabitItemUIModel,isChecked : Boolean) = viewModelScope.launch {
        if (isChecked) {
            diaryRepository.upsertDailyLog(habitUI.toDailyLogEntity(DateUtil.getCurrentDateTime()))
                .onFailure { error -> _effect.emit(HabitEffect.ShowToast(ToastMessage.Text(error.message ?: "Error logging habit"))) }
        } else {
            diaryRepository.deleteDailyLog(habitUI.logId ?: return@launch)
                .onFailure { error -> _effect.emit(HabitEffect.ShowToast(ToastMessage.Text(error.message ?: "Error removing log"))) }
        }
    }

    private fun observeHabitSortType() = viewModelScope.launch {
        dataStoreRepo.habitSortType.collectLatest { sortType ->
            _state.update {
                it.copy(
                    habitSortType = sortType
                )
            }
        }
    }
    private fun observeIs24HourFormat() = viewModelScope.launch {
        dataStoreRepo.getIs24HourFormat.collectLatest { is24HourFormat ->
            _state.update {
                it.copy(
                    is24HourFormat = is24HourFormat
                )
            }
        }
    }


}
