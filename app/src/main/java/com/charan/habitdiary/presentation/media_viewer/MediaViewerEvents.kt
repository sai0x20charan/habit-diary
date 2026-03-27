package com.charan.habitdiary.presentation.media_viewer

sealed class MediaViewerEvents {
    data class DownloadMedia(val filePath : String) : MediaViewerEvents()

    data class ShareMedia(val filePath : String) : MediaViewerEvents()

    data class ToggleStoragePermissionRationale(val show : Boolean) : MediaViewerEvents()

    data object OpenSettingsForPermission : MediaViewerEvents()
}