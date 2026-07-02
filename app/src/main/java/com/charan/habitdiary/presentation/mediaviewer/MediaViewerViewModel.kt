package com.charan.habitdiary.presentation.mediaviewer

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charan.habitdiary.R
import com.charan.habitdiary.data.repository.FileRepository
import com.charan.habitdiary.presentation.common.model.ToastMessage
import com.charan.habitdiary.core.utils.PermissionManager
import com.charan.habitdiary.core.utils.isSDK29OrAbove
import com.charan.habitdiary.presentation.common.model.MediaItemUIModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.assisted.AssistedFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel(assistedFactory = MediaViewerViewModel.Factory::class)
class MediaViewerViewModel @AssistedInject constructor(
    @Assisted("allImages") private val allImages: List<MediaItemUIModel>,
    @Assisted("currentImage") private val currentImage: MediaItemUIModel,
    private val fileRepository: FileRepository,
    private val permissionManager: PermissionManager
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("allImages") allImages: List<MediaItemUIModel>,
            @Assisted("currentImage") currentImage: MediaItemUIModel
        ): MediaViewerViewModel

    }
    private val _state = MutableStateFlow(MediaViewerState())
    val state = _state.asStateFlow()
    private val _effect = MutableSharedFlow<MediaViewerEffect>()
    val effect = _effect.asSharedFlow()

    init {
        val currentIndex = allImages.indexOfFirst { it.mediaPath == currentImage.mediaPath }
        _state.update {
            it.copy(
                images = allImages,
                currentIndex = if (currentIndex != -1) currentIndex else 0
            )
        }
    }

    fun onEvent(event : MediaViewerEvent) {
        when(event){
            is MediaViewerEvent.DownloadMedia -> {
                handleMediaDownload(filePath = event.filePath)

            }

            is MediaViewerEvent.ShareMedia -> {
                handleShareMedia(event.filePath)

            }

            is MediaViewerEvent.ToggleStoragePermissionRationale -> {
                handleStoragePermissionRationale(event.show)
            }

            is MediaViewerEvent.OpenSettingsForPermission -> {
                handleOpenSettingsForPermission()
            }

            is MediaViewerEvent.OnIndexChange -> {
                handleIndexChange(event.index)
            }
        }
    }

    private fun handleIndexChange(index: Int) {
        _state.update {
            it.copy(currentIndex = index)
        }
    }

    private fun handleStoragePermissionRationale(show : Boolean) = viewModelScope.launch {
        _state.update {
            it.copy(
                showPermissionDialog = show
            )
        }
    }

    private fun handleOpenSettingsForPermission() = viewModelScope.launch {
        permissionManager.openSettingsPermissionScreen()
    }

    private fun handleShareMedia(filePath : String) = viewModelScope.launch {
        val fileUri = fileRepository.getMediaUri(filePath)
        sendEffect(MediaViewerEffect.ShareMedia(fileUri))

    }

    private fun handleMediaDownload(filePath : String) = viewModelScope.launch {
        if (isSDK29OrAbove()) {
            saveMedia(filePath)

        } else{
            if(permissionManager.isStoragePermissionGranted()){
                saveMedia(filePath)

            } else{
                sendEffect(MediaViewerEffect.RequestStoragePermission)
            }
        }
    }

    private fun saveMedia(filePath: String) = viewModelScope.launch {
        _state.update {
            it.copy(isDownloading = true)
        }
        val result = fileRepository.saveMediaToDownloads(filePath)
        _state.update {
            it.copy(isDownloading = false)
        }
        result.onSuccess {
            sendEffect(MediaViewerEffect.ShowToast(ToastMessage.Res(R.string.saved_to_download)))
        }.onFailure { e ->
            sendEffect(MediaViewerEffect.ShowToast(ToastMessage.Res(R.string.failed_to_save_media)))
        }
    }

    private fun sendEffect(effect : MediaViewerEffect) = viewModelScope.launch {
        _effect.emit(effect)
    }

}