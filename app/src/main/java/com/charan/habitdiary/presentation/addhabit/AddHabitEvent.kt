package com.charan.habitdiary.presentation.addhabit

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

sealed class AddHabitEvent {

    data class InitializeHabit(val habitId : Long?) : AddHabitEvent()

    data class OnHabitNameChange(val habitName : String) : AddHabitEvent()
    data class OnHabitDescriptionChange(val habitDescription : String) : AddHabitEvent()
    data class OnHabitTimeChange(val time : LocalTime) : AddHabitEvent()

    data class OnToggleReminderTimeDialog(val showReminderTimeDialog : Boolean) : AddHabitEvent()

    data class OnHabitReminderToggle(val isEnabled : Boolean) : AddHabitEvent()

    data class OnToggleHabitTimeDialog(val showHabitTimeDialog : Boolean) : AddHabitEvent()
    data class OnHabitFrequencyChange(val dayOfWeek : DayOfWeek) : AddHabitEvent()
    data class OnHabitReminderTimeChange(val time : LocalTime) : AddHabitEvent()
    object OnSaveHabitClick : AddHabitEvent()

    data class TogglePermissionRationale(val showPermissionRationale : Boolean) : AddHabitEvent()

    data object OpenPermissionSettings : AddHabitEvent()

    data object OnNavigateBack : AddHabitEvent()
    data class OnToggleDeleteDialog(val showDeleteDialog : Boolean) : AddHabitEvent()
    data object OnDeleteHabit : AddHabitEvent()
}