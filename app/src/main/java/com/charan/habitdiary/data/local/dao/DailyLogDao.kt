package com.charan.habitdiary.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.charan.habitdiary.data.local.entity.DailyLogEntity
import com.charan.habitdiary.data.local.model.DailyLogWithHabit
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

@Dao
interface DailyLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsetDailyLog(dailyLog: DailyLogEntity) : Long

    @Update
    fun updateDailyLog(dailyLog: DailyLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDailyLogs(dailyLogs: List<DailyLogEntity>) : List<Long>

    @Query("SELECT * FROM daily_log_entity ORDER BY createdAt DESC")
    fun getAllDailyLogsFlow(): Flow<List<DailyLogEntity>>

    @Query("SELECT * FROM daily_log_entity ORDER BY createdAt DESC")
    fun getAllDailyLogs(): List<DailyLogEntity>

    @Transaction
    @Query("SELECT * FROM daily_log_entity WHERE createdAt >= :startOfDay and createdAt <= :endOfDay and isDeleted = 0 ORDER BY createdAt DESC")
    fun getDailyLogsInRangeNewestFirst(startOfDay: LocalDateTime , endOfDay : LocalDateTime): Flow<List<DailyLogWithHabit>>

    @Transaction
    @Query("SELECT * FROM daily_log_entity WHERE createdAt >= :startOfDay and createdAt <= :endOfDay and isDeleted = 0 ORDER BY createdAt ASC")
    fun getDailyLogsInRangeOldestFirst(startOfDay: LocalDateTime , endOfDay : LocalDateTime): Flow<List<DailyLogWithHabit>>

    @Query("SELECT * FROM daily_log_entity WHERE id = :id")
    fun getDailyLogWithId(id: Long): DailyLogEntity

    @Transaction
    @Query("SELECT * FROM daily_log_entity WHERE id = :id")
    fun getDailyLogsWithHabitWithId(id: Long): DailyLogWithHabit

    @Query("UPDATE daily_log_entity SET isDeleted = 1 WHERE id = :id")
    fun deleteDailyLog(id: Long)

    @Query("SELECT * FROM daily_log_entity WHERE createdAt >= :startOfDay and createdAt <= :endOfDay AND isDeleted = 0")
    fun getLoggedHabitIdsForTodayFlow(startOfDay: LocalDateTime, endOfDay : LocalDateTime): Flow<List<DailyLogEntity>>

    @Query("SELECT * FROM daily_log_entity WHERE createdAt >= :startOfDay and createdAt <= :endOfDay AND isDeleted = 0")
    fun getLoggedHabitIdsForToday(startOfDay: LocalDateTime, endOfDay : LocalDateTime): List<DailyLogEntity>

    @Query("SELECT * FROM daily_log_entity WHERE habitId = :habitId AND createdAt >= :startOfDay and createdAt <= :endOfDay AND isDeleted = 0 LIMIT 1")
    fun getLoggedHabitFromIdForRange(habitId : Long,startOfDay: LocalDateTime,endOfDay : LocalDateTime): DailyLogEntity?

    @Query("SELECT DISTINCT createdAt FROM daily_log_entity WHERE createdAt >= :start and createdAt <= :end and isDeleted = 0")
    fun getLoggedDatesInRange(start: LocalDateTime, end: LocalDateTime): Flow<List<LocalDate>>

    @Query("""
    SELECT *
    FROM daily_log_entity
    WHERE habitId = :habitId
      AND isDeleted = 0
    ORDER BY createdAt DESC
""")
    fun getAllLogsForHabitId(habitId: Long): Flow<List<DailyLogEntity>>



}