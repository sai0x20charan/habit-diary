package com.charan.habitdiary.data.repository.impl

import com.charan.habitdiary.data.local.dao.DailyLogDao
import com.charan.habitdiary.data.local.dao.DailyLogMediaDao
import com.charan.habitdiary.data.local.dao.HabitDao
import com.charan.habitdiary.data.local.entity.DailyLogEntity
import com.charan.habitdiary.data.local.entity.DailyLogMediaEntity
import com.charan.habitdiary.data.local.entity.HabitEntity
import com.charan.habitdiary.data.local.model.DailyLogWithHabit
import com.charan.habitdiary.data.local.model.HabitWithDone
import com.charan.habitdiary.data.model.enums.DailyLogSortType
import com.charan.habitdiary.data.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import javax.inject.Inject

class HabitRepositoryImpl @Inject constructor(
    private val habitDao : HabitDao,
    private val dailyLogDao : DailyLogDao,
    private val dailyLogMediaDao: DailyLogMediaDao
) : HabitRepository {

    override suspend fun upsetHabit(habit: HabitEntity) : Result<Long> = runCatching {
        habitDao.upsetHabit(habit)
    }

    override suspend fun upsetDailyLog(
        dailyLog: DailyLogEntity,
        mediaEntity : List<DailyLogMediaEntity>
    ): Result<Unit> = runCatching {
        val id = dailyLogDao.upsetDailyLog(dailyLog)
        if(mediaEntity.isNotEmpty()){
            val mappedMedia = mediaEntity.map { it.copy(dailyLogId = id) }
            dailyLogMediaDao.upsertMedia(mappedMedia)
        }
    }

    override fun getAllHabitsFlow(): Flow<Result<List<HabitEntity>>> {
        return habitDao.getAllHabitsFlow().map { Result.success(it) }.catch { emit(Result.failure(it)) }
    }

    override suspend fun getAllHabits(): Result<List<HabitEntity>> = runCatching {
        habitDao.getAllHabits()
    }

    override fun getAllDailyLogsFlow(): Flow<Result<List<DailyLogEntity>>> {
        return dailyLogDao.getAllDailyLogsFlow().map { Result.success(it) }.catch { emit(Result.failure(it)) }
    }
    override suspend fun getAllDailyLogs(): Result<List<DailyLogEntity>> = runCatching {
        dailyLogDao.getAllDailyLogs()
    }

    override fun getDailyLogsInRange(
        startOfDay: LocalDateTime,
        endOfDay: LocalDateTime,
        sortBy : DailyLogSortType
    ): Flow<Result<List<DailyLogWithHabit>>> {
        return when(sortBy){
            DailyLogSortType.NEWEST_FIRST -> {
                dailyLogDao
                    .getDailyLogsInRangeNewestFirst(startOfDay, endOfDay)

            }
            DailyLogSortType.OLDEST_FIRST -> {
                dailyLogDao
                    .getDailyLogsInRangeOldestFirst(startOfDay, endOfDay)
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



    override fun getActiveHabits(): Flow<Result<List<HabitWithDone>>> {
        return combine(
            habitDao.getActiveHabitsFlow(),
            getLoggedHabitIdsForRange().map { it.getOrNull() ?: emptyList() }
        ) { habits, dailyLogs ->
            val logMap = dailyLogs.associateBy { it.habitId }
            val mapped = habits.map { habit ->
                val log = logMap[habit.id]
                HabitWithDone(
                    habitEntity = habit,
                    isDone = log != null,
                    logId = log?.id,
                    created = log?.createdAt
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

    override suspend fun getHabitWithId(id: Long): Result<HabitEntity> = runCatching {
        habitDao.getHabitWithId(id)
    }

    override suspend fun deleteDailyLog(id: Long): Result<Unit> = runCatching {
        dailyLogDao.deleteDailyLog(id)
    }

    override suspend fun deleteHabit(id: Long): Result<Unit> = runCatching {
        habitDao.deleteHabit(id)
    }

    override fun getLoggedHabitIdsForRange(startOfDay: LocalDateTime,endOfDay: LocalDateTime): Flow<Result<List<DailyLogEntity>>> {
        return dailyLogDao.getLoggedHabitIdsForToday(startOfDay,endOfDay).map { Result.success(it) }.catch { emit(Result.failure(it)) }
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
        return dailyLogDao.getLoggedDatesInRange(start,end).map { Result.success(it) }.catch { emit(Result.failure(it)) }
    }

    override suspend fun getAllMedia(): Result<List<DailyLogMediaEntity>> = runCatching {
        dailyLogMediaDao.getAllMedia()
    }

    override suspend fun insertDailyLogs(dailyLogs: List<DailyLogEntity>): Result<List<Long>> = runCatching {
        dailyLogDao.insertDailyLogs(dailyLogs)
    }
    override suspend fun insertHabits(habits: List<HabitEntity>) : Result<List<Long>> = runCatching {
        habitDao.insertHabits(habits)
    }

    override fun getAllLogsWithHabitId(habitId: Long): Flow<Result<List<DailyLogEntity>>> {
        return dailyLogDao.getAllLogsForHabitId(habitId).map { Result.success(it) }.catch { emit(Result.failure(it)) }
    }

    override fun getTodayHabits(currentDayOfWeek: DayOfWeek): Flow<Result<List<HabitWithDone>>> {
        return combine(
            habitDao.getTodayHabits(currentDayOfWeek),
            getLoggedHabitIdsForRange().map { it.getOrNull() ?: emptyList() }
        ) { habits, dailyLogs ->
            val logMap = dailyLogs.associateBy { it.habitId }
            val mapped = habits.map { habit ->
                val log = logMap[habit.id]
                HabitWithDone(
                    habitEntity = habit,
                    isDone = log != null,
                    logId = log?.id,
                    created = log?.createdAt
                )
            }
            Result.success(mapped)
        }.catch { emit(Result.failure(it)) }
    }

    override fun getHabitWithIdFlow(id: Long): Flow<Result<HabitEntity>> {
        return habitDao.getHabitByIdFLow(id).map { Result.success(it) }.catch { emit(Result.failure(it)) }
    }
}