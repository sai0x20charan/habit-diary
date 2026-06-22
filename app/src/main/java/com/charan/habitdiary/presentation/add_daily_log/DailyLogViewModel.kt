package com.charan.habitdiary.presentation.add_daily_log

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charan.habitdiary.R
import com.charan.habitdiary.data.repository.DataStoreRepository
import com.charan.habitdiary.data.repository.FileRepository
import com.charan.habitdiary.data.repository.HabitRepository
import com.charan.habitdiary.presentation.common.model.ToastMessage
import com.charan.habitdiary.presentation.mapper.toDailyLogEntity
import com.charan.habitdiary.presentation.mapper.toDailyLogItemDetails
import com.charan.habitdiary.presentation.mapper.toDailyLogMediaEntityList
import com.charan.habitdiary.utils.DateUtil
import com.charan.habitdiary.utils.DateUtil.toFormattedString
import com.charan.habitdiary.utils.DateUtil.toLocalDate
import com.charan.habitdiary.utils.PermissionManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

@HiltViewModel(assistedFactory = DailyLogViewModel.Factory::class)
class DailyLogViewModel @AssistedInject constructor(
    @Assisted("logId") val logId: Long?,
    @Assisted("date") val date: LocalDate?,
    @Assisted("openImageCapture") val openImageCaptureOnLaunch: Boolean?,
    @Assisted("openVideoCapture") val openVideoRecordingOnLaunch: Boolean?,
    @Assisted("sharedMediaItems") val sharedMediaItems : List<String>?,
    private val habitRepository: HabitRepository,
    private val fileRepository: FileRepository,
    private val permissionManager: PermissionManager,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("logId") logId: Long?,
            @Assisted("date") date: LocalDate?,
            @Assisted("openImageCapture") openImageCaptureOnLaunch: Boolean?,
            @Assisted("openVideoCapture") openVideoRecordingOnLaunch: Boolean?,
            @Assisted("sharedMediaItems") sharedMediaItems : List<String>?
        ): DailyLogViewModel
    }



    private val _state = MutableStateFlow(DailyLogState())
    val state: StateFlow<DailyLogState> = _state.asStateFlow()

    private val _effect = Channel<DailyLogEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        initializeLog(logId)
        observeDateTimeChanges()
        observeHourFormat()
        if(openImageCaptureOnLaunch == true){
            handleTakePhoto()
        }
        if(openVideoRecordingOnLaunch == true){
            handleTakeVideo()
        }
        if(!(sharedMediaItems.isNullOrEmpty())){
            val sharedUris = sharedMediaItems.map { it.toUri() }
            handleMediaPicked(sharedUris)

        }
    }

    fun onEvent(event: DailyLogEvent) {
        when (event) {
            is DailyLogEvent.OnNotesTextChange -> {
                updateNotes(event.text)
            }
            is DailyLogEvent.OnImagePathChange -> {
                updateImagePath(event.path)
            }
            DailyLogEvent.OnSaveDailyLogClick -> {
                saveDailyLog()
            }
            DailyLogEvent.OnBackClick -> {
                sendEffect(DailyLogEffect.OnNavigateBack)
            }
            is DailyLogEvent.OnToggleImagePickOptionsSheet -> {
                toggleImagePickerSheet(event.isVisible)
            }
            DailyLogEvent.OnPickFromGalleryClick -> {
                sendEffect(DailyLogEffect.OnOpenMediaPicker)
            }
            DailyLogEvent.OnTakePhotoClick -> {
                handleTakePhoto()
            }
            is DailyLogEvent.OnImagePick -> {
                handleMediaPicked(event.uris)
            }
            DailyLogEvent.OnOpenSettingsForPermissions -> {
                permissionManager.openSettingsPermissionScreen()
            }
            is DailyLogEvent.ToggleShowRationaleForCameraPermission -> {
                setCameraRationaleVisible(event.showRationale)
            }
            is DailyLogEvent.OnToggleDateSelectorDialog -> {
                setDatePickerVisible(event.isVisible)
            }
            is DailyLogEvent.OnDateSelected -> {
                updateDate(event.date)
            }
            is DailyLogEvent.OnToggleTimeSelectorDialog -> setTimePickerVisible(event.isVisible)
            is DailyLogEvent.OnTimeSelected -> updateTime(event.time)
            DailyLogEvent.OnDeleteDailyLog -> deleteDailyLog()
            is DailyLogEvent.OnToggleDeleteDialog -> setDeleteDialogVisible(event.showDeleteDialog)
            is DailyLogEvent.OnConfirmMediaItemDelete -> {
                handleDeleteMediaItem(event.confirmDelete)

            }
            is DailyLogEvent.OnSelectMediaItemForDelete -> {
                updateMediaItemDeletion(
                    event.mediaItem!!
                )

            }

            DailyLogEvent.OnCaptureVideoClick -> {
                handleTakeVideo()

            }

            is DailyLogEvent.OnVideoPick -> {

            }

            is DailyLogEvent.OnPermissionResult -> {
                handlePermissionResult(event.isGranted)
            }

            is DailyLogEvent.OnNavigateToHabitScreen ->{
                handleNavigationToHabitScreen()
            }

            DailyLogEvent.OnToggleTextEditingControls -> {
                handleTextEditingControlsToggle()
            }
        }
    }

    private fun handleTextEditingControlsToggle(){
        _state.update {
            it.copy(
                isTextEditingControlsExpanded = !it.isTextEditingControlsExpanded
            )
        }
    }

    private fun handleNavigationToHabitScreen(){
        if(_state.value.isHabitDeleted){
            sendEffect(DailyLogEffect.ShowToast(ToastMessage.Res(R.string.habit_deleted_message)))
        } else {
            val habitId = _state.value.dailyLogItemDetails.habitId ?: return
            sendEffect(DailyLogEffect.OnNavigateToHabitScreen(habitId))
        }
    }

    private fun handlePermissionResult(isGranted: Boolean) {
        val pendingAction = _state.value.pendingCameraAction ?: return
        if (isGranted) {
            when (pendingAction) {
                PendingCameraAction.PHOTO -> {
                    handleTakePhoto()
                }
                PendingCameraAction.VIDEO -> {
                    handleTakeVideo()
                }
            }
        }
        _state.update {
            it.copy(
                pendingCameraAction = null
            )
        }
    }

    private fun handleDeleteMediaItem(confirmDelete: Boolean) {
        val selectedItem = _state.value.selectedMediaItemForDelete ?: return

        if (confirmDelete) {
            val updatedMediaItems = _state.value.dailyLogItemDetails.mediaItems.map { item ->
                if (item == selectedItem) item.copy(isDeleted = true) else item
            }

            _state.update {
                it.copy(
                    dailyLogItemDetails = it.dailyLogItemDetails.copy(
                        mediaItems = updatedMediaItems
                    ),
                    showImageDeleteOption = false,
                    selectedMediaItemForDelete = null
                )
            }
        } else {
            _state.update {
                it.copy(
                    showImageDeleteOption = false,
                    selectedMediaItemForDelete = null
                )
            }
        }
    }



    private fun updateMediaItemDeletion(
        mediaItem : String
    ) {
        _state.update {
            it.copy(
                selectedMediaItemForDelete = it.dailyLogItemDetails.mediaItems.first { item ->
                    item.mediaPath == mediaItem
                },
                showImageDeleteOption = true

            )
        }
    }

    private fun setDeleteDialogVisible(visible: Boolean) {
        _state.update { it.copy(showDeleteDialog = visible) }
    }

    private fun initializeLog(logId: Long?) = viewModelScope.launch {
        if (logId != null) {
            val log = habitRepository.getDailyLogsWithHabitWithId(logId).onFailure { error ->
                sendEffect(DailyLogEffect.ShowToast(ToastMessage.Text(error.message ?: "Failed to load log details")))
            }.getOrNull() ?: return@launch
            _state.update {
                it.copy(
                    dailyLogItemDetails = log.toDailyLogItemDetails(),
                    isEdit = true,
                    isHabitDeleted = log.habitEntity?.isDeleted == true
                )
            }
        } else {
            _state.update {
                it.copy(
                    dailyLogItemDetails = it.dailyLogItemDetails.copy(
                        date = date ?: DateUtil.getCurrentDate(),
                        time = DateUtil.getCurrentTime()
                    )
                )
            }
        }
    }

    private fun updateNotes(text: String) {
        _state.update {
            it.copy(
                dailyLogItemDetails = it.dailyLogItemDetails.copy(
                    notesText = text
                )
            )
        }
    }

    private fun updateImagePath(path: String) {
        _state.update { state ->
            val updatedMediaItems =
                state.dailyLogItemDetails.mediaItems.toMutableList().apply {
                    add(DailyLogMediaItem(
                        mediaPath = path,
                        isPendingSave = true
                    ))
                }

            state.copy(
                dailyLogItemDetails = state.dailyLogItemDetails.copy(
                    mediaItems = updatedMediaItems
                ),
                tempImagePath = "",
                showImagePickOptionsSheet = false
            )
        }
    }


    private fun toggleImagePickerSheet(visible: Boolean) {
        _state.update { it.copy(showImagePickOptionsSheet = visible) }
    }

    private fun setCameraRationaleVisible(visible: Boolean) {
        _state.update { it.copy(showRationaleForCameraPermission = visible) }
    }

    private fun setDatePickerVisible(visible: Boolean) {
        _state.update { it.copy(showDateSelectDialog = visible) }
    }

    private fun setTimePickerVisible(visible: Boolean) {
        _state.update { it.copy(showTimeSelectDialog = visible) }
    }

    private fun updateDate(dateMillis: Long) {
        _state.update {
            it.copy(
                dailyLogItemDetails = it.dailyLogItemDetails.copy(date = dateMillis.toLocalDate()),
                showDateSelectDialog = false
            )
        }
    }

    private fun updateTime(timeMillis: LocalTime) {
        _state.update {
            it.copy(
                dailyLogItemDetails = it.dailyLogItemDetails.copy(time = timeMillis),
                showTimeSelectDialog = false
            )
        }
    }

    private fun saveDailyLog() = viewModelScope.launch {
        try {
            setLoading(true)
            saveImagesToFileDir()
            habitRepository.upsetDailyLog(
                dailyLog = _state.value.toDailyLogEntity(),
                mediaEntity = _state.value.dailyLogItemDetails.mediaItems.toDailyLogMediaEntityList()
            )
            sendEffect(DailyLogEffect.OnNavigateBack)
        } finally {
            setLoading(false)
        }
    }

    private fun setLoading(isLoading: Boolean) {
        _state.update {
            it.copy(
                isLoading = isLoading
            )
        }
    }

    private fun handleTakePhoto() {
        if (!permissionManager.isCameraPermissionGranted()) {
            sendEffect(DailyLogEffect.OnRequestCameraPermission)
            _state.update { it.copy(
                pendingCameraAction = PendingCameraAction.PHOTO
            ) }
            return
        }
        val uri = fileRepository.createImageUri()
        _state.update {
            it.copy(
                tempImagePath = uri.toString()
            )
        }
        Log.d("TAG", "handleTakePhoto:")
        sendEffect(DailyLogEffect.OnTakePhoto)
    }

    private fun handleTakeVideo() {
        if (!permissionManager.isCameraPermissionGranted()) {
            sendEffect(DailyLogEffect.OnRequestCameraPermission)
            _state.update { it.copy(
                pendingCameraAction = PendingCameraAction.VIDEO
            ) }
            return
        }
        val uri = fileRepository.createVideoUri()
        _state.update {
            it.copy(
                tempVideoPath = uri.toString()
            )
        }
        sendEffect(DailyLogEffect.OnTakeVideo)
    }

    private fun handleMediaPicked(uris: List<Uri>) = viewModelScope.launch {
        setLoading(true)

        try {
            uris
                .map { uri ->
                    async {
                        fileRepository.saveImagesToCache(uri).onFailure { error ->
                            sendEffect(DailyLogEffect.ShowToast(ToastMessage.Text(error.message ?: "Failed to cache image")))
                        }.getOrNull()
                    }
                }
                .awaitAll()
                .filterNotNull()
                .forEach { path ->
                    updateImagePath(path)
                }
        } finally {
            setLoading(false)
        }
    }

    private suspend fun saveImagesToFileDir() {
        val unSavedImages = _state.value.dailyLogItemDetails.mediaItems.filter { it.isPendingSave && !it.isDeleted }
        unSavedImages.forEach { item->
            fileRepository.saveMedia(item.mediaPath.toUri()).onSuccess { newPath ->
                _state.update { current->
                    val updatedMediaItems = current.dailyLogItemDetails.mediaItems.map { mediaItem->
                        if(mediaItem == item){
                            mediaItem.copy(
                                mediaPath = newPath,
                                isPendingSave = false
                            )
                        } else mediaItem
                    }
                    current.copy(
                        dailyLogItemDetails = current.dailyLogItemDetails.copy(
                            mediaItems = updatedMediaItems
                        )
                    )
                }
            }
        }

    }



    private fun deleteDailyLog() = viewModelScope.launch {
        val id = _state.value.dailyLogItemDetails.id ?: return@launch
        habitRepository.deleteDailyLog(id)
        _state.update {
            it.copy(showDeleteDialog = false)
        }
        sendEffect(DailyLogEffect.OnNavigateBack)
    }

    private fun observeDateTimeChanges() = viewModelScope.launch {
        _state
            .map { it.dailyLogItemDetails.date to it.dailyLogItemDetails.time }
            .distinctUntilChanged()
            .collectLatest { (dateMillis, timeMillis) ->
                _state.update { current ->
                    current.copy(
                        dailyLogItemDetails = current.dailyLogItemDetails.copy(
                            formattedDateString = dateMillis.toFormattedString(),
                            formattedTimeString = timeMillis.toFormattedString(current.is24HourFormat)
                        )
                    )
                }
            }
    }

    private fun observeHourFormat() = viewModelScope.launch {
        dataStoreRepository.getIs24HourFormat.collectLatest { format24 ->
            _state.update { it.copy(is24HourFormat = format24) }
        }
    }

    private fun sendEffect(effect: DailyLogEffect) = viewModelScope.launch {
        _effect.send(effect)
    }

    override fun onCleared() {
        super.onCleared()

        fileRepository.clearCacheMedia()
    }
}
