package com.charan.habitdiary.presentation.root

import android.util.Log
import androidx.biometric.BiometricManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charan.habitdiary.R
import com.charan.habitdiary.data.repository.DataStoreRepository
import com.charan.habitdiary.presentation.common.model.ToastMessage
import com.charan.habitdiary.utils.isBiometricAvailable
import com.charan.habitdiary.utils.getAppVersion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val dataStoreRepo: DataStoreRepository,
    private val biometricManager: BiometricManager
) : ViewModel() {

    private val _state = MutableStateFlow(AppState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<AppEffects>()
    val effect = _effect.asSharedFlow()

    init {
        observeAppState()
    }

    private fun observeAppState() = viewModelScope.launch {

        launch {
            combine(
                dataStoreRepo.getTheme,
                dataStoreRepo.getDynamicColorsState,
                dataStoreRepo.getSystemFontState,
            ) { theme, dynamicColorsEnabled, useSystemFont ->
                Triple(theme, dynamicColorsEnabled, useSystemFont)
            }.collectLatest { observed ->
                _state.update {
                    it.copy(
                        theme = observed.first,
                        dynamicColorsEnabled = observed.second,
                        useSystemFont = observed.third
                    )
                }
            }
        }

        launch {
            val onboarding = dataStoreRepo.getOnBoardingCompleted.first()
            _state.update {
                it.copy(isOnBoardingCompleted = onboarding)
            }
        }

        launch {
            val biometric = dataStoreRepo.getBiometricLockEnabled.first()
            if (biometric) {
                handleBiometric()
            }
        }
    }

    private fun handleBiometric() = viewModelScope.launch {
        if(biometricManager.isBiometricAvailable()){
            _state.update { state->
                state.copy(
                    showBiometricUnlock = true
                )
            }

        } else {
            sendEffect(AppEffects.ShowToast(ToastMessage.Res(R.string.biometric_unavailable)))
            dataStoreRepo.setBiometricLockEnabled(false)


        }
    }

    fun onEvent(event : AppEvents) {
        when(event) {
            is AppEvents.OnAuthResult -> {
                handleAuthResult(event.isSuccess)
            }
        }
    }

    private fun handleAuthResult(isUnlocked : Boolean) = viewModelScope.launch {
        _state.update {
            it.copy(
                isUnlocked = isUnlocked
            )
        }
    }

    private fun sendEffect(effect : AppEffects) = viewModelScope.launch {
        _effect.emit(effect)

    }
}
