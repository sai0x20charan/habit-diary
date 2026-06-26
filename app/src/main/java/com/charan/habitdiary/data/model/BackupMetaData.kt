package com.charan.habitdiary.data.model

import kotlinx.serialization.Serializable

@Serializable
data class BackupMetaData(
    val appVersion : String,
    val versionCode : String,
    val createdAt : Long
)
