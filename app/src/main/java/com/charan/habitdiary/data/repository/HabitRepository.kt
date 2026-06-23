package com.charan.habitdiary.data.repository

import com.charan.habitdiary.data.local.entity.HabitEntity
import com.charan.habitdiary.data.local.model.HabitWithDone
import com.charan.habitdiary.core.utils.DateUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime

interface HabitRepository {

    suspend fun upsertHabit(habit: HabitEntity): Result<Long>

    fun getAllHabitsFlow(): Flow<Result<List<HabitEntity>>>

    suspend fun getAllHabits(): Result<List<HabitEntity>>

    fun getActiveHabits(): Flow<Result<List<HabitWithDone>>>

    suspend fun getHabitWithId(id: Long): Result<HabitEntity>

    suspend fun deleteHabit(id: Long): Result<Unit>

    suspend fun insertHabits(habits: List<HabitEntity>): Result<List<Long>>

    fun getTodayHabits(currentDayOfWeek: DayOfWeek = DateUtil.getCurrentDayOfWeek()): Flow<Result<List<HabitWithDone>>>

    fun getHabitWithIdFlow(id: Long): Flow<Result<HabitEntity>>
}