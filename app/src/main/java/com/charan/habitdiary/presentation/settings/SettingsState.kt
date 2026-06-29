package com.charan.habitdiary.presentation.settings

import com.charan.habitdiary.data.model.enums.ThemeOption
import kotlinx.datetime.LocalTime

data class SettingsState(
    val selectedThemeOption: ThemeOption = ThemeOption.SYSTEM_DEFAULT,
    val isDynamicColorsEnabled : Boolean = true,
    val isSystemFontEnabled : Boolean = true,
    val is24HourFormat: Boolean = false,
    val appVersion : String = "",
    val isExporting: Boolean = false,
    val isImporting : Boolean = false,
    val isBiometricLockEnabled : Boolean = false,
    val showChangeLog : Boolean = false,
    val isDailyLogReminderEnabled: Boolean = false,
    val dailyLogReminderTime: LocalTime = LocalTime(20, 0),
    val formatedReminderTime : String = "20:00",
    val showDailyLogTimeDialog: Boolean = false,
    val showPermissionRationale: Boolean = false
)
