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

    suspend fun upsetHabit(habit: HabitEntity): Long

    suspend fun upsetDailyLog(
        dailyLog: DailyLogEntity,
        mediaEntity: List<DailyLogMediaEntity> = emptyList()
    )

    fun getAllHabitsFlow(): Flow<List<HabitEntity>>

    suspend fun getAllHabits(): List<HabitEntity>

    fun getAllDailyLogsFlow(): Flow<List<DailyLogEntity>>

    suspend fun getAllDailyLogs(): List<DailyLogEntity>

    fun getDailyLogsInRange(
        startOfDay: LocalDateTime = DateUtil.todayStartOfDay(),
        endOfDay: LocalDateTime = DateUtil.todayEndOfDay(),
        sortBy: DailyLogSortType = DailyLogSortType.NEWEST_FIRST
    ): Flow<List<DailyLogWithHabit>>

    fun getActiveHabits(): Flow<List<HabitWithDone>>

    suspend fun getDailyLogWithId(id: Long): DailyLogEntity

    suspend fun getDailyLogsWithHabitWithId(id: Long): DailyLogWithHabit

    suspend fun getHabitWithId(id: Long): HabitEntity

    suspend fun deleteDailyLog(id: Long)

    suspend fun deleteHabit(id: Long)

    fun getLoggedHabitIdsForRange(
        startOfDay: LocalDateTime = DateUtil.todayStartOfDay(),
        endOfDay: LocalDateTime = DateUtil.todayEndOfDay()
    ): Flow<List<DailyLogEntity>>

    suspend fun getLoggedHabitFromIdForRange(
        habitId: Long,
        startOfDay: LocalDateTime = DateUtil.todayStartOfDay(),
        endOfDay: LocalDateTime = DateUtil.todayEndOfDay()
    ): DailyLogEntity?


    suspend fun upsetDailyLogMediaEntities(mediaEntity: List<DailyLogMediaEntity>)

    suspend fun getAllMedia() : List<DailyLogMediaEntity>

    suspend fun insertDailyLogs(dailyLogs: List<DailyLogEntity>) : List<Long>

    suspend fun insertHabits(habits: List<HabitEntity>) : List<Long>

    fun getLoggedDatesInRange(
        start: LocalDateTime,
        end: LocalDateTime
    ): Flow<List<LocalDate>>

    fun getAllLogsWithHabitId(habitId: Long): Flow<List<DailyLogEntity>>

    fun getTodayHabits(currentDayOfWeek: DayOfWeek = DateUtil.getCurrentDayOfWeek()): Flow<List<HabitWithDone>>

    fun getHabitWithIdFlow(id: Long): Flow<HabitEntity>
}