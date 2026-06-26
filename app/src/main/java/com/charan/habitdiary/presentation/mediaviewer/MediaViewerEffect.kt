package com.charan.habitdiary.presentation.mediaviewer

import android.net.Uri
import com.charan.habitdiary.presentation.common.model.ToastMessage

sealed class MediaViewerEffect {

    data class ShowToast(val message : ToastMessage) : MediaViewerEffect()

    data class ShareMedia(val filePath : Uri) : MediaViewerEffect()

    data object RequestStoragePermission : MediaViewerEffect()
}