package com.charan.habitdiary.presentation.add_daily_log

import android.net.Uri
import kotlinx.datetime.LocalTime

sealed class DailyLogEvent {

    data class OnNotesTextChange(val text : String) : DailyLogEvent()
    data class OnImagePathChange(val path : String) : DailyLogEvent()
    data object OnSaveDailyLogClick : DailyLogEvent()
    data object OnBackClick : DailyLogEvent()

    data class OnToggleImagePickOptionsSheet(val isVisible : Boolean) : DailyLogEvent()

    data object OnPickFromGalleryClick : DailyLogEvent()

    data object OnTakePhotoClick : DailyLogEvent()

    data class OnImagePick(val uris : List<Uri>) : DailyLogEvent()

    data object OnOpenSettingsForPermissions : DailyLogEvent()

    data class ToggleShowRationaleForCameraPermission(val showRationale : Boolean) : DailyLogEvent()

    data class OnToggleDateSelectorDialog(val isVisible : Boolean) : DailyLogEvent()

    data class OnDateSelected(val date : Long) : DailyLogEvent()

    data class OnToggleTimeSelectorDialog(val isVisible : Boolean) : DailyLogEvent()

    data class OnTimeSelected(val time : LocalTime) : DailyLogEvent()

    data class OnToggleDeleteDialog(val showDeleteDialog : Boolean) : DailyLogEvent()

    data object OnDeleteDailyLog : DailyLogEvent()

    data class OnSelectMediaItemForDelete(val mediaItem : String?) : DailyLogEvent()

    data class OnConfirmMediaItemDelete(val confirmDelete : Boolean) : DailyLogEvent()

    data object OnCaptureVideoClick : DailyLogEvent()

    data class OnVideoPick(val uri : List<Uri>) : DailyLogEvent()

    data class OnPermissionResult(val isGranted : Boolean) : DailyLogEvent()

    data object OnNavigateToHabitScreen : DailyLogEvent()

    data object OnToggleTextEditingControls : DailyLogEvent()

}