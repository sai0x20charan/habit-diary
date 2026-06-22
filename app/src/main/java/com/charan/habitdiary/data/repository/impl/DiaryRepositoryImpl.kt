package com.charan.habitdiary.data.repository.impl

import com.charan.habitdiary.data.local.dao.DailyLogDao
import com.charan.habitdiary.data.local.dao.DailyLogMediaDao
import com.charan.habitdiary.data.local.entity.DailyLogEntity
import com.charan.habitdiary.data.local.entity.DailyLogMediaEntity
import com.charan.habitdiary.data.local.model.DailyLogWithHabit
import com.charan.habitdiary.data.model.enums.DailyLogSortType
import com.charan.habitdiary.data.repository.DiaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import javax.inject.Inject

class DiaryRepositoryImpl @Inject constructor(
    private val dailyLogDao: DailyLogDao,
    private val dailyLogMediaDao: DailyLogMediaDao
) : DiaryRepository {

    override suspend fun upsetDailyLog(
        dailyLog: DailyLogEntity,
        mediaEntity: List<DailyLogMediaEntity>
    ): Result<Unit> = runCatching {
        val id = dailyLogDao.upsetDailyLog(dailyLog)
        if (mediaEntity.isNotEmpty()) {
            val mappedMedia = mediaEntity.map { it.copy(dailyLogId = id) }
            dailyLogMediaDao.upsertMedia(mappedMedia)
        }
    }

    override fun getAllDailyLogsFlow(): Flow<Result<List<DailyLogEntity>>> {
        return dailyLogDao.getAllDailyLogsFlow()
            .map { Result.success(it) }
            .catch { emit(Result.failure(it)) }
    }

    override suspend fun getAllDailyLogs(): Result<List<DailyLogEntity>> = runCatching {
        dailyLogDao.getAllDailyLogs()
    }

    override fun getDailyLogsInRange(
        startOfDay: LocalDateTime,
        endOfDay: LocalDateTime,
        sortBy: DailyLogSortType
    ): Flow<Result<List<DailyLogWithHabit>>> {
        return when (sortBy) {
            DailyLogSortType.NEWEST_FIRST -> {
                dailyLogDao.getDailyLogsInRangeNewestFirst(startOfDay, endOfDay)
            }
            DailyLogSortType.OLDEST_FIRST -> {
                dailyLogDao.getDailyLogsInRangeOldestFirst(startOfDay, endOfDay)
            }
        }.map { logs ->
            val mapped = logs.map { log ->
                log.copy(
                    mediaEntities = log.mediaEntities.filter { !it.isDeleted }
                )
            }
            Result.success(mapped)
        }.catch { emit(Result.failure(it)) }
    }

    override suspend fun getDailyLogWithId(id: Long): Result<DailyLogEntity> = runCatching {
        dailyLogDao.getDailyLogWithId(id)
    }

    override suspend fun getDailyLogsWithHabitWithId(id: Long): Result<DailyLogWithHabit> = runCatching {
        dailyLogDao.getDailyLogsWithHabitWithId(id)
    }

    override suspend fun deleteDailyLog(id: Long): Result<Unit> = runCatching {
        dailyLogDao.deleteDailyLog(id)
    }

    override fun getLoggedHabitIdsForRange(
        startOfDay: LocalDateTime,
        endOfDay: LocalDateTime
    ): Flow<Result<List<DailyLogEntity>>> {
        return dailyLogDao.getLoggedHabitIdsForToday(startOfDay, endOfDay)
            .map { Result.success(it) }
            .catch { emit(Result.failure(it)) }
    }

    override suspend fun getLoggedHabitFromIdForRange(
        habitId: Long,
        startOfDay: LocalDateTime,
        endOfDay: LocalDateTime
    ): Result<DailyLogEntity?> = runCatching {
        dailyLogDao.getLoggedHabitFromIdForRange(habitId, startOfDay, endOfDay)
    }

    override suspend fun upsetDailyLogMediaEntities(mediaEntity: List<DailyLogMediaEntity>): Result<Unit> = runCatching {
        dailyLogMediaDao.upsertMedia(mediaEntity)
    }

    override fun getLoggedDatesInRange(
        start: LocalDateTime,
        end: LocalDateTime
    ): Flow<Result<List<LocalDate>>> {
        return dailyLogDao.getLoggedDatesInRange(start, end)
            .map { Result.success(it) }
            .catch { emit(Result.failure(it)) }
    }

    override suspend fun getAllMedia(): Result<List<DailyLogMediaEntity>> = runCatching {
        dailyLogMediaDao.getAllMedia()
    }

    override suspend fun insertDailyLogs(dailyLogs: List<DailyLogEntity>): Result<List<Long>> = runCatching {
        dailyLogDao.insertDailyLogs(dailyLogs)
    }

    override fun getAllLogsWithHabitId(habitId: Long): Flow<Result<List<DailyLogEntity>>> {
        return dailyLogDao.getAllLogsForHabitId(habitId)
            .map { Result.success(it) }
            .catch { emit(Result.failure(it)) }
    }
}
