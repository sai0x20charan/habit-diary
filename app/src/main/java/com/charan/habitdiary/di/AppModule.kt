package com.charan.habitdiary.di

import android.content.Context
import androidx.biometric.BiometricManager
import com.charan.habitdiary.appfunctions.AppFunctions
import com.charan.habitdiary.data.local.AppDatabase
import com.charan.habitdiary.data.local.dao.DailyLogDao
import com.charan.habitdiary.data.local.dao.DailyLogMediaDao
import com.charan.habitdiary.data.local.dao.HabitDao
import com.charan.habitdiary.data.local.entity.DailyLogMediaEntity
import com.charan.habitdiary.data.repository.BackupRepository
import com.charan.habitdiary.data.repository.DataStoreRepository
import com.charan.habitdiary.data.repository.FileRepository
import com.charan.habitdiary.data.repository.HabitLocalRepository
import com.charan.habitdiary.data.repository.impl.BackupRepositoryImpl
import com.charan.habitdiary.data.repository.impl.DateStoreRepositoryImpl
import com.charan.habitdiary.data.repository.impl.FileRepositoryImpl
import com.charan.habitdiary.data.repository.impl.HabitLocalRepositoryImpl
import com.charan.habitdiary.notification.NotificationHelper
import com.charan.habitdiary.notification.NotificationScheduler
import com.charan.habitdiary.utils.PermissionManager
import com.charan.habitdiary.widgets.HabitDiaryWidgetManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
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

    @Provides
    @Singleton
    fun provideHabitLocalRepository(
        habitDao: HabitDao,
        dailyLogDao: DailyLogDao,
        dailyLogMediaDao: DailyLogMediaDao
    ): HabitLocalRepository = HabitLocalRepositoryImpl(habitDao, dailyLogDao, dailyLogMediaDao)

    @Provides
    @Singleton
    fun provideFileRepository(
        @ApplicationContext context: Context
    ): FileRepository = FileRepositoryImpl(context)

    @Provides
    @Singleton
    fun permissionManager(
        @ApplicationContext context: Context
    ): PermissionManager = PermissionManager(context)


    @Provides
    @Singleton
    fun provideNotificationHelper(
        @ApplicationContext context: Context
    ) = NotificationHelper(context)

    @Provides
    @Singleton
    fun provideNotificationScheduler(
        @ApplicationContext context: Context
    ) = NotificationScheduler(context)

    @Provides
    @Singleton
    fun provideDataStoreRepository(
        @ApplicationContext context: Context
    ): DataStoreRepository = DateStoreRepositoryImpl(context)

    @Provides
    @Singleton
    fun provideBackupRepository(
        @ApplicationContext context: Context,
        habitLocalRepository: HabitLocalRepository,
        notificationScheduler: NotificationScheduler
    ) : BackupRepository= BackupRepositoryImpl(
        context,
        habitLocalRepository,
        notificationScheduler
    )

    @Provides
    @Singleton
    fun provideBiometricManager(
        @ApplicationContext context: Context
    ) : BiometricManager = BiometricManager.from(context)


    @Provides
    @Singleton
    fun provideWidgetManager(
        @ApplicationContext context : Context
    ) : HabitDiaryWidgetManager = HabitDiaryWidgetManager(context)


    @Provides
    @Singleton
    fun provideAppFunctions(
        habitLocalRepository: HabitLocalRepository
    ) : AppFunctions = AppFunctions(habitLocalRepository)
}