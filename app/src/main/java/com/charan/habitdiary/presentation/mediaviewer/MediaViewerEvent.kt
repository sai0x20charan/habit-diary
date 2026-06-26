package com.charan.habitdiary.presentation.mediaviewer

sealed class MediaViewerEvent {
    data class DownloadMedia(val filePath : String) : MediaViewerEvent()

    data class ShareMedia(val filePath : String) : MediaViewerEvent()

    data class ToggleStoragePermissionRationale(val show : Boolean) : MediaViewerEvent()

    data object OpenSettingsForPermission : MediaViewerEvent()
}