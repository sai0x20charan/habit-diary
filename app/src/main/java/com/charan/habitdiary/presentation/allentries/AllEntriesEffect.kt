package com.charan.habitdiary.presentation.allentries

import kotlinx.datetime.LocalDate
import com.charan.habitdiary.presentation.common.model.MediaItemUIModel

sealed interface AllEntriesEffect {
    data object NavigateBack : AllEntriesEffect
    data class NavigateToDailyLog(val id: Long) : AllEntriesEffect
    data class NavigateToImageViewer(val allImages: List<MediaItemUIModel>, val currentImage: MediaItemUIModel) : AllEntriesEffect
}
