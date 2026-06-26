package com.charan.habitdiary.data.repository

import android.net.Uri

interface FileRepository {

    suspend fun saveImagesToCache(imageUri : Uri) : Result<String>

    suspend fun saveMedia(imageUri : Uri) : Result<String>

    fun createImageUri() : Uri

    fun createVideoUri() : Uri

    fun clearCacheMedia()

    suspend fun saveMediaToDownloads(filePath : String) : Result<Boolean>

    fun getMediaUri(filePath : String) : Uri
}