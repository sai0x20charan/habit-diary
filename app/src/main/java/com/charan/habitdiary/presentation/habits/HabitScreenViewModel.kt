package com.charan.habitdiary.presentation.habits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charan.habitdiary.data.local.model.HabitWithDone
import com.charan.habitdiary.data.model.enums.HabitSortType
import com.charan.habitdiary.data.repository.DataStoreRepository
import com.charan.habitdiary.data.repository.HabitLocalRepository
import com.charan.habitdiary.presentation.habits.HabitScreenEffect.*
import com.charan.habitdiary.presentation.mapper.toDailyLogEntity
import com.charan.habitdiary.presentation.mapper.toHabitUIState
import com.charan.habitdiary.utils.DateUtil
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HabitScreenViewModel @Inject constructor(
    private val habitLocalRepository: HabitLocalRepository,
    private val dataStoreRepo : DataStoreRepository

): ViewModel() {
    private val _state = MutableStateFlow(HabitScreenState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<HabitScreenEffect>()
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

    fun onEvent(event : HabitScreenEvent){
        when(event){
            is HabitScreenEvent.OnFabExpandToggle -> {
                _state.value = state.value.copy(
                    isFabExpanded = !state.value.isFabExpanded
                )
            }

            HabitScreenEvent.OnAddDailyLogClick -> {
                sendEffect(OnNavigateToAddDailyLogScreen(null))

            }

            HabitScreenEvent.OnAddHabitClick ->{
                sendEffect(OnNavigateToAddHabitScreen(null))

            }

            is HabitScreenEvent.OnHabitCheckToggle -> {
                onAddHabitClick(event.habit,event.isChecked)

            }

            is HabitScreenEvent.OnDailyLogEdit -> {
                sendEffect(OnNavigateToAddDailyLogScreen(event.id))
            }

            is HabitScreenEvent.OnHabitStatsScreen -> {
                sendEffect(OnNavigateToHabitStatsScreen(event.id))
            }

            is HabitScreenEvent.OnSortTypeChange -> {
                handleSortTypeChange(event.sortType)
            }

            HabitScreenEvent.OnSortDropDownToggle -> {
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

    private fun handleSortTypeChange(sortType : HabitSortType) = viewModelScope.launch(Dispatchers.IO) {
        dataStoreRepo.setHabitSortType(sortType)
        toggleSortDropDown()

    }
    fun observeHabits(sortType: HabitSortType): Flow<List<HabitWithDone>> {
        return when (sortType) {
            HabitSortType.ALL_HABITS -> {
                habitLocalRepository.getActiveHabits()
            }
            HabitSortType.TODAY_HABITS -> {
                habitLocalRepository.getTodayHabitsFlow()
            }
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getHabits() = viewModelScope.launch(Dispatchers.IO) {
        combine(
            _state.map { it.habitSortType }.flatMapLatest {
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



//    private fun getDailyLogs() = viewModelScope.launch(Dispatchers.IO) {
//        combine(
//            habitLocalRepository.getDailyLogsInRange(),
//            _state.map { it.is24HourFormat }.distinctUntilChanged()
//        ) { logs, is24Hours ->
//            logs.toDailyLogUIStateList(is24Hours)
//        }.collectLatest { dailyLogs ->
//            _state.update { it.copy(dailyLogs = dailyLogs) }
//        }
//    }


    private fun sendEffect(effect : HabitScreenEffect) = viewModelScope.launch {
        _effect.emit(effect)
    }

    private fun onAddHabitClick(habitUI : HabitItemUIState,isChecked : Boolean) = viewModelScope.launch(Dispatchers.IO) {
        if (isChecked) {
            habitLocalRepository.upsetDailyLog(habitUI.toDailyLogEntity(DateUtil.getCurrentDateTime()))
        } else {
            habitLocalRepository.deleteDailyLog(habitUI.logId ?: return@launch)
        }
    }

    private fun observeHabitSortType() = viewModelScope.launch(Dispatchers.IO) {
        dataStoreRepo.habitSortType.collectLatest { sortType ->
            _state.update {
                it.copy(
                    habitSortType = sortType
                )
            }
        }
    }
    private fun observeIs24HourFormat() = viewModelScope.launch(Dispatchers.IO) {
        dataStoreRepo.getIs24HourFormat.collectLatest { is24HourFormat ->
            _state.update {
                it.copy(
                    is24HourFormat = is24HourFormat
                )
            }
        }
    }


}
