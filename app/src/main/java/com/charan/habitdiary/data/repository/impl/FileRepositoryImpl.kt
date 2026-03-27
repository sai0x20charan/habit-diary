package com.charan.habitdiary.data.repository.impl

import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Environment.getExternalStoragePublicDirectory
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.charan.habitdiary.data.repository.FileRepository
import com.charan.habitdiary.utils.ProcessState
import com.charan.habitdiary.utils.isSDK29OrAbove
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.Exception

class FileRepositoryImpl(
    private val context : Context
) : FileRepository {

    companion object {
        const val HABIT_DIARY_MEDIA_DIR = "habit_diary_media"
        // v0.1.0 was using this directory name
        const val HABIT_DIARY_IMAGES = "habit_diary_images"

        const val HABIT_DIARY_DOWNLOAD_FOLDER = "HabitDiary"
    }
    override fun saveImagesToCache(imageUri: Uri) =
        saveMediaInternal(File(context.cacheDir, HABIT_DIARY_MEDIA_DIR), imageUri)

    override fun saveMedia(imageUri: Uri) =
        saveMediaInternal(File(context.filesDir, HABIT_DIARY_MEDIA_DIR), imageUri)



    override fun createImageUri(): Uri {
        val file = File(
            context.cacheDir,
            "IMG_${System.currentTimeMillis()}.jpg"
        )

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    }

    override fun createVideoUri(): Uri {
        val file = File(
            context.cacheDir,
            "VID_${System.currentTimeMillis()}.mp4"
        )

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    }


    private fun saveMediaInternal(
        baseDir: File,
        sourceUri: Uri
    ): Flow<ProcessState<String>> = flow {
        emit(ProcessState.Loading())

        try {
            if (!baseDir.exists()) baseDir.mkdirs()
            val inputStream = if (sourceUri.scheme == "content") {
                context.contentResolver.openInputStream(sourceUri)
            } else {
                File(sourceUri.path ?: "").inputStream()
            }

            val mimeType = context.contentResolver.getType(sourceUri)
            val extension = when {
                mimeType?.startsWith("video/") == true || sourceUri.path?.endsWith(".mp4") == true -> ".mp4"
                mimeType?.startsWith("image/") == true || sourceUri.path?.endsWith(".jpg") == true -> ".jpg"
                else -> ".jpg"
            }

            val file = File(baseDir, "MEDIA_${System.currentTimeMillis()}$extension")

            inputStream?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            } ?: throw Exception("Could not open input stream")

            emit(ProcessState.Success(file.absolutePath))
        } catch (e: Exception) {
            emit(ProcessState.Error(e.message ?: "Unexpected error"))
        }
    }

    override fun clearCacheMedia() {
        try {
            println("Clearing cache media directory")
            val cacheDir = File(context.cacheDir, HABIT_DIARY_MEDIA_DIR)
            if (cacheDir.exists()) {
                cacheDir.deleteRecursively()
            }
        } catch (e: Exception) {
            Log.e("FileRepositoryImpl", "Error clearing cache media: ${e.message}")
        }
    }

    override fun saveMediaToDownloads(filePath: String) = flow {
        emit(ProcessState.Loading())

        try {
            val sourceFile = File(filePath)
            val resolver = context.contentResolver
            val mimeType = MimeTypeMap.getSingleton()
                .getMimeTypeFromExtension(sourceFile.extension) ?: "*/*"
            val fileName = "${System.currentTimeMillis().toString().takeLast(4)}_" + sourceFile.name

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+
                val collection = MediaStore.Downloads.EXTERNAL_CONTENT_URI

                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                    put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        "${Environment.DIRECTORY_DOWNLOADS}/${HABIT_DIARY_DOWNLOAD_FOLDER}"
                    )
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }

                val uri = resolver.insert(collection, values)
                    ?: throw Exception("Failed to create MediaStore entry")

                resolver.openOutputStream(uri)?.use { output ->
                    FileInputStream(sourceFile).use { input ->
                        input.copyTo(output)
                    }
                }
                val update = ContentValues().apply {
                    put(MediaStore.MediaColumns.IS_PENDING, 0)
                }
                resolver.update(uri, update, null, null)

            } else {
                // Android 9 and below
                val downloadsDir = getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS
                )
                val habitDiaryDir = File(downloadsDir, HABIT_DIARY_DOWNLOAD_FOLDER).apply {
                    if (!exists()) mkdirs()
                }

                val destFile = File(habitDiaryDir, fileName)
                FileInputStream(sourceFile).use { input ->
                    FileOutputStream(destFile).use { output ->
                        input.copyTo(output)
                    }
                }
            }

            emit(ProcessState.Success(true))

        } catch (e: Exception) {
            emit(ProcessState.Error(e.message ?: "Unexpected error"))
        }
    }
        .flowOn(Dispatchers.IO)


    override fun getMediaUri(filePath: String): Uri {
        val file = File(filePath)
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    }
}