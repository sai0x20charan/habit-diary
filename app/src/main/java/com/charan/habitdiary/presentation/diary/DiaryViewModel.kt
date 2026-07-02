package com.charan.habitdiary.presentation.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charan.habitdiary.data.model.enums.DailyLogSortType
import com.charan.habitdiary.data.repository.DataStoreRepository
import com.charan.habitdiary.data.repository.DiaryRepository
import com.charan.habitdiary.presentation.common.model.ToastMessage
import com.charan.habitdiary.presentation.diary.DiaryEffect.*
import com.charan.habitdiary.core.utils.DateUtil.getEndOfDay
import com.charan.habitdiary.core.utils.DateUtil.getStartOfDay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import kotlinx.datetime.LocalDate
import javax.inject.Inject
import kotlin.time.ExperimentalTime
import com.charan.habitdiary.R

@HiltViewModel
class DiaryViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository,
    private val dataStoreRepo: DataStoreRepository
) : ViewModel() {
    private val _state = MutableStateFlow(DiaryState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<DiaryEffect>()
    val effect = _effect.asSharedFlow()
    init {
        fetchDailyLogsForDate()
        getLoggedDatesInRange()
        observerSortType()
    }


    fun onEvent(event: DiaryEvent) {
        when (event) {
            is DiaryEvent.OnDateSelected -> {
                selectDateChange(event.date)
            }

            is DiaryEvent.OnDiaryViewTypeChange -> {
                calendarViewChange(event.viewType)
                sendEffect(ScrollToSelectedDate)
            }

            DiaryEvent.OnScrollToCurrentDate -> {
                scrollToCurrentDate()
            }

            is DiaryEvent.OnNavigateToAddDailyLogScreen -> {
                sendEffect(OnNavigateToAddDailyLogScreen(event.id))
            }

            is DiaryEvent.OnVisibleDateRangeChange -> {
                handleDateRangeChange(event.startDate, event.endDate)


            }
            DiaryEvent.OnSortTypeChange -> {
                handleSortTypeChange()
            }
            DiaryEvent.OnNavigateToAllEntries -> {
                sendEffect(DiaryEffect.NavigateToAllEntries)
            }

        }
    }

    private fun handleSortTypeChange() = viewModelScope.launch{
        val changeSortType = if(_state.value.sortType == DailyLogSortType.NEWEST_FIRST) {
            DailyLogSortType.OLDEST_FIRST
        } else {
            DailyLogSortType.NEWEST_FIRST
        }
        dataStoreRepo.setDailyLogSortType(changeSortType)

    }

    private fun handleDateRangeChange(startDate: LocalDate, endDate: LocalDate) {
        _state.update {
            it.copy(
                visibleStartOfDate = startDate,
                visibleEndOfDate = endDate
            )
        }
    }


    fun selectDateChange(date : LocalDate){
        _state.update {
            it.copy(
                selectedDate = date
            )
        }
    }

    private fun scrollToCurrentDate(){
        _state.update {
            it.copy(
                selectedDate = it.currentDate
            )
        }
        sendEffect(DiaryEffect.ScrollToCurrentDate)
    }

    fun calendarViewChange(viewType : CalendarViewType) = viewModelScope.launch {
        _state.update {
            it.copy(
                selectedCalendarView = viewType
            )
        }

    }

    private fun sendEffect(effect : DiaryEffect) = viewModelScope.launch{
            _effect.emit(effect)
    }

    @OptIn(ExperimentalTime::class, ExperimentalCoroutinesApi::class)
    private fun fetchDailyLogsForDate() = viewModelScope.launch {
        combine(
            _state.map { it.selectedDate }.distinctUntilChanged(),
            _state.map { it.sortType }.distinctUntilChanged(),
            dataStoreRepo.getIs24HourFormat.distinctUntilChanged()
        ) { date, sortType, is24Hours ->
            val start = date.getStartOfDay()
            val end = date.getEndOfDay()
            val logsFlow = diaryRepository.getDailyLogsInRange(start, end, sortType)
                .onEach { result -> result.onFailure { error -> _effect.emit(DiaryEffect.ShowToast(error.message?.let { ToastMessage.Text(it) } ?: ToastMessage.Res(R.string.failed_to_load_logs))) } }
                .map { it.getOrNull() ?: emptyList() }
            logsFlow.map { logs ->
                logs.toDailyLogUIStateList(is24Hours)
            }
        }.flatMapLatest { it }
         .collectLatest { uiList ->
             _state.update { it.copy(dailyLogItem = uiList) }
         }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getLoggedDatesInRange() {
        viewModelScope.launch {
            _state
                .map { it.visibleStartOfDate to it.visibleEndOfDate }
                .distinctUntilChanged()
                .flatMapLatest { range ->
                    diaryRepository.getLoggedDatesInRange(range.first.getStartOfDay(), range.second.getEndOfDay())
                        .onEach { result -> result.onFailure { error -> _effect.emit(DiaryEffect.ShowToast(error.message?.let { ToastMessage.Text(it) } ?: ToastMessage.Res(R.string.failed_to_load_logged_dates))) } }
                        .map { it.getOrNull() ?: emptyList() }
                }
                .collectLatest { dates ->
                    _state.update { it.copy(datesWithLogs = dates.toSet()) }
                }
        }
    }

    private fun observerSortType() = viewModelScope.launch {
        dataStoreRepo.dailyLogSortType.collectLatest { sortType ->
            _state.update {
                it.copy(
                    sortType = sortType
                )
            }
        }
    }


}