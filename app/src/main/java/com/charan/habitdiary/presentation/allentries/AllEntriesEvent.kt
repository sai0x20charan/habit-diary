package com.charan.habitdiary.presentation.allentries

import com.charan.habitdiary.presentation.common.model.MediaItemUIModel

sealed interface AllEntriesEvent {
    data class OnTabSelected(val tab: EntriesTab) : AllEntriesEvent
    data object OnNavigateBack : AllEntriesEvent
    data class OnEntryClick(val id: Long) : AllEntriesEvent
    data class OnImageClick(val allImages: List<MediaItemUIModel>, val currentImage: MediaItemUIModel) : AllEntriesEvent
    data object OnSortToggle : AllEntriesEvent

    data object OnBackClick : AllEntriesEvent
}
