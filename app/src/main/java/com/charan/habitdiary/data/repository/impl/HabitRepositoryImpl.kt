package com.charan.habitdiary.data.repository.impl

import com.charan.habitdiary.data.local.dao.DailyLogDao
import com.charan.habitdiary.data.local.dao.HabitDao
import com.charan.habitdiary.data.local.entity.DailyLogEntity
import com.charan.habitdiary.data.local.entity.HabitEntity
import com.charan.habitdiary.data.local.model.HabitWithDone
import com.charan.habitdiary.data.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime
import javax.inject.Inject

class HabitRepositoryImpl @Inject constructor(
    private val habitDao: HabitDao,
    private val dailyLogDao: DailyLogDao
) : HabitRepository {

    override suspend fun upsetHabit(habit: HabitEntity): Result<Long> = runCatching {
        habitDao.upsetHabit(habit)
    }

    override fun getAllHabitsFlow(): Flow<Result<List<HabitEntity>>> {
        return habitDao.getAllHabitsFlow()
            .map { Result.success(it) }
            .catch { emit(Result.failure(it)) }
    }

    override suspend fun getAllHabits(): Result<List<HabitEntity>> = runCatching {
        habitDao.getAllHabits()
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

    override suspend fun getHabitWithId(id: Long): Result<HabitEntity> = runCatching {
        habitDao.getHabitWithId(id)
    }

    override suspend fun deleteHabit(id: Long): Result<Unit> = runCatching {
        habitDao.deleteHabit(id)
    }

    private fun getLoggedHabitIdsForRange(
        startOfDay: LocalDateTime = com.charan.habitdiary.utils.DateUtil.todayStartOfDay(),
        endOfDay: LocalDateTime = com.charan.habitdiary.utils.DateUtil.todayEndOfDay()
    ): Flow<Result<List<DailyLogEntity>>> {
        return dailyLogDao.getLoggedHabitIdsForToday(startOfDay, endOfDay)
            .map { Result.success(it) }
            .catch { emit(Result.failure(it)) }
    }

    override suspend fun insertHabits(habits: List<HabitEntity>): Result<List<Long>> = runCatching {
        habitDao.insertHabits(habits)
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
        return habitDao.getHabitByIdFLow(id)
            .map { Result.success(it) }
            .catch { emit(Result.failure(it)) }
    }
}