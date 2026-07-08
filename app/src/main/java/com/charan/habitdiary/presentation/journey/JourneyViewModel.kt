package com.charan.habitdiary.presentation.journey

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charan.habitdiary.core.utils.DateUtil
import com.charan.habitdiary.core.utils.DateUtil.getOneYearBackRange
import com.charan.habitdiary.core.utils.DateUtil.getSixMonthsBackRange
import com.charan.habitdiary.core.utils.DateUtil.getThreeMonthsBackRange
import com.charan.habitdiary.core.utils.getDiaryStreak
import com.charan.habitdiary.core.utils.getBestDiaryStreak
import com.charan.habitdiary.data.local.entity.DailyLogEntity
import com.charan.habitdiary.data.repository.DiaryRepository
import com.charan.habitdiary.data.repository.HabitRepository
import com.charan.habitdiary.presentation.common.model.MediaItemUIModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import com.charan.habitdiary.R
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JourneyViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository,
    private val habitRepository: HabitRepository
) : ViewModel() {
    private val _state = MutableStateFlow(JourneyState())
    val state = _state.asStateFlow()

    private val _effect = Channel<JourneyEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        getFlashbackMedia()
        observeStats()

    }

    private fun getFlashbackMedia() = viewModelScope.launch {
        val currentDate = DateUtil.getCurrentDate()
        val oneYearBackRange = currentDate.getOneYearBackRange()
        val threeMonthsBackRange = currentDate.getThreeMonthsBackRange()
        val sixMonthsBackRange = currentDate.getSixMonthsBackRange()

        val oneYearLogs = diaryRepository.getDiaryMediaForDateRange(oneYearBackRange.first, oneYearBackRange.second).getOrNull() ?: emptyList()
        val threeMonthsLogs = diaryRepository.getDiaryMediaForDateRange(threeMonthsBackRange.first, threeMonthsBackRange.second).getOrNull() ?: emptyList()
        val sixMonthsLogs = diaryRepository.getDiaryMediaForDateRange(sixMonthsBackRange.first, sixMonthsBackRange.second).getOrNull() ?: emptyList()

        val oneYearBackMedia = oneYearLogs.toFlashbackMedia(R.string.one_year_ago)

        val threeMonthsBackMedia = threeMonthsLogs.toFlashbackMedia(R.string.three_months_ago)

        val sixMonthsBackMedia = sixMonthsLogs.toFlashbackMedia(R.string.six_months_ago)

        val activeFlashbacks = listOf(oneYearBackMedia, sixMonthsBackMedia, threeMonthsBackMedia)
            .filter { it.mediaItems.isNotEmpty() }
        
        val mediaPaths = activeFlashbacks.map { it.mediaItems.first().mediaPath }

        _state.update {
            it.copy(
                flashbackMedia = activeFlashbacks,
                carouselMediaPaths = mediaPaths
            )
        }
    }

    private fun observeStats() = viewModelScope.launch {
        combine(
            diaryRepository.getAllDailyLogsFlow()
                .map { it.getOrNull() ?: emptyList() },
            habitRepository.getAllHabitsFlow()
                .map { it.getOrNull() ?: emptyList() }
        ) { logs, habits ->
            logs to habits
        }.collectLatest { (allLogs, allHabits) ->
            val activeLogs = allLogs.filter { !it.isDeleted }
            val activeHabits = allHabits.filter { !it.isDeleted }

            // 1. Diary Stats
            val diaryStreak = activeLogs.getDiaryStreak()
            val bestDiaryStreak = activeLogs.getBestDiaryStreak()
            val totalMediaCount = diaryRepository.getAllMedia().getOrNull()?.size ?: 0

            // 2. Habits Stats
            val habitLogs = activeLogs.filter { it.habitId != null }
            val activeHabitIds = activeHabits.map { it.id }.toSet()
            val activeHabitLogs = habitLogs.filter { it.habitId in activeHabitIds }

            val habitStreak = activeHabitLogs.getDiaryStreak()
            val bestHabitStreak = activeHabitLogs.getBestDiaryStreak()

            _state.update {
                it.copy(
                    diaryStats = DiaryStats(
                        currentStreak = diaryStreak,
                        bestStreak = bestDiaryStreak,
                        totalLogs = activeLogs.size,
                        totalMedia = totalMediaCount
                    ),
                    habitsStats = HabitsStats(
                        totalHabits = activeHabits.size,
                        totalCompletions = activeHabitLogs.size,
                        currentStreak = habitStreak,
                        bestStreak = bestHabitStreak
                    )
                )
            }
        }
    }



    fun onEvent(event: JourneyEvent) {
        when (event) {
            is JourneyEvent.OnImageClick -> {
                val matchingFlashback = state.value.flashbackMedia.find {
                    it.mediaItems.firstOrNull()?.mediaPath == event.clickedPath
                }
                matchingFlashback?.let {
                    sendEffect(
                        JourneyEffect.NavigateToImageViewer(
                            allImages = it.mediaItems,
                            currentImage = it.mediaItems.first()
                        )
                    )
                }
            }
        }
    }

    private fun sendEffect(effect: JourneyEffect) = viewModelScope.launch {
        _effect.send(effect)
    }
}