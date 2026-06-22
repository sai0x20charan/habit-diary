package com.charan.habitdiary.data.repository

import com.charan.habitdiary.data.local.entity.DailyLogEntity
import com.charan.habitdiary.data.local.entity.DailyLogMediaEntity
import com.charan.habitdiary.data.local.model.DailyLogWithHabit
import com.charan.habitdiary.data.model.enums.DailyLogSortType
import com.charan.habitdiary.utils.DateUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

interface DiaryRepository {

    suspend fun upsetDailyLog(
        dailyLog: DailyLogEntity,
        mediaEntity: List<DailyLogMediaEntity> = emptyList()
    ): Result<Unit>

    fun getAllDailyLogsFlow(): Flow<Result<List<DailyLogEntity>>>

    suspend fun getAllDailyLogs(): Result<List<DailyLogEntity>>

    fun getDailyLogsInRange(
        startOfDay: LocalDateTime = DateUtil.todayStartOfDay(),
        endOfDay: LocalDateTime = DateUtil.todayEndOfDay(),
        sortBy: DailyLogSortType = DailyLogSortType.NEWEST_FIRST
    ): Flow<Result<List<DailyLogWithHabit>>>

    suspend fun getDailyLogWithId(id: Long): Result<DailyLogEntity>

    suspend fun getDailyLogsWithHabitWithId(id: Long): Result<DailyLogWithHabit>

    suspend fun deleteDailyLog(id: Long): Result<Unit>

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

    suspend fun getAllMedia(): Result<List<DailyLogMediaEntity>>

    suspend fun insertDailyLogs(dailyLogs: List<DailyLogEntity>): Result<List<Long>>

    fun getLoggedDatesInRange(
        start: LocalDateTime,
        end: LocalDateTime
    ): Flow<Result<List<LocalDate>>>

    fun getAllLogsWithHabitId(habitId: Long): Flow<Result<List<DailyLogEntity>>>
}
