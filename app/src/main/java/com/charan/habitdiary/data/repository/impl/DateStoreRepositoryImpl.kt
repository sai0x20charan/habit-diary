package com.charan.habitdiary.data.repository.impl

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.charan.habitdiary.data.model.enums.DailyLogSortType
import com.charan.habitdiary.data.model.enums.HabitSortType
import com.charan.habitdiary.data.model.enums.ThemeOption
import com.charan.habitdiary.data.repository.DataStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DateStoreRepositoryImpl(
    private val context : Context
) : DataStoreRepository {
    companion object {
        private val Context.dataStore by preferencesDataStore("app_preferences")
        private val KEY_THEME = stringPreferencesKey("theme_option")

        private val KEY_DYNAMIC_COLORS = booleanPreferencesKey("dynamic_colors")

        private val KEY_IS_24_HOUR_FORMAT = booleanPreferencesKey("is_24_hour_format")

        private val KEY_ON_BOARDING_COMPLETED = booleanPreferencesKey("on_boarding_completed")

        private val KEY_SYSTEM_FONT = booleanPreferencesKey("system_font")

        private val KEY_HABIT_SORT_TYPE = stringPreferencesKey("habit_sort_type")

        private val KEY_DAILY_LOG_SORT_TYPE = stringPreferencesKey("daily_log_sort_type")

        private val KEY_BIOMETRIC_LOCK = booleanPreferencesKey("biometric_lock")
    }


    override suspend fun setTheme(theme: ThemeOption) {
        context.dataStore.edit { preferences ->
            preferences[KEY_THEME] = theme.name
        }

    }

    override val getTheme: Flow<ThemeOption>
        get() = context.dataStore.data.map { pref ->
            val themeName = pref[KEY_THEME] ?: ThemeOption.SYSTEM_DEFAULT.name
            ThemeOption.valueOf(themeName)
        }

    override suspend fun setDynamicColorsState(isEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_DYNAMIC_COLORS] = isEnabled
        }
    }

    override val getDynamicColorsState: Flow<Boolean>
        get() = context.dataStore.data.map { pref ->
            val isEnabledString = pref[KEY_DYNAMIC_COLORS] ?: true
            isEnabledString

        }

    override suspend fun setIs24HourFormat(is24HourFormat: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_IS_24_HOUR_FORMAT] = is24HourFormat
        }

    }

    override val getIs24HourFormat: Flow<Boolean>
        get() = context.dataStore.data.map { pref ->
            val is24HourFormat = pref[KEY_IS_24_HOUR_FORMAT] ?: false
            is24HourFormat
        }

    override suspend fun setOnBoardingCompleted(isCompleted: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_ON_BOARDING_COMPLETED] = isCompleted
        }
    }

    override val getOnBoardingCompleted: Flow<Boolean>
        get() = context.dataStore.data.map { pref ->
            val isCompleted = pref[KEY_ON_BOARDING_COMPLETED] ?: false
            isCompleted
        }

    override suspend fun setSystemFontState(useSystemFont: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_SYSTEM_FONT] = useSystemFont
        }
    }

    override val getSystemFontState: Flow<Boolean>
        get() = context.dataStore.data.map { pref ->
            val useSystemFont = pref[KEY_SYSTEM_FONT] ?: true
            useSystemFont
        }

    override val habitSortType: Flow<HabitSortType>
        get() = context.dataStore.data.map { pref ->
            val sortTypeString = pref[KEY_HABIT_SORT_TYPE] ?: HabitSortType.ALL_HABITS.name
            HabitSortType.valueOf(sortTypeString)
        }

    override suspend fun setHabitSortType(sortType: HabitSortType) {
        context.dataStore.edit { preferences ->
            preferences[KEY_HABIT_SORT_TYPE] = sortType.name
        }
    }

    override val dailyLogSortType: Flow<DailyLogSortType>
        get() = context.dataStore.data.map {  pref->
            val sortTypeString = pref[KEY_DAILY_LOG_SORT_TYPE] ?: DailyLogSortType.NEWEST_FIRST.name
            DailyLogSortType.valueOf(sortTypeString)

        }

    override suspend fun setDailyLogSortType(sortType: DailyLogSortType) {
        context.dataStore.edit { preferences ->
            preferences[KEY_DAILY_LOG_SORT_TYPE] = sortType.name
        }
    }

    override suspend fun setBiometricLockEnabled(isEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_BIOMETRIC_LOCK] = isEnabled
        }
    }

    override val getBiometricLockEnabled: Flow<Boolean>
        get() = context.dataStore.data.map { pref ->
            pref[KEY_BIOMETRIC_LOCK] ?: false
        }
}
