package com.charan.habitdiary.presentation.mediaviewer

data class MediaViewerState(
    val isDownloading : Boolean = false,
    val showPermissionDialog : Boolean = false
)
