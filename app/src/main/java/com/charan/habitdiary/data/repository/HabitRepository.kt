package com.charan.habitdiary.data.repository

import com.charan.habitdiary.data.local.entity.DailyLogEntity
import com.charan.habitdiary.data.local.entity.DailyLogMediaEntity
import com.charan.habitdiary.data.local.entity.HabitEntity
import com.charan.habitdiary.data.local.model.DailyLogWithHabit
import com.charan.habitdiary.data.local.model.HabitWithDone
import com.charan.habitdiary.data.model.enums.DailyLogSortType
import com.charan.habitdiary.utils.DateUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

interface HabitRepository {

    suspend fun upsetHabit(habit: HabitEntity): Result<Long>

    suspend fun upsetDailyLog(
        dailyLog: DailyLogEntity,
        mediaEntity: List<DailyLogMediaEntity> = emptyList()
    ): Result<Unit>

    fun getAllHabitsFlow(): Flow<Result<List<HabitEntity>>>

    suspend fun getAllHabits(): Result<List<HabitEntity>>

    fun getAllDailyLogsFlow(): Flow<Result<List<DailyLogEntity>>>

    suspend fun getAllDailyLogs(): Result<List<DailyLogEntity>>

    fun getDailyLogsInRange(
        startOfDay: LocalDateTime = DateUtil.todayStartOfDay(),
        endOfDay: LocalDateTime = DateUtil.todayEndOfDay(),
        sortBy: DailyLogSortType = DailyLogSortType.NEWEST_FIRST
    ): Flow<Result<List<DailyLogWithHabit>>>

    fun getActiveHabits(): Flow<Result<List<HabitWithDone>>>

    suspend fun getDailyLogWithId(id: Long): Result<DailyLogEntity>

    suspend fun getDailyLogsWithHabitWithId(id: Long): Result<DailyLogWithHabit>

    suspend fun getHabitWithId(id: Long): Result<HabitEntity>

    suspend fun deleteDailyLog(id: Long): Result<Unit>

    suspend fun deleteHabit(id: Long): Result<Unit>

    fun getLoggedHabitIdsForRange(
        startOfDay: LocalDateTime = DateUtil.todayStartOfDay(),
        endOfDay: LocalDateTime = DateUtil.todayEndOfDay()
    ): Flow<Result<List<DailyLogEntity>>>

    suspend fun getLoggedHabitFromIdForRange(
        habitId: Long,
        startOfDay: LocalDateTime = DateUtil.todayStartOfDay(),
        endOfDay: LocalDateTime = DateUtil.todayEndOfDay()
    ): Result<DailyLogEntity?>


    suspend fun upsetDailyLogMediaEntities(mediaEntity: List<DailyLogMediaEntity>): Result<Unit>

    suspend fun getAllMedia() : Result<List<DailyLogMediaEntity>>

    suspend fun insertDailyLogs(dailyLogs: List<DailyLogEntity>) : Result<List<Long>>

    suspend fun insertHabits(habits: List<HabitEntity>) : Result<List<Long>>

    fun getLoggedDatesInRange(
        start: LocalDateTime,
        end: LocalDateTime
    ): Flow<Result<List<LocalDate>>>

    fun getAllLogsWithHabitId(habitId: Long): Flow<Result<List<DailyLogEntity>>>

    fun getTodayHabits(currentDayOfWeek: DayOfWeek = DateUtil.getCurrentDayOfWeek()): Flow<Result<List<HabitWithDone>>>

    fun getHabitWithIdFlow(id: Long): Flow<Result<HabitEntity>>
}