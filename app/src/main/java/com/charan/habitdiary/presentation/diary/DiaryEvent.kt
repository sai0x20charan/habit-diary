package com.charan.habitdiary.presentation.diary

import kotlinx.datetime.LocalDate

sealed class DiaryEvent {

    data class OnDateSelected(val date : LocalDate) : DiaryEvent()

    data class OnDiaryViewTypeChange(val viewType : CalendarViewType) : DiaryEvent()

    data object OnScrollToCurrentDate : DiaryEvent()

    data class OnNavigateToAddDailyLogScreen(val id : Long?) : DiaryEvent()

    data class OnVisibleDateRangeChange(val startDate: LocalDate, val endDate: LocalDate) : DiaryEvent()

    data object OnSortTypeChange : DiaryEvent()

    data object OnNavigateToAllEntries : DiaryEvent()

}