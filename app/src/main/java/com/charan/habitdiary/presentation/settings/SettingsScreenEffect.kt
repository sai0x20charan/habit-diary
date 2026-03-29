package com.charan.habitdiary.presentation.settings

import com.charan.habitdiary.presentation.common.model.ToastMessage

sealed interface SettingsScreenEffect {
    data object NavigateToLibrariesScreen : SettingsScreenEffect

    data object OnBack : SettingsScreenEffect

    data class LaunchCreateDocument(val fileName : String) : SettingsScreenEffect

    data class ShowToast(val message : ToastMessage) : SettingsScreenEffect

    data object LaunchOpenDocument : SettingsScreenEffect

    data class OpenUrl(val url : String) : SettingsScreenEffect

    data object LaunchSendFeedbackEmail : SettingsScreenEffect
}