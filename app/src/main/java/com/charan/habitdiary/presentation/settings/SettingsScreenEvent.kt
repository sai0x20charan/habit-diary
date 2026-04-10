package com.charan.habitdiary.presentation.settings

import android.net.Uri
import com.charan.habitdiary.data.model.enums.ThemeOption

sealed class SettingsScreenEvent {
    data class OnThemeChange(val theme : ThemeOption) : SettingsScreenEvent()
    data class OnTimeFormatChange(val is24HourFormat : Boolean) : SettingsScreenEvent()

    data class OnDynamicColorsChange(val isEnabled : Boolean) : SettingsScreenEvent()

    data class OnUseSystemFontChange(val useSystemFont : Boolean) : SettingsScreenEvent()

    data class OnBiometricLockChange(val isEnabled : Boolean) : SettingsScreenEvent()

    data object OnAboutLibrariesClick : SettingsScreenEvent()

    data object OnBack : SettingsScreenEvent()

    data object OnExportDataClick : SettingsScreenEvent()

    data class BackupData(val uri : Uri) : SettingsScreenEvent()

    data class RestoreBackup(val uri : Uri) : SettingsScreenEvent()

    data object OnImportDataClick : SettingsScreenEvent()

    data object OnOpenSourceCodeClick : SettingsScreenEvent()

    data object OnSendFeedbackClick : SettingsScreenEvent()

    data object OnRateAppClick : SettingsScreenEvent()

    data object OnToggleChangeLogClick : SettingsScreenEvent()
}
