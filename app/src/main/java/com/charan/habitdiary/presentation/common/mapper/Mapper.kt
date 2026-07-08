package com.charan.habitdiary.presentation.common.mapper

import com.charan.habitdiary.data.local.entity.DailyLogMediaEntity
import com.charan.habitdiary.presentation.common.model.MediaItemUIModel

fun DailyLogMediaEntity.toMediaItemUIModel() : MediaItemUIModel {
    return MediaItemUIModel(
        mediaPath = this.mediaPath,
        logId = this.dailyLogId,
    )
}

fun List<DailyLogMediaEntity>.toMediaItemUIModelList() : List<MediaItemUIModel> {
    return this.map { it.toMediaItemUIModel() }
}