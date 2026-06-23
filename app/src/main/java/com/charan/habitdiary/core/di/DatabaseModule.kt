package com.charan.habitdiary.core.di

import android.content.Context
import androidx.room.Room
import com.charan.habitdiary.data.local.AppDatabase
import com.charan.habitdiary.data.local.MIGRATION_1_2
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    @Provides
    @Singleton
    fun provideHabitDao(database: AppDatabase) = database.habitDao()

    @Provides
    @Singleton
    fun provideDailyLogDao(database: AppDatabase) = database.dailyLogDao()

    @Provides
    @Singleton
    fun provideDailyLogMediaDao(database: AppDatabase) = database.dailyLogMediaEntityDao()
}
