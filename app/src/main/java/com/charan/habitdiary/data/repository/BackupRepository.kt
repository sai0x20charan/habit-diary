package com.charan.habitdiary.data.repository

import android.net.Uri

interface BackupRepository {

    suspend fun backupData(uri : Uri?): Result<Boolean>

    suspend fun importData(uri : Uri?) : Result<Boolean>
    val fileName : String
}