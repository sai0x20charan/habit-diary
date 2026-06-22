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

    override suspend fun upsetHabit(habit: HabitEntity) : Long {
        return habitDao.upsetHabit(habit)

    }

    override suspend fun upsetDailyLog(
        dailyLog: DailyLogEntity,
        mediaEntity : List<DailyLogMediaEntity>
    ) {
        val id = dailyLogDao.upsetDailyLog(dailyLog)
        if(mediaEntity.isNotEmpty()){
            val mediaEntity = mediaEntity.map { it.copy(dailyLogId = id) }
            dailyLogMediaDao.upsertMedia(mediaEntity)
        }
    }

    override fun getAllHabitsFlow(): Flow<List<HabitEntity>> {
        return habitDao.getAllHabitsFlow()
    }

    override suspend fun getAllHabits(): List<HabitEntity> {
        return habitDao.getAllHabits()
    }

    override fun getAllDailyLogsFlow(): Flow<List<DailyLogEntity>> {
        return dailyLogDao.getAllDailyLogsFlow()
    }
    override suspend fun getAllDailyLogs(): List<DailyLogEntity> {
        return dailyLogDao.getAllDailyLogs()
    }

    override fun getDailyLogsInRange(
        startOfDay: LocalDateTime,
        endOfDay: LocalDateTime,
        sortBy : DailyLogSortType
    ): Flow<List<DailyLogWithHabit>> {
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
            logs.map { log ->
                log.copy(
                    mediaEntities = log.mediaEntities.filter { !it.isDeleted }
                )
            }
        }
    }



    override fun getActiveHabits(): Flow<List<HabitWithDone>> {
        return combine(
            habitDao.getActiveHabitsFlow(),
            getLoggedHabitIdsForRange()
        ) { habits, dailyLogs ->
            val logMap = dailyLogs.associateBy { it.habitId }
            habits.map { habit ->
                val log = logMap[habit.id]
                HabitWithDone(
                    habitEntity = habit,
                    isDone = log != null,
                    logId = log?.id,
                    created = log?.createdAt
                )
            }
        }


    }


    override suspend fun getDailyLogWithId(id: Long): DailyLogEntity {
        return dailyLogDao.getDailyLogWithId(id)
    }

    override suspend fun getDailyLogsWithHabitWithId(id: Long): DailyLogWithHabit {
        return dailyLogDao.getDailyLogsWithHabitWithId(id)
    }

    override suspend fun getHabitWithId(id: Long): HabitEntity {
        return habitDao.getHabitWithId(id)
    }

    override suspend fun deleteDailyLog(id: Long) {
        dailyLogDao.deleteDailyLog(id)

    }

    override suspend fun deleteHabit(id: Long) {
        habitDao.deleteHabit(id)
    }

    override fun getLoggedHabitIdsForRange(startOfDay: LocalDateTime,endOfDay: LocalDateTime): Flow<List<DailyLogEntity>> {
        return dailyLogDao.getLoggedHabitIdsForToday(startOfDay,endOfDay)
    }

    override suspend fun getLoggedHabitFromIdForRange(
        habitId: Long,
        startOfDay: LocalDateTime,
        endOfDay: LocalDateTime
    ): DailyLogEntity? {
        return dailyLogDao.getLoggedHabitFromIdForRange(habitId, startOfDay, endOfDay)
    }

    override suspend fun upsetDailyLogMediaEntities(mediaEntity: List<DailyLogMediaEntity>) {
        dailyLogMediaDao.upsertMedia(mediaEntity)
    }

    override fun getLoggedDatesInRange(
        start: LocalDateTime,
        end: LocalDateTime
    ): Flow<List<LocalDate>> {
        return dailyLogDao.getLoggedDatesInRange(start,end)
    }

    override suspend fun getAllMedia(): List<DailyLogMediaEntity> {
        return dailyLogMediaDao.getAllMedia()
    }

    override suspend fun insertDailyLogs(dailyLogs: List<DailyLogEntity>): List<Long> {
        return dailyLogDao.insertDailyLogs(dailyLogs)
    }
    override suspend fun insertHabits(habits: List<HabitEntity>) : List<Long> {
        return habitDao.insertHabits(habits)
    }

    override fun getAllLogsWithHabitId(habitId: Long): Flow<List<DailyLogEntity>> {
        return dailyLogDao.getAllLogsForHabitId(habitId)
    }

    override fun getTodayHabits(currentDayOfWeek: DayOfWeek): Flow<List<HabitWithDone>> {
        return combine(
            habitDao.getTodayHabits(currentDayOfWeek),
            getLoggedHabitIdsForRange()
        ) { habits, dailyLogs ->
            val logMap = dailyLogs.associateBy { it.habitId }
            habits.map { habit ->
                val log = logMap[habit.id]
                HabitWithDone(
                    habitEntity = habit,
                    isDone = log != null,
                    logId = log?.id,
                    created = log?.createdAt
                )
            }
        }
    }

    override fun getHabitWithIdFlow(id: Long): Flow<HabitEntity> {
        return habitDao.getHabitByIdFLow(id)
    }
}