package com.charan.habitdiary.presentation.habits

import com.charan.habitdiary.data.local.entity.DailyLogEntity
import com.charan.habitdiary.data.local.entity.HabitEntity
import com.charan.habitdiary.data.local.model.HabitWithDone
import com.charan.habitdiary.core.utils.DateUtil
import com.charan.habitdiary.core.utils.DateUtil.toFormattedString
import kotlinx.datetime.LocalDateTime

fun List<HabitEntity>.toHabitUIList() : List<HabitItemUIModel>{
    return this.map {
        HabitItemUIModel(
            id = it.id,
            habitName = it.habitName,
            habitDescription = it.habitDescription,
            habitTime = it.habitTime.toString(),
            logId = null,
            habitReminderTime = null
        )
    }
}

fun HabitWithDone.toHabitUIState(is24HourFormat: Boolean) : HabitItemUIModel {
    return HabitItemUIModel(
        id = this.habitEntity.id,
        habitName = this.habitEntity.habitName,
        habitDescription = this.habitEntity.habitDescription,
        habitTime = this.habitEntity.habitTime.toFormattedString(is24HourFormat),
        isDone = this.isDone,
        logId = this.logId,
        habitReminderTime = this.habitEntity.habitReminder?.toFormattedString(is24HourFormat),
        habitFrequency = this.habitEntity.habitFrequency.sortedBy { DateUtil.getDaysOfWeek().indexOf(it)}
    )
}

fun List<HabitWithDone>.toHabitUIState(is24HourFormat: Boolean) : List<HabitItemUIModel> {
    return this.map {
        it.toHabitUIState(is24HourFormat)
    }
}

fun HabitItemUIModel.toDailyLogEntity(date : LocalDateTime) : DailyLogEntity {
    return DailyLogEntity(
        logNote = "",
        imagePath = "",
        createdAt = date,
        habitId = this.id
    )
}
