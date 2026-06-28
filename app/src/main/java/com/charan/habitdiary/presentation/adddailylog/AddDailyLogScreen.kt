package com.charan.habitdiary.presentation.adddailylog

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.charan.habitdiary.R
import com.charan.habitdiary.presentation.adddailylog.DailyLogEvent.*
import com.charan.habitdiary.presentation.adddailylog.components.DiaryEditorToolbar
import com.charan.habitdiary.presentation.adddailylog.components.AddNoteItem
import com.charan.habitdiary.presentation.adddailylog.components.DateTimeRow
import com.charan.habitdiary.presentation.adddailylog.components.HabitDetailsCard
import com.charan.habitdiary.presentation.adddailylog.components.ImagePickOptionsBottomSheet
import com.charan.habitdiary.presentation.common.components.CustomCarouselImageItem
import com.charan.habitdiary.presentation.common.components.CustomMediumTopBar
import com.charan.habitdiary.presentation.common.components.DeleteWarningDialog
import com.charan.habitdiary.presentation.common.components.RationaleDialog
import com.charan.habitdiary.presentation.common.components.SelectDateDialog
import com.charan.habitdiary.presentation.common.components.SelectTimeDialog
import com.charan.habitdiary.presentation.common.components.toScreenContentPadding
import com.charan.habitdiary.core.utils.isVideo
import com.charan.habitdiary.core.utils.showToast
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.datetime.LocalDate
import com.charan.habitdiary.presentation.common.model.MediaItemUIModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalPermissionsApi::class
)
@Composable
fun AddDailyLogScreen(
    onNavigateBack : () -> Unit,
    logId : Long? = null,
    date : LocalDate?= null,
    openImageCaptureOnLaunch : Boolean = false,
    openVideoRecordingOnLaunch : Boolean = false,
    onHabitOpen : (habitId : Long) -> Unit,
    onImageOpen : (allImages : List<MediaItemUIModel>, currentImage : MediaItemUIModel, showLogEntryButton: Boolean) -> Unit,
    sharedMedia : List<String>? = null
) {
    val viewModel = hiltViewModel<DailyLogViewModel, DailyLogViewModel.Factory>(
        creationCallback = { factory ->
            factory.create(logId,date, openImageCaptureOnLaunch, openVideoRecordingOnLaunch,sharedMedia)
        }
    )
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    val richTextState = rememberRichTextState()
    val imagePickOptionsBottomSheetState = rememberModalBottomSheetState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris->
        if(uris.isNotEmpty()){
            viewModel.onEvent(DailyLogEvent.OnImagePick(uris))
        }

    }
    val captureImage = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success->
        if(success){
            viewModel.onEvent(DailyLogEvent.OnImagePick(listOf(state.tempImagePath.toUri())))
        }
    }

    val captureVideo = rememberLauncherForActivityResult(ActivityResultContracts.CaptureVideo()) { success->
        if(success){
            viewModel.onEvent(DailyLogEvent.OnImagePick(listOf(state.tempVideoPath.toUri())))
        }
    }
    val cameraPermission = rememberPermissionState(
        permission = Manifest.permission.CAMERA
    ) {
        if(it){
            viewModel.onEvent(DailyLogEvent.OnPermissionResult(it))
        }

    }

    if(state.showRationaleForCameraPermission){
        RationaleDialog(
            title = stringResource(R.string.camera_permission_required),
            message = stringResource(R.string.camera_permission_description),
            onDismissRequest = {
                viewModel.onEvent(DailyLogEvent.ToggleShowRationaleForCameraPermission(false))
            },
            onConfirmRequest = {
                viewModel.onEvent(DailyLogEvent.OnOpenSettingsForPermissions)
                viewModel.onEvent(DailyLogEvent.ToggleShowRationaleForCameraPermission(false))


            }
        )
    }

    if(state.showImageDeleteOption) {
        val mediaTypeIsVideo = state.selectedMediaItemForDelete?.mediaPath?.isVideo() == true
        DeleteWarningDialog(
            onConfirm = {
                viewModel.onEvent(DailyLogEvent.OnConfirmMediaItemDelete(true))
            },
            onDismiss = {
                viewModel.onEvent(DailyLogEvent.OnConfirmMediaItemDelete(false))
            },
            title = if(mediaTypeIsVideo) stringResource(
                R.string.delete_video
            ) else stringResource(R.string.delete_image),
            message = if(mediaTypeIsVideo) stringResource(
                R.string.delete_video_confirmation_description
            ) else stringResource(R.string.delete_image_confirmation_description)
        )
    }

    if(state.showDateSelectDialog){
        SelectDateDialog(
            onDismissRequest = {
                viewModel.onEvent(DailyLogEvent.OnToggleDateSelectorDialog(false))
            },
            onDateSelected = {
                viewModel.onEvent(DailyLogEvent.OnDateSelected(it ?: 0L))
            },
            dateMillis = state.dailyLogItemDetails.date,


        )
    }

    if(state.showTimeSelectDialog){
        SelectTimeDialog(
            onDismiss = {
                viewModel.onEvent(DailyLogEvent.OnToggleTimeSelectorDialog(false))
            },
            onTimeSelected = {
                viewModel.onEvent(DailyLogEvent.OnTimeSelected(it))
            },
            selectedTime = state.dailyLogItemDetails.time,
            is24HourFormat = state.is24HourFormat
        )
    }


    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest {
            when(it){
                DailyLogEffect.OnNavigateBack -> {
                    onNavigateBack()

                }
                DailyLogEffect.OnOpenMediaPicker -> {
                    pickMedia.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                    )

                }

                is DailyLogEffect.OnTakePhoto -> {
                    captureImage.launch(state.tempImagePath.toUri())
                }

                is DailyLogEffect.OnRequestCameraPermission -> {
                    if(cameraPermission.status.shouldShowRationale){
                        viewModel.onEvent(ToggleShowRationaleForCameraPermission(true))
                    } else {
                        cameraPermission.launchPermissionRequest()
                    }

                }

                DailyLogEffect.OnTakeVideo -> {
                    captureVideo.launch(state.tempVideoPath.toUri())
                }

                is DailyLogEffect.OnNavigateToHabitScreen -> {
                    onHabitOpen(it.habitId)
                }

                is DailyLogEffect.OnNavigateToImageViewer -> {
                    onImageOpen(it.allImages, it.currentImage, false)
                }

                is DailyLogEffect.ShowToast -> {
                    context.showToast(it.message)

                }
            }
        }
    }

    if(state.showImagePickOptionsSheet){
        ImagePickOptionsBottomSheet(
            onImageFromGalleryClick = {
                viewModel.onEvent(DailyLogEvent.OnPickFromGalleryClick)

            },
            onImageFromCameraClick = {
                viewModel.onEvent(DailyLogEvent.OnTakePhotoClick)

            },
            onDismissRequest = {
                viewModel.onEvent(DailyLogEvent.OnToggleImagePickOptionsSheet(false))
            },
            onCaptureVideo = {
                viewModel.onEvent(DailyLogEvent.OnCaptureVideoClick)
            },

            sheetState = imagePickOptionsBottomSheetState
        )


    }

    if(state.showDeleteDialog){
        DeleteWarningDialog(
            onConfirm = {
                viewModel.onEvent(DailyLogEvent.OnDeleteDailyLog)
            },
            onDismiss = {
                viewModel.onEvent(DailyLogEvent.OnToggleDeleteDialog(false))
            }
        )
    }

    Scaffold(
        topBar = {
            CustomMediumTopBar(
                title = if(state.isEdit) stringResource(R.string.edit_daily_log) else stringResource(R.string.add_daily_log)
                ,
                scrollBehavior = scrollBehavior,
                showBackButton = true,
                onBackClick = {
                    viewModel.onEvent(DailyLogEvent.OnBackClick)
                },
                actions = {
                    if(state.isLoading){
                        CircularWavyProgressIndicator(
                            modifier = Modifier.size(30.dp)
                        )
                    }
                },
            )
        },
        bottomBar = {
            val hasMedia = state.dailyLogItemDetails.mediaItems.any { !it.isDeleted }
            val hasNotes = state.dailyLogItemDetails.notesText.isNotBlank()
            val hasHabit = state.dailyLogItemDetails.habitId != null
            val canEditContent = (hasMedia || hasNotes) && !state.isLoading
                    DiaryEditorToolbar(
                        onSaveLog = {
                            viewModel.onEvent(DailyLogEvent.OnSaveDailyLogClick)
                        },
                        onDeleteLogClick = {
                            viewModel.onEvent(DailyLogEvent.OnToggleDeleteDialog(true))
                        },
                        onExpandEditingToolsClick = { viewModel.onEvent(DailyLogEvent.OnToggleTextEditingControls) },
                        isEditingToolsExpanded = state.isTextEditingControlsExpanded,
                        onBoldTextClick = {
                            richTextState.toggleSpanStyle(SpanStyle(fontWeight = FontWeight.Bold))
                        },
                        onItalicTextClick = {
                            richTextState.toggleSpanStyle(SpanStyle(fontStyle = FontStyle.Italic))
                        },
                        onUnderlineTextClick = {
                            richTextState.toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                        },
                        isBoldTextSelected = richTextState.currentSpanStyle.fontWeight == FontWeight.Bold,
                        isItalicTextSelected = richTextState.currentSpanStyle.fontStyle == FontStyle.Italic,
                        isUnderlineTextSelected = richTextState.currentSpanStyle.textDecoration == TextDecoration.Underline,
                        isSaveEnabled = canEditContent || hasHabit,
                        showDeleteButton = state.isEdit,
                        onAttachMedia = {
                            viewModel.onEvent(DailyLogEvent.OnToggleImagePickOptionsSheet(true))
                        }
                    )
        },
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .imePadding()
    ) { innerPadding->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = innerPadding.toScreenContentPadding()
        ) {
            if(state.dailyLogItemDetails.habitId!=null){
                item {
                    HabitDetailsCard(
                        habitTitle = state.dailyLogItemDetails.habitName ?: "",
                        onClick = {
                            viewModel.onEvent(DailyLogEvent.OnNavigateToHabitScreen)
                        }

                    )
                }
            }
            item {
                DateTimeRow(
                    date = state.dailyLogItemDetails.formattedDateString,
                    time = state.dailyLogItemDetails.formattedTimeString,
                    onDateClick = {
                        viewModel.onEvent(DailyLogEvent.OnToggleDateSelectorDialog(true))
                    },
                    onTimeClick = {
                        viewModel.onEvent(DailyLogEvent.OnToggleTimeSelectorDialog(true))
                    }
                )
                if(state.dailyLogItemDetails.mediaItems.isNotEmpty()) {
                    val activeMedia = state.dailyLogItemDetails.mediaItems.filter { !it.isDeleted }
                    CustomCarouselImageItem(
                        mediaPaths = activeMedia.map { it.mediaPath },
                        onRemoveClick = {
                            viewModel.onEvent(DailyLogEvent.OnSelectMediaItemForDelete(
                                it

                            ))

                        },
                        isEdit = true,
                        onImageOpen = { imagePath ->
                            viewModel.onEvent(DailyLogEvent.OnImageClick(imagePath))
                        }
                    )
                }
                AddNoteItem(
                    value = state.dailyLogItemDetails.notesText,
                    onValueChange = {
                        viewModel.onEvent(DailyLogEvent.OnNotesTextChange(it))
                    },
                    state = richTextState
                )


            }


        }

    }

}
