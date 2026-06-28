package com.charan.habitdiary.presentation.allentries

import androidx.annotation.StringRes
import com.charan.habitdiary.R
import com.charan.habitdiary.presentation.common.model.DailyLogItemUIModel
import com.charan.habitdiary.data.model.enums.DailyLogSortType

import com.charan.habitdiary.presentation.common.model.MediaItemUIModel

data class AllEntriesState(
    val selectedTab: EntriesTab = EntriesTab.ALL_ENTRIES,
    val entries: Map<String, List<DailyLogItemUIModel>> = emptyMap(),
    val allMedia : List<MediaItemUIModel> = emptyList(),
    val sortType: DailyLogSortType = DailyLogSortType.NEWEST_FIRST,
    val isLoading: Boolean = true
)

enum class EntriesTab(@StringRes val titleResId: Int) {
    ALL_ENTRIES(R.string.all_entries),
    GALLERY(R.string.gallery)
}
