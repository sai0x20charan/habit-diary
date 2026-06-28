package com.charan.habitdiary.presentation.allentries

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charan.habitdiary.data.repository.DataStoreRepository
import com.charan.habitdiary.data.repository.DiaryRepository
import com.charan.habitdiary.presentation.common.model.ToastMessage
import com.charan.habitdiary.presentation.diary.toDailyLogUIStateList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import com.charan.habitdiary.core.utils.DateUtil.toFormattedString
import com.charan.habitdiary.data.model.enums.DailyLogSortType
import com.charan.habitdiary.presentation.allentries.AllEntriesEffect.*
import com.charan.habitdiary.presentation.common.model.MediaItemUIModel
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllEntriesViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository,
    private val dataStoreRepo: DataStoreRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AllEntriesState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<AllEntriesEffect>()
    val effect = _effect.asSharedFlow()

    init {
        fetchAllLogs()
    }

    fun onEvent(event: AllEntriesEvent) {
        when (event) {
            is AllEntriesEvent.OnTabSelected -> {
                _state.update { it.copy(selectedTab = event.tab) }
            }
            AllEntriesEvent.OnNavigateBack -> {
                sendEffect(AllEntriesEffect.NavigateBack)
            }
            is AllEntriesEvent.OnEntryClick -> {
                sendEffect(NavigateToDailyLog(event.id))
            }
            is AllEntriesEvent.OnImageClick -> {
                sendEffect(NavigateToImageViewer(event.allImages, event.currentImage))
            }
            AllEntriesEvent.OnSortToggle -> {
                _state.update {
                    it.copy(
                        sortType = if (it.sortType == DailyLogSortType.NEWEST_FIRST) {
                            DailyLogSortType.OLDEST_FIRST
                        } else {
                            DailyLogSortType.NEWEST_FIRST
                        }
                    )
                }
            }

            AllEntriesEvent.OnBackClick -> {
                sendEffect(AllEntriesEffect.NavigateBack)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun fetchAllLogs() = viewModelScope.launch {
        _state.map { it.sortType }
            .distinctUntilChanged()
            .flatMapLatest { sortType ->
                combine(
                    diaryRepository.getAllLogsWithHabit(sortBy = sortType)
                        .onEach { result ->
                            result.onFailure {
                            }
                        }
                        .map { it.getOrNull() ?: emptyList() },
                    dataStoreRepo.getIs24HourFormat.distinctUntilChanged()
                ) { logs, is24Hours ->
                    val grouped = logs.groupBy {
                        it.dailyLogEntity.createdAt.date.toFormattedString()
                    }
                    grouped.mapValues { entry ->
                        entry.value.toDailyLogUIStateList(is24Hours)
                    }
                }
            }.collectLatest { uiMap ->
                val allMediaItems = uiMap.values.flatten().flatMap { log ->
                    log.mediaPaths.map { path ->
                        MediaItemUIModel(mediaPath = path, logId = log.id, logDate = null)
                    }
                }
                _state.update {
                    it.copy(
                        entries = uiMap,
                        allMedia = allMediaItems,
                        isLoading = false
                    )
                }
            }
    }

    private fun sendEffect(effect: AllEntriesEffect) = viewModelScope.launch {
        _effect.emit(effect)
    }
}
