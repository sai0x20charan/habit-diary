package com.charan.habitdiary.data.repository

import com.charan.habitdiary.data.model.enums.DailyLogSortType
import com.charan.habitdiary.data.model.enums.HabitSortType
import com.charan.habitdiary.data.model.enums.ThemeOption
import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {

    suspend fun setTheme(theme : ThemeOption)

    val getTheme : Flow<ThemeOption>

    suspend fun setDynamicColorsState(isEnabled : Boolean)

    val getDynamicColorsState : Flow<Boolean>

    suspend fun setIs24HourFormat(is24HourFormat : Boolean)

    val getIs24HourFormat : Flow<Boolean>

    suspend fun setOnBoardingCompleted(isCompleted : Boolean)

    val getOnBoardingCompleted : Flow<Boolean>

    suspend fun setSystemFontState(useSystemFont : Boolean)

    val getSystemFontState : Flow<Boolean>

    val habitSortType : Flow<HabitSortType>

    suspend fun setHabitSortType(sortType : HabitSortType)

    val dailyLogSortType : Flow<DailyLogSortType>

    suspend fun setDailyLogSortType(sortType : DailyLogSortType)

    suspend fun setBiometricLockEnabled(isEnabled : Boolean)

    val getBiometricLockEnabled : Flow<Boolean>
}
