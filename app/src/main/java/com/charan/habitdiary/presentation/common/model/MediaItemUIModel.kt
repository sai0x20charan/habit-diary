package com.charan.habitdiary.presentation.common.model

import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDate

@Serializable
data class MediaItemUIModel(
    val mediaPath: String,
    val logId: Long? = null,
    val logDate: LocalDate? = null
)
