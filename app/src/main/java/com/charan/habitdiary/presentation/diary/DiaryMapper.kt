package com.charan.habitdiary.presentation.diary

import com.charan.habitdiary.data.local.model.DailyLogWithHabit
import com.charan.habitdiary.presentation.common.model.DailyLogItemUIModel
import com.charan.habitdiary.core.utils.DateUtil.toFormattedString

fun DailyLogWithHabit.toDailyLogUIState(is24HourFormat : Boolean) : DailyLogItemUIModel {
    return DailyLogItemUIModel(
        id = this.dailyLogEntity.id,
        logNote = this.dailyLogEntity.logNote,
        mediaPaths = this.mediaEntities.map { it.mediaPath },
        createdAt = this.dailyLogEntity.createdAt.time.toFormattedString(is24HourFormat),
        habitId = this.dailyLogEntity.habitId,
        habitName = this.habitEntity?.habitName
    )
}

fun List<DailyLogWithHabit>.toDailyLogUIStateList(is24HourFormat: Boolean) : List<DailyLogItemUIModel> {
    return this.map {
        it.toDailyLogUIState(is24HourFormat)
    }
}
