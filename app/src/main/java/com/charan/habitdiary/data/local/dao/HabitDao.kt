package com.charan.habitdiary.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.charan.habitdiary.data.local.entity.HabitEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.DayOfWeek

@Dao
interface HabitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsetHabit(habit: HabitEntity) : Long

    @Insert
    fun insertHabits(habits: List<HabitEntity>) : List<Long>

    @Query("SELECT * FROM habit_entity ORDER BY createdAt DESC")
    fun getAllHabitsFlow(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habit_entity ORDER BY createdAt DESC")
    fun getAllHabits(): List<HabitEntity>

    @Query("""
    SELECT * FROM habit_entity
    WHERE isDeleted = 0
    AND habitFrequency LIKE '%' || :currentDayOfWeek || '%'
    ORDER BY habitTime
""")
    fun getTodayHabitsFlow(
        currentDayOfWeek: DayOfWeek
    ): Flow<List<HabitEntity>>

    @Query("""
    SELECT * FROM habit_entity
    WHERE isDeleted = 0
    AND habitFrequency LIKE '%' || :currentDayOfWeek || '%'
    ORDER BY habitTime
""")
    fun getTodayHabits(
        currentDayOfWeek: DayOfWeek
    ): List<HabitEntity>





    @Query("SELECT * FROM habit_entity WHERE id = :id")
    fun getHabitWithId(id: Long): HabitEntity

    @Query("UPDATE habit_entity SET isDeleted = 1 WHERE id = :id")
    fun deleteHabit(id: Long)

    @Query("SELECT * FROM habit_entity WHERE id = :id")
    fun getHabitByIdFLow(id: Long): Flow<HabitEntity>

    @Query("SELECT * FROM habit_entity WHERE isDeleted = 0 ORDER BY habitTime")
    fun getActiveHabitsFlow(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habit_entity WHERE habitName = :habitName AND isDeleted = 0")
    fun getHabitByName(habitName: String): HabitEntity?


}