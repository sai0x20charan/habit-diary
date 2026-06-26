package com.charan.habitdiary.presentation.settings

import com.charan.habitdiary.presentation.common.model.ToastMessage

sealed interface SettingsEffect {
    data object NavigateToLibrariesScreen : SettingsEffect

    data object OnBack : SettingsEffect

    data class LaunchCreateDocument(val fileName : String) : SettingsEffect

    data class ShowToast(val message : ToastMessage) : SettingsEffect

    data object LaunchOpenDocument : SettingsEffect

    data class OpenUrl(val url : String) : SettingsEffect

    data object LaunchSendFeedbackEmail : SettingsEffect
}