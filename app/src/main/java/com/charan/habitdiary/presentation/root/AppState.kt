package com.charan.habitdiary.presentation.root

import com.charan.habitdiary.data.model.enums.ThemeOption

data class AppState(
    val isOnBoardingCompleted: Boolean = true,
    val theme: ThemeOption = ThemeOption.SYSTEM_DEFAULT,
    val dynamicColorsEnabled: Boolean = true,
    val useSystemFont: Boolean = true,
    val showChangeLog: Boolean = false,
    val showBiometricUnlock: Boolean = false,
    val isUnlocked : Boolean = false
)
