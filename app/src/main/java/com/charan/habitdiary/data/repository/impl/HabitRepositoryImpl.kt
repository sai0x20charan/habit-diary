package com.charan.habitdiary.data.repository.impl

import com.charan.habitdiary.data.local.dao.HabitDao
import com.charan.habitdiary.data.local.entity.HabitEntity
import com.charan.habitdiary.data.local.model.HabitWithDone
import com.charan.habitdiary.data.repository.DiaryRepository
import com.charan.habitdiary.data.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime
import javax.inject.Inject
import com.charan.habitdiary.core.utils.suspendRunCatching

class HabitRepositoryImpl @Inject constructor(
    private val habitDao: HabitDao,
    private val diaryRepository: DiaryRepository
) : HabitRepository {

    override suspend fun upsertHabit(habit: HabitEntity): Result<Long> = suspendRunCatching {
        habitDao.upsertHabit(habit)
    }

    override fun getAllHabitsFlow(): Flow<Result<List<HabitEntity>>> {
        return habitDao.getAllHabitsFlow()
            .map { Result.success(it) }
            .catch { emit(Result.failure(it)) }
    }

    override suspend fun getAllHabits(): Result<List<HabitEntity>> = suspendRunCatching {
        habitDao.getAllHabits()
    }

    override fun getActiveHabits(): Flow<Result<List<HabitWithDone>>> {
        return combine(
            habitDao.getActiveHabitsFlow(),
            diaryRepository.getLoggedHabitIdsForRange()
        ) { habits, dailyLogsResult ->
            dailyLogsResult.fold(
                onSuccess = { dailyLogs ->
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
                },
                onFailure = {
                    Result.failure(it)
                }
            )
        }.catch { emit(Result.failure(it)) }
    }

    override suspend fun getHabitWithId(id: Long): Result<HabitEntity> = suspendRunCatching {
        habitDao.getHabitWithId(id)
    }

    override suspend fun deleteHabit(id: Long): Result<Unit> = suspendRunCatching {
        habitDao.deleteHabit(id)
    }

    override suspend fun insertHabits(habits: List<HabitEntity>): Result<List<Long>> = suspendRunCatching {
        habitDao.insertHabits(habits)
    }

    override fun getTodayHabits(currentDayOfWeek: DayOfWeek): Flow<Result<List<HabitWithDone>>> {
        return combine(
            habitDao.getTodayHabits(currentDayOfWeek),
            diaryRepository.getLoggedHabitIdsForRange()
        ) { habits, dailyLogsResult ->
            dailyLogsResult.fold(
                onSuccess = { dailyLogs ->
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
                },
                onFailure = {
                    Result.failure(it)
                }
            )
        }.catch { emit(Result.failure(it)) }
    }

    override fun getHabitWithIdFlow(id: Long): Flow<Result<HabitEntity>> {
        return habitDao.getHabitByIdFLow(id)
            .map { Result.success(it) }
            .catch { emit(Result.failure(it)) }
    }
}