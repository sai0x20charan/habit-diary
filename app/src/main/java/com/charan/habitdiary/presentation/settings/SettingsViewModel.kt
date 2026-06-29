package com.charan.habitdiary.presentation.settings

import android.net.Uri
import androidx.biometric.BiometricManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charan.habitdiary.R
import com.charan.habitdiary.data.model.enums.ThemeOption
import com.charan.habitdiary.data.repository.BackupRepository
import com.charan.habitdiary.data.repository.DataStoreRepository
import com.charan.habitdiary.presentation.common.model.ToastMessage
import com.charan.habitdiary.presentation.settings.SettingsEffect.*
import com.charan.habitdiary.core.utils.GITHUB_URL
import com.charan.habitdiary.core.utils.PLAY_STORE_URL
import com.charan.habitdiary.core.utils.getAppVersionWithVersionCode
import com.charan.habitdiary.core.utils.isBiometricAvailable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.charan.habitdiary.core.notification.NotificationScheduler
import com.charan.habitdiary.core.utils.DateUtil.toFormattedString
import com.charan.habitdiary.core.utils.PermissionManager
import kotlinx.datetime.LocalTime
import kotlinx.coroutines.flow.first
import javax.inject.Inject
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStore : DataStoreRepository,
    private val backupRepository: BackupRepository,
    private val biometricManager : BiometricManager,
    private val notificationScheduler: NotificationScheduler,
    private val permissionManager: PermissionManager
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SettingsEffect>()
    val effect = _effect.asSharedFlow()

    init {
        observeSettingsDataStore()
        getAppVersion()
    }


    fun onEvent(event: SettingsEvent) {
        when(event){
            is SettingsEvent.OnThemeChange -> {
                changeTheme(event.theme)
            }
            is SettingsEvent.OnTimeFormatChange -> {
                changeTimeFormat(event.is24HourFormat)

            }

            is SettingsEvent.OnDynamicColorsChange -> {
                setDynamicColorsState(event.isEnabled)
            }

            SettingsEvent.OnAboutLibrariesClick -> {
                sendEffect(NavigateToLibrariesScreen)
            }
            SettingsEvent.OnBack -> {
                sendEffect(OnBack)
            }

            is SettingsEvent.BackupData -> {
                backupData(event.uri)

            }
            SettingsEvent.OnExportDataClick -> {
                sendEffect(LaunchCreateDocument(backupRepository.fileName))
            }

            SettingsEvent.OnImportDataClick -> {
                sendEffect(LaunchOpenDocument)

            }

            is SettingsEvent.RestoreBackup -> {
                importData(event.uri)
            }

            is SettingsEvent.OnUseSystemFontChange -> {
                handleUseSystemFont(event.useSystemFont)
            }

            is SettingsEvent.OnBiometricLockChange -> {
                handleBiometricLockChange(event.isEnabled)

            }

            is SettingsEvent.OnOpenSourceCodeClick -> {
                sendEffect(OpenUrl(GITHUB_URL))
            }

            SettingsEvent.OnSendFeedbackClick -> {
                sendEffect(LaunchSendFeedbackEmail)
            }

            SettingsEvent.OnRateAppClick -> {
                sendEffect(OpenUrl(PLAY_STORE_URL))
            }

            SettingsEvent.OnToggleChangeLogClick -> {
                handleChangeLogClick()
            }

            is SettingsEvent.OnDailyLogReminderToggle -> {
                handleDailyLogReminderToggle(event.isEnabled)
            }
            is SettingsEvent.OnDailyLogReminderTimeChange -> {
                handleDailyLogReminderTimeChange(event.time)
            }
            is SettingsEvent.OnToggleDailyLogTimeDialog -> {
                _state.update { it.copy(showDailyLogTimeDialog = event.show) }
            }
            is SettingsEvent.TogglePermissionRationale -> {
                _state.update { it.copy(showPermissionRationale = event.show) }
            }
            SettingsEvent.OpenPermissionSettings -> {
                permissionManager.openSettingsPermissionScreen()
            }
        }
    }

    private fun handleChangeLogClick() = viewModelScope.launch {
        _state.update {
            it.copy(
                showChangeLog = !it.showChangeLog
            )
        }
    }


    private fun handleBiometricLockChange(isEnabled : Boolean){
        if(isEnabled){
            checkIfBiometricIsAvailable()
        }else{
            updateBiometricPreference(false)
        }
    }

    private fun observeSettingsDataStore() = viewModelScope.launch {
        launch {
            combine(
                dataStore.getSystemFontState,
                dataStore.getIs24HourFormat,
                dataStore.getTheme,
                dataStore.getDynamicColorsState,
                dataStore.getBiometricLockEnabled
            ) { font, time, theme, dynamic, biometric ->
                _state.update { it.copy(
                    isSystemFontEnabled = font,
                    is24HourFormat = time,
                    selectedThemeOption = theme,
                    isDynamicColorsEnabled = dynamic,
                    isBiometricLockEnabled = biometric
                )}
            }.collect {}
        }
        launch {
            combine(
                dataStore.getDailyLogReminderEnabled,
                dataStore.getDailyLogReminderTime,
                dataStore.getIs24HourFormat
            ) { enabled, time, is24Hour ->
                _state.update { it.copy(
                    isDailyLogReminderEnabled = enabled && isNotificationPermissionGranted(),
                    dailyLogReminderTime = time,
                    formatedReminderTime = time.toFormattedString(is24Hour)
                )}
            }.collect {}
        }
    }

    private fun isNotificationPermissionGranted() : Boolean {
        return permissionManager.isNotificationPermissionGranted()
    }

    private fun handleDailyLogReminderToggle(isEnabled: Boolean) = viewModelScope.launch {
        if (isEnabled) {
            if (isNotificationPermissionGranted()) {
                dataStore.setDailyLogReminderEnabled(true)
                val time = dataStore.getDailyLogReminderTime.first()
                notificationScheduler.scheduleDailyLogReminder(time, true)
            } else {
                sendEffect(SettingsEffect.RequestNotificationPermission)
            }
        } else {
            dataStore.setDailyLogReminderEnabled(false)
            notificationScheduler.cancelDailyLogReminder()
        }
    }

    private fun handleDailyLogReminderTimeChange(time: LocalTime) = viewModelScope.launch {
        dataStore.setDailyLogReminderTime(time)
        val isReminderEnabled = _state.value.isDailyLogReminderEnabled
        if (isReminderEnabled) {
            notificationScheduler.scheduleDailyLogReminder(time, true)
        }
    }


    private fun handleUseSystemFont(useSystemFont : Boolean) = viewModelScope.launch {
        dataStore.setSystemFontState(useSystemFont)
    }

    private fun changeTimeFormat(is24HourFormat : Boolean) = viewModelScope.launch{
        dataStore.setIs24HourFormat(is24HourFormat)
    }


    private fun changeTheme(theme : ThemeOption) = viewModelScope.launch{
        dataStore.setTheme(
            theme
        )
    }

    private fun setDynamicColorsState(isEnabled : Boolean) = viewModelScope.launch {
        dataStore.setDynamicColorsState(isEnabled)
    }

    private fun checkIfBiometricIsAvailable() =viewModelScope.launch{
        when (biometricManager.isBiometricAvailable()) {
            true -> {
                _state.update {
                    it.copy(
                        isBiometricLockEnabled = true
                    )
                }
                updateBiometricPreference(true)
            }
            else -> {
                _state.update {
                    it.copy(
                        isBiometricLockEnabled = false
                    )
                }
                updateBiometricPreference(false)
                sendEffect(
                    ShowToast(ToastMessage.Res(R.string.biometric_unavailable))
                )

            }
        }
    }

    private fun updateBiometricPreference(isEnabled: Boolean) = viewModelScope.launch {
        dataStore.setBiometricLockEnabled(isEnabled)
    }

    private fun sendEffect(effect : SettingsEffect) = viewModelScope.launch{
            _effect.emit(effect)

    }

    private fun getAppVersion() {
        val appVersion = getAppVersionWithVersionCode()
        _state.update {
            it.copy(
                appVersion = appVersion
            )
        }
    }

    private fun backupData(uri: Uri) = viewModelScope.launch {
        _state.update {
            it.copy(isExporting = true)
        }
        try {
            val result = backupRepository.backupData(uri)
            result.onSuccess {
                sendEffect(SettingsEffect.ShowToast(ToastMessage.Res(R.string.backup_saved)))
            }.onFailure { exception ->
                sendEffect(SettingsEffect.ShowToast(exception.message?.let { ToastMessage.Text(it) } ?: ToastMessage.Res(R.string.an_error_occurred)))
            }
        } finally {
            _state.update {
                it.copy(isExporting = false)
            }
        }
    }

    private fun importData(uri: Uri) = viewModelScope.launch {
        _state.update {
            it.copy(isImporting = true)
        }
        try {
            val result = backupRepository.importData(uri)
            result.onSuccess {
                sendEffect(SettingsEffect.ShowToast(ToastMessage.Res(R.string.backup_restored)))
            }.onFailure { exception ->
                sendEffect(SettingsEffect.ShowToast(exception.message?.let { ToastMessage.Text(it) } ?: ToastMessage.Res(R.string.an_error_occurred)))
            }
        } finally {
            _state.update {
                it.copy(isImporting = false)
            }
        }
    }

}
