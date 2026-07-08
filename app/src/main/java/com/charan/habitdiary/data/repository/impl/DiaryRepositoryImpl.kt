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
import com.charan.habitdiary.core.utils.suspendRunCatching
import com.charan.habitdiary.data.local.model.DailyLogWithMedia
import com.charan.habitdiary.core.utils.DateUtil.getStartOfDay
import com.charan.habitdiary.core.utils.DateUtil.getEndOfDay

class DiaryRepositoryImpl @Inject constructor(
    private val dailyLogDao: DailyLogDao,
    private val dailyLogMediaDao: DailyLogMediaDao
) : DiaryRepository {

    override suspend fun upsertDailyLog(
        dailyLog: DailyLogEntity,
        mediaEntity: List<DailyLogMediaEntity>
    ): Result<Unit> = suspendRunCatching {
        val id = dailyLogDao.upsertDailyLog(dailyLog)
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

    override suspend fun getAllDailyLogs(): Result<List<DailyLogEntity>> = suspendRunCatching {
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

    override suspend fun getDailyLogWithId(id: Long): Result<DailyLogEntity> = suspendRunCatching {
        dailyLogDao.getDailyLogWithId(id)
    }

    override suspend fun getDailyLogsWithHabitWithId(id: Long): Result<DailyLogWithHabit> = suspendRunCatching {
        dailyLogDao.getDailyLogsWithHabitWithId(id)
    }

    override suspend fun deleteDailyLog(id: Long): Result<Unit> = suspendRunCatching {
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
    ): Result<DailyLogEntity?> = suspendRunCatching {
        dailyLogDao.getLoggedHabitFromIdForRange(habitId, startOfDay, endOfDay)
    }

    override suspend fun upsertDailyLogMediaEntities(mediaEntity: List<DailyLogMediaEntity>): Result<Unit> = suspendRunCatching {
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

    override suspend fun getAllMedia(): Result<List<DailyLogMediaEntity>> = suspendRunCatching {
        dailyLogMediaDao.getAllMedia()
    }

    override suspend fun insertDailyLogs(dailyLogs: List<DailyLogEntity>): Result<List<Long>> = suspendRunCatching {
        dailyLogDao.insertDailyLogs(dailyLogs)
    }

    override fun getAllLogsWithHabitId(habitId: Long): Flow<Result<List<DailyLogEntity>>> {
        return dailyLogDao.getAllLogsForHabitId(habitId)
            .map { Result.success(it) }
            .catch { emit(Result.failure(it)) }
    }

    override fun getAllLogsWithHabit(sortBy: DailyLogSortType): Flow<Result<List<DailyLogWithHabit>>> {
        return when (sortBy) {
            DailyLogSortType.NEWEST_FIRST -> {
                dailyLogDao.getNewestLogWithHabit()
            }
            DailyLogSortType.OLDEST_FIRST -> {
                dailyLogDao.getOldestLogWithHabit()
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

    override suspend fun getDiaryMediaForDateRange(
        start: LocalDate,
        end: LocalDate
    ): Result<List<DailyLogWithMedia>> = suspendRunCatching {
        dailyLogDao.getDailyLogWithMediaForDateRange(
            start.getStartOfDay(),
            end.getEndOfDay()
        )
    }
}
