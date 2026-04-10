package com.charan.habitdiary.presentation.settings

import android.net.Uri
import androidx.biometric.BiometricManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charan.habitdiary.BuildConfig
import com.charan.habitdiary.R
import com.charan.habitdiary.data.model.enums.ThemeOption
import com.charan.habitdiary.data.repository.BackupRepository
import com.charan.habitdiary.data.repository.DataStoreRepository
import com.charan.habitdiary.presentation.common.model.ToastMessage
import com.charan.habitdiary.presentation.settings.SettingsScreenEffect.*
import com.charan.habitdiary.utils.GITHUB_URL
import com.charan.habitdiary.utils.PLAY_STORE_URL
import com.charan.habitdiary.utils.ProcessState
import com.charan.habitdiary.utils.getAppVersionWithVersionCode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStore : DataStoreRepository,
    private val backupRepository: BackupRepository,
    private val biometricManager : BiometricManager
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SettingsScreenEffect>()
    val effect = _effect.asSharedFlow()

    init {
        observeSettingsDataStore()
        getAppVersion()
    }


    fun onEvent(event: SettingsScreenEvent) {
        when(event){
            is SettingsScreenEvent.OnThemeChange -> {
                changeTheme(event.theme)
            }
            is SettingsScreenEvent.OnTimeFormatChange -> {
                changeTimeFormat(event.is24HourFormat)

            }

            is SettingsScreenEvent.OnDynamicColorsChange -> {
                setDynamicColorsState(event.isEnabled)
            }

            SettingsScreenEvent.OnAboutLibrariesClick -> {
                sendEvent(NavigateToLibrariesScreen)
            }
            SettingsScreenEvent.OnBack -> {
                sendEvent(OnBack)
            }

            is SettingsScreenEvent.BackupData -> {
                backupData(event.uri)

            }
            SettingsScreenEvent.OnExportDataClick -> {
                sendEvent(LaunchCreateDocument(backupRepository.fileName))
            }

            SettingsScreenEvent.OnImportDataClick -> {
                sendEvent(LaunchOpenDocument)

            }

            is SettingsScreenEvent.RestoreBackup -> {
                importData(event.uri)
            }

            is SettingsScreenEvent.OnUseSystemFontChange -> {
                handleUseSystemFont(event.useSystemFont)
            }

            is SettingsScreenEvent.OnBiometricLockChange -> {
                handleBiometricLockChange(event.isEnabled)

            }

            is SettingsScreenEvent.OnOpenSourceCodeClick -> {
                sendEvent(OpenUrl(GITHUB_URL))
            }

            SettingsScreenEvent.OnSendFeedbackClick -> {
                sendEvent(LaunchSendFeedbackEmail)
            }

            SettingsScreenEvent.OnRateAppClick -> {
                sendEvent(OpenUrl(PLAY_STORE_URL))
            }

            SettingsScreenEvent.OnToggleChangeLogClick -> {
                handleChangeLogClick()
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

    private fun observeSettingsDataStore() {
        viewModelScope.launch {
            dataStore.getSystemFontState.collect { systemFontState ->
                _state.update {
                    it.copy(isSystemFontEnabled = systemFontState)
                }
            }
        }

        viewModelScope.launch {
            dataStore.getIs24HourFormat.collect { is24HourFormat ->
                _state.update {
                    it.copy(is24HourFormat = is24HourFormat)
                }
            }
        }

        viewModelScope.launch {
            dataStore.getTheme.collect { themeOption ->
                _state.update {
                    it.copy(selectedThemeOption = themeOption)
                }
            }
        }

        viewModelScope.launch {
            dataStore.getDynamicColorsState.collect { isEnabled ->
                _state.update {
                    it.copy(isDynamicColorsEnabled = isEnabled)
                }
            }
        }

        viewModelScope.launch {
            dataStore.getBiometricLockEnabled.collect { isEnabled ->
                _state.update {
                    it.copy(isBiometricLockEnabled = isEnabled)
                }
            }
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
        when(biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.BIOMETRIC_WEAK
        )){
            BiometricManager.BIOMETRIC_SUCCESS -> {
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
                sendEvent(
                    ShowToast(ToastMessage.Res(R.string.biometric_unavailable))
                )

            }
        }
    }

    private fun updateBiometricPreference(isEnabled: Boolean) = viewModelScope.launch {
        dataStore.setBiometricLockEnabled(isEnabled)
    }

    private fun sendEvent(event : SettingsScreenEffect) = viewModelScope.launch{
            _effect.emit(event)

    }

    private fun getAppVersion() {
        val appVersion = getAppVersionWithVersionCode()
        _state.update {
            it.copy(
                appVersion = appVersion
            )
        }
    }

    private fun backupData(uri : Uri)= viewModelScope.launch(Dispatchers.IO) {
        backupRepository.backupData(uri).collectLatest { state ->
            when(state){
                is ProcessState.Error -> {
                    _state.update {
                        it.copy(
                            isExporting = false
                        )
                    }
                    sendEvent(SettingsScreenEffect.ShowToast(ToastMessage.Text(state.exception)))

                }
                is ProcessState.Loading -> {
                    _state.update {
                        it.copy(
                            isExporting = true
                        )
                    }
                }

                ProcessState.NotDetermined ->{}
                is ProcessState.Success<*> -> {
                    _state.update {
                        it.copy(
                            isExporting = false
                        )
                    }
                    sendEvent(SettingsScreenEffect.ShowToast(ToastMessage.Res(R.string.backup_restored)))
                }
            }
        }
    }

    private fun importData(uri : Uri) = viewModelScope.launch(Dispatchers.IO) {
        backupRepository.importData(uri).collectLatest { state->
            when(state){
                is ProcessState.Error -> {
                    _state.update {
                        it.copy(
                            isImporting = false
                        )
                    }
                    sendEvent(SettingsScreenEffect.ShowToast(ToastMessage.Text(state.exception)))
                }
                is ProcessState.Loading -> {
                    _state.update {
                        it.copy(
                            isImporting = true
                        )
                    }
                }
                ProcessState.NotDetermined -> {}
                is ProcessState.Success<*> -> {
                    _state.update {
                        it.copy(
                            isImporting = false
                        )
                    }
                    sendEvent(SettingsScreenEffect.ShowToast(ToastMessage.Res(R.string.backup_restored)))
                }
            }

        }

    }

}
