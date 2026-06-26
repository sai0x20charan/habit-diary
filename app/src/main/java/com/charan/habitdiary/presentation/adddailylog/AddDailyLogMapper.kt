package com.charan.habitdiary.presentation.adddailylog

import com.charan.habitdiary.data.local.entity.DailyLogEntity
import com.charan.habitdiary.data.local.entity.DailyLogMediaEntity
import com.charan.habitdiary.data.local.model.DailyLogWithHabit
import com.charan.habitdiary.core.utils.DateUtil

fun DailyLogState.toDailyLogEntity(): DailyLogEntity {
    val item = this.dailyLogItemDetails
    return DailyLogEntity(
        id = item.id ?: 0,
        logNote = item.notesText,
        imagePath = "",
        createdAt = DateUtil.mergeDateTime(item.date, item.time),
        habitId = item.habitId
    )
}

fun DailyLogMediaItem.toDailyLogMediaEntity() : DailyLogMediaEntity {
    return DailyLogMediaEntity(
        dailyLogId =  0,
        mediaPath = this.mediaPath,
        isDeleted = this.isDeleted
    )
}

fun List<DailyLogMediaItem>.toDailyLogMediaEntityList() : List<DailyLogMediaEntity> {
    return this.map {
        it.toDailyLogMediaEntity()
    }
}

fun DailyLogMediaEntity.toDailyLogMediaItem(isPendingSave : Boolean) : DailyLogMediaItem {
    return DailyLogMediaItem(
        mediaPath = this.mediaPath,
        isDeleted = this.isDeleted,
        id = this.id,
        isPendingSave = isPendingSave
    )
}

fun List<DailyLogMediaEntity>.toDailyLogMediaItemList(isPendingSave : Boolean) : List<DailyLogMediaItem> {
    return this.map {
        it.toDailyLogMediaItem(isPendingSave)
    }
}

fun DailyLogWithHabit.toDailyLogItemDetails(pendingSaveImage : Boolean = false) : DailyLogItemDetails {
    return DailyLogItemDetails(
        id = this.dailyLogEntity.id,
        notesText = this.dailyLogEntity.logNote,
        mediaItems = this.mediaEntities.toDailyLogMediaItemList(pendingSaveImage),
        date = this.dailyLogEntity.createdAt.date,
        time = this.dailyLogEntity.createdAt.time,
        habitId = this.dailyLogEntity.habitId,
        habitName = this.habitEntity?.habitName,
        habitDescription = this.habitEntity?.habitDescription
    )
}
