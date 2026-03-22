package com.charan.habitdiary.presentation.settings

import com.charan.habitdiary.data.model.enums.ThemeOption

data class SettingsState(
    val selectedThemeOption: ThemeOption = ThemeOption.SYSTEM_DEFAULT,
    val isDynamicColorsEnabled : Boolean = true,
    val isSystemFontEnabled : Boolean = true,
    val is24HourFormat: Boolean = false,
    val appVersion : String = "",
    val isExporting: Boolean = false,
    val isImporting : Boolean = false,
    val isBiometricLockEnabled : Boolean = false
)
