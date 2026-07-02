package com.charan.habitdiary.presentation.common.model

import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDate

@Serializable
@Stable
data class MediaItemUIModel(
    val mediaPath: String,
    val logId: Long? = null,
    val logDate: LocalDate? = null
)
