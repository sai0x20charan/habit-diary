package com.charan.habitdiary.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.charan.habitdiary.data.local.entity.DailyLogMediaEntity

@Dao
interface DailyLogMediaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedia(media : DailyLogMediaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedia(mediaList : List<DailyLogMediaEntity>)

    @Upsert
    suspend fun upsertMedia(media : List<DailyLogMediaEntity>)

    @Query("SELECT * FROM daily_log_media_entity")
    suspend fun getAllMedia() : List<DailyLogMediaEntity>
}