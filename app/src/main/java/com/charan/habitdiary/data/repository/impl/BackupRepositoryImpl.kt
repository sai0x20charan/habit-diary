package com.charan.habitdiary.data.repository.impl

import android.content.Context
import android.net.Uri
import android.util.Log
import com.charan.habitdiary.BuildConfig
import com.charan.habitdiary.data.local.entity.DailyLogEntity
import com.charan.habitdiary.data.local.entity.DailyLogMediaEntity
import com.charan.habitdiary.data.local.entity.HabitEntity
import com.charan.habitdiary.data.model.BackupMetaData
import com.charan.habitdiary.data.repository.BackupRepository
import com.charan.habitdiary.data.repository.DiaryRepository
import com.charan.habitdiary.data.repository.FileRepository
import com.charan.habitdiary.data.repository.HabitRepository
import com.charan.habitdiary.data.repository.impl.FileRepositoryImpl.Companion.HABIT_DIARY_IMAGES
import com.charan.habitdiary.data.repository.impl.FileRepositoryImpl.Companion.HABIT_DIARY_MEDIA_DIR
import com.charan.habitdiary.core.notification.NotificationScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.util.UUID
import java.util.zip.CRC32
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.decodeFromStream
import okio.Deflater
import javax.inject.Inject


class BackupRepositoryImpl @Inject constructor(
    @ApplicationContext private val context : Context,
    private val habitRepository: HabitRepository,
    private val diaryRepository: DiaryRepository,
    private val notificationScheduler: NotificationScheduler
) : BackupRepository{
    companion object{
        const val FILE_TYPE = "application/zip"

        private const val DATA_DIR = "data/"

        const val HABIT_FILE = "${DATA_DIR}habits.json"
        const val DAILY_LOG_FILE = "${DATA_DIR}daily_logs.json"
        const val MEDIA_FILE = "${DATA_DIR}media.json"

        const val META_FILE = "meta.json"

        const val HABIT_MEDIA_DIR = "habitMedia/"
        const val HABIT_IMAGES_DIR = "habitImages/"

        const val COPY_BUFFER_SIZE = 1024 * 128
    }
    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun backupData(uri: Uri?): Result<Boolean> = withContext(Dispatchers.IO) {
        if (uri == null) {
            return@withContext Result.failure(Exception("No File Found"))
        }
        return@withContext try {
            val habitsDeferred = async { habitRepository.getAllHabits() }
            val dailyLogsDeferred = async { diaryRepository.getAllDailyLogs() }
            val mediaDeferred = async { diaryRepository.getAllMedia() }

            val habits = habitsDeferred.await().onFailure { return@withContext Result.failure(it) }.getOrNull() ?: emptyList()
            val dailyLogs = dailyLogsDeferred.await().onFailure { return@withContext Result.failure(it) }.getOrNull() ?: emptyList()
            val media = mediaDeferred.await().onFailure { return@withContext Result.failure(it) }.getOrNull() ?: emptyList()
            val metaData = BackupMetaData(
                versionCode = BuildConfig.VERSION_CODE.toString(),
                appVersion = BuildConfig.VERSION_NAME,
                createdAt = System.currentTimeMillis()
            )

            val outputStream = context.contentResolver.openOutputStream(uri)
                ?: throw Exception("Failed to open output stream")

            ZipOutputStream(BufferedOutputStream(outputStream)).use { zip ->
                zip.setLevel(Deflater.BEST_SPEED)

                zip.putNextEntry(ZipEntry(META_FILE))
                Json.encodeToStream(metaData, zip)
                zip.closeEntry()


                zip.putNextEntry(ZipEntry(HABIT_FILE))
                Json.encodeToStream(habits, zip)
                zip.closeEntry()

                zip.putNextEntry(ZipEntry(DAILY_LOG_FILE))
                Json.encodeToStream(dailyLogs, zip)
                zip.closeEntry()

                zip.putNextEntry(ZipEntry(MEDIA_FILE))
                Json.encodeToStream(media, zip)
                zip.closeEntry()

                val mediaFiles = File(context.filesDir, HABIT_DIARY_MEDIA_DIR)
                val oldFiles = File(context.filesDir, HABIT_DIARY_IMAGES)

                if (mediaFiles.exists()) {
                    mediaFiles.listFiles()?.forEach { file ->
                        zip.writeStoredFile(file, "$HABIT_MEDIA_DIR${file.name}")
                    }
                }

                if (oldFiles.exists()) {
                    oldFiles.listFiles()?.forEach { file ->
                        zip.writeStoredFile(file, "$HABIT_IMAGES_DIR${file.name}")
                    }
                }

            }
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun importData(uri: Uri?): Result<Boolean> = withContext(Dispatchers.IO) {
        if (uri == null) {
            return@withContext Result.failure(Exception("No File Found"))
        }
        var importedHabits: List<HabitEntity> = emptyList()
        var importedDailyLogs: List<DailyLogEntity> = emptyList()
        var importedMediaEntities: List<DailyLogMediaEntity> = emptyList()

        val fileNameMapping = mutableMapOf<String, String>()

        return@withContext try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: throw Exception("Failed to open input stream")
            ZipInputStream(BufferedInputStream(inputStream)).use { zip ->
                var entry = zip.nextEntry


                while (entry != null) {
                    val entryName = entry.name

                    when {
                        entryName == HABIT_FILE -> {
                            importedHabits = Json.decodeFromStream(zip)
                        }
                        entryName == DAILY_LOG_FILE -> {
                            importedDailyLogs = Json.decodeFromStream(zip)
                        }
                        entryName == MEDIA_FILE -> {
                            importedMediaEntities = Json.decodeFromStream(zip)
                        }
                        entryName.startsWith(HABIT_MEDIA_DIR) ||
                                entryName.startsWith(HABIT_IMAGES_DIR) -> {
                            val targetDir = File(context.filesDir, HABIT_DIARY_MEDIA_DIR)
                            if (!targetDir.exists()) targetDir.mkdirs()
                            val originalFileName = File(entryName).name
                            val extension = originalFileName.substringAfterLast('.', "jpg")
                            val newFileName =
                                "MEDIA_${System.currentTimeMillis()}_${UUID.randomUUID()}.$extension"

                            val newFile = File(targetDir, newFileName)

                            BufferedOutputStream(newFile.outputStream(), COPY_BUFFER_SIZE).use { output ->
                                zip.copyTo(output, bufferSize = COPY_BUFFER_SIZE)
                            }

                            fileNameMapping[originalFileName] = newFile.absolutePath
                        }
                    }
                    zip.closeEntry()
                    entry = zip.nextEntry
                }
            }
            val habitIdMap = mutableMapOf<Long, Long>()
            if (importedHabits.isNotEmpty()) {
                val insertHabits = importedHabits.map { it.copy(id = 0) }
                val newIds = habitRepository.insertHabits(insertHabits).onFailure { return@withContext Result.failure(it) }.getOrNull() ?: emptyList()

                importedHabits.forEachIndexed { index, oldHabit ->
                    habitIdMap[oldHabit.id] = newIds[index]
                }

                insertHabits.forEachIndexed { index, habit ->
                    val newId = newIds[index]
                    if (habit.isReminderEnabled) {
                        notificationScheduler.scheduleReminder(
                            habitId = newId,
                            time = habit.habitReminder,
                            frequency = habit.habitFrequency,
                            isReminderEnabled = true
                        )
                    }
                }
            }

            val newDailyLogIdMap = mutableMapOf<Long, Long>()
            if (importedDailyLogs.isNotEmpty()) {
                val validLogPairs = importedDailyLogs.mapNotNull { oldLog ->
                    val newHabitId = if (oldLog.habitId != null) {
                        habitIdMap[oldLog.habitId] ?: return@mapNotNull null
                    } else {
                        null
                    }
                    oldLog to oldLog.copy(
                        id = 0,
                        habitId = newHabitId
                    )
                }

                val insertDailyLogs = validLogPairs.map { it.second }
                val newIds = diaryRepository.insertDailyLogs(insertDailyLogs).onFailure { return@withContext Result.failure(it) }.getOrNull() ?: emptyList()

                validLogPairs.forEachIndexed { index, (oldLog, _) ->
                    newDailyLogIdMap[oldLog.id] = newIds[index]
                }
            }
            if (importedMediaEntities.isNotEmpty()) {
                val finalMediaEntities = importedMediaEntities.mapNotNull { media ->
                    val newLogId = newDailyLogIdMap[media.dailyLogId] ?: return@mapNotNull null
                    val oldFileName = File(media.mediaPath).name
                    val newPath = fileNameMapping[oldFileName] ?: return@mapNotNull null

                    media.copy(
                        id = 0,
                        dailyLogId = newLogId,
                        mediaPath = newPath
                    )
                }

                diaryRepository.upsertDailyLogMediaEntities(finalMediaEntities).onFailure { return@withContext Result.failure(it) }
            }

            Result.success(true)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }


    override val fileName: String
        get() {
            val appName = context.applicationInfo.loadLabel(context.packageManager)
            return "${appName}_Backup_${System.currentTimeMillis()}.zip"
        }

    private fun crc32Of(file: File): Long {
        val crc = CRC32()
        file.inputStream().use { input ->
            val buffer = ByteArray(COPY_BUFFER_SIZE)
            var read: Int
            while (input.read(buffer).also { read = it } != -1) {
                crc.update(buffer, 0, read)
            }
        }
        return crc.value
    }

    private fun ZipOutputStream.writeStoredFile(file: File, entryName: String) {
        val entry = ZipEntry(entryName).apply {
            method = ZipEntry.STORED
            size = file.length()
            crc = crc32Of(file)
        }
        putNextEntry(entry)
        file.inputStream().use { it.copyTo(this, bufferSize = COPY_BUFFER_SIZE) }
        closeEntry()
    }
}