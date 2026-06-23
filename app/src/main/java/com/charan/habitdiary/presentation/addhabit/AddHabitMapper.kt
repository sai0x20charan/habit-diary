package com.charan.habitdiary.presentation.addhabit

import com.charan.habitdiary.data.local.entity.HabitEntity
import com.charan.habitdiary.core.utils.DateUtil

fun AddHabitState.toHabitEntity(): HabitEntity {
    return HabitEntity(
        habitName = this.habitTitle,
        habitDescription = this.habitDescription,
        habitTime = this.habitTime,
        habitReminder = if(this.isReminderEnabled) this.habitReminderTime else null,
        habitFrequency = this.habitFrequency,
        isReminderEnabled = this.isReminderEnabled,
        id = this.habitId ?: 0,
        createdAt = this.createdAt ?: DateUtil.getCurrentDateTime()
    )
}
