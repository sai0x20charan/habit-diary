package com.charan.habitdiary.presentation.journey

import com.charan.habitdiary.data.local.model.DailyLogWithMedia
import com.charan.habitdiary.presentation.common.model.MediaItemUIModel

fun List<DailyLogWithMedia>.toFlashbackMedia(titleRes : Int) : FlashbackMedia {
    return FlashbackMedia(
        titleRes = titleRes,
        mediaItems = this.flatMap { logWithMedia ->
            logWithMedia.mediaEntities.map { mediaEntity ->
                MediaItemUIModel(
                    mediaPath = mediaEntity.mediaPath,
                    logId = mediaEntity.dailyLogId,
                    logDate = logWithMedia.dailyLogEntity.createdAt.date
                )
            }
        }
    )
}