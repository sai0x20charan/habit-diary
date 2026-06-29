package com.charan.habitdiary.presentation.mediaviewer

import com.charan.habitdiary.presentation.common.model.MediaItemUIModel

data class MediaViewerState(
    val isDownloading : Boolean = false,
    val showPermissionDialog : Boolean = false,
    val images : List<MediaItemUIModel> = emptyList(),
    val currentIndex : Int = 0,
)
