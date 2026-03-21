package com.charan.habitdiary.presentation.add_daily_log

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

data class DailyLogState(
    val showImagePickOptionsSheet : Boolean = false,
    val tempImagePath : String = "",
    val tempVideoPath : String = "",
    val showRationaleForCameraPermission : Boolean = false,
    val showDateSelectDialog : Boolean = false,
    val showTimeSelectDialog : Boolean = false,
    val isEdit : Boolean = false,
    val showDeleteDialog : Boolean = false,
    val is24HourFormat : Boolean = false,
    val dailyLogItemDetails : DailyLogItemDetails = DailyLogItemDetails(),
    val selectedMediaItemForDelete : DailyLogMediaItem? = null,
    val showImageDeleteOption : Boolean = false,
    val isLoading : Boolean = false,
    val pendingCameraAction : PendingCameraAction? = null,
    val isHabitDeleted : Boolean = false,
    val isTextEditingControlsExpanded : Boolean = false,
)

enum class PendingCameraAction {
    PHOTO,
    VIDEO
}

data class DailyLogItemDetails(
    val id: Long? = null,
    val notesText: String = "",
    val mediaItems: List<DailyLogMediaItem> = emptyList(),
    val formattedDateString: String = "",
    val formattedTimeString: String = "",
    val time: LocalTime = LocalTime(0,0),
    val date: LocalDate = LocalDate(1970,1,1),
    val habitId: Long? = null,
    val habitName: String? = null,
    val habitDescription: String? = null
)

data class DailyLogMediaItem(
    val mediaPath : String = "",
    val isDeleted : Boolean = false,
    val id : Long? = null,
    val isPendingSave : Boolean = true
)


