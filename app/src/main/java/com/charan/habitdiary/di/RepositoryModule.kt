package com.charan.habitdiary.di

import com.charan.habitdiary.data.repository.BackupRepository
import com.charan.habitdiary.data.repository.DataStoreRepository
import com.charan.habitdiary.data.repository.DiaryRepository
import com.charan.habitdiary.data.repository.FileRepository
import com.charan.habitdiary.data.repository.HabitRepository
import com.charan.habitdiary.data.repository.impl.BackupRepositoryImpl
import com.charan.habitdiary.data.repository.impl.DataStoreRepositoryImpl
import com.charan.habitdiary.data.repository.impl.DiaryRepositoryImpl
import com.charan.habitdiary.data.repository.impl.FileRepositoryImpl
import com.charan.habitdiary.data.repository.impl.HabitRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindHabitRepository(
        impl: HabitRepositoryImpl
    ): HabitRepository

    @Binds
    @Singleton
    abstract fun bindDiaryRepository(
        impl: DiaryRepositoryImpl
    ): DiaryRepository

    @Binds
    @Singleton
    abstract fun bindFileRepository(
        impl: FileRepositoryImpl
    ): FileRepository

    @Binds
    @Singleton
    abstract fun bindDataStoreRepository(
        impl: DataStoreRepositoryImpl
    ): DataStoreRepository

    @Binds
    @Singleton
    abstract fun bindBackupRepository(
        impl: BackupRepositoryImpl
    ): BackupRepository
}
