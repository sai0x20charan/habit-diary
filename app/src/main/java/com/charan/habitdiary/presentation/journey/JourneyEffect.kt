package com.charan.habitdiary.presentation.journey

import com.charan.habitdiary.presentation.common.model.MediaItemUIModel

sealed class JourneyEffect {
    data class NavigateToImageViewer(
        val allImages: List<MediaItemUIModel>,
        val currentImage: MediaItemUIModel
    ) : JourneyEffect()
}