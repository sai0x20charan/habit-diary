package com.charan.habitdiary.presentation.settings

import android.net.Uri
import com.charan.habitdiary.data.model.enums.ThemeOption
import kotlinx.datetime.LocalTime

sealed class SettingsEvent {
    data class OnThemeChange(val theme : ThemeOption) : SettingsEvent()
    data class OnTimeFormatChange(val is24HourFormat : Boolean) : SettingsEvent()

    data class OnDynamicColorsChange(val isEnabled : Boolean) : SettingsEvent()

    data class OnUseSystemFontChange(val useSystemFont : Boolean) : SettingsEvent()

    data class OnBiometricLockChange(val isEnabled : Boolean) : SettingsEvent()

    data object OnAboutLibrariesClick : SettingsEvent()

    data object OnBack : SettingsEvent()

    data object OnExportDataClick : SettingsEvent()

    data class BackupData(val uri : Uri) : SettingsEvent()

    data class RestoreBackup(val uri : Uri) : SettingsEvent()

    data object OnImportDataClick : SettingsEvent()

    data object OnOpenSourceCodeClick : SettingsEvent()

    data object OnSendFeedbackClick : SettingsEvent()

    data object OnRateAppClick : SettingsEvent()

    data object OnToggleChangeLogClick : SettingsEvent()

    data class OnDailyLogReminderToggle(val isEnabled: Boolean) : SettingsEvent()
    data class OnDailyLogReminderTimeChange(val time: LocalTime) : SettingsEvent()
    data class OnToggleDailyLogTimeDialog(val show: Boolean) : SettingsEvent()
    data class TogglePermissionRationale(val show: Boolean) : SettingsEvent()
    data object OpenPermissionSettings : SettingsEvent()
}
