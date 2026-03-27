package com.charan.habitdiary.presentation.media_viewer

data class MediaViewerState(
    val isDownloading : Boolean = false,
    val showPermissionDialog : Boolean = false
)
