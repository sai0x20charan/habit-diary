package com.charan.habitdiary.presentation.addhabit

import com.charan.habitdiary.core.utils.DateUtil
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

data class AddHabitState(
    val habitTitle : String = "",
    val habitDescription : String = "",
    val habitReminderTime : LocalTime = LocalTime(8,0),
    val formatedReminderTime : String = "08:00",
    val isLoading : String = "",
    val habitTime : LocalTime = LocalTime(8,0),
    val formatedHabitTime : String = "08:00",
    val habitFrequency : List<DayOfWeek> = DateUtil.defaultHabitFrequency(),
    val showHabitTimeDialog : Boolean = false,
    val showReminderTimeDialog : Boolean = false,
    val isReminderEnabled : Boolean = false,
    val showPermissionRationale : Boolean = false,
    val showDeleteDialog : Boolean = false,
    val habitId : Long? = null,
    val isEdit : Boolean = false,
    val is24HourFormat : Boolean = false,
    val createdAt: LocalDateTime? = null
)