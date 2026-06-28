package com.charan.habitdiary.presentation.root

import androidx.biometric.AuthenticationRequest
import androidx.biometric.AuthenticationResult
import androidx.biometric.AuthenticationResultCallback
import androidx.biometric.compose.rememberAuthenticationLauncher
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.charan.habitdiary.R
import com.charan.habitdiary.data.model.enums.ThemeOption
import com.charan.habitdiary.presentation.common.components.ChangeLogBottomSheet
import com.charan.habitdiary.presentation.root.navigation.RootNavigation
import com.charan.habitdiary.presentation.theme.HabitDiaryTheme
import com.charan.habitdiary.core.utils.showToast
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AppRoot(
    deepLinkStack: List<NavKey>?
) {
    val viewModel = hiltViewModel<AppViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val biometricLauncher = rememberAuthenticationLauncher(
        resultCallback = object : AuthenticationResultCallback {
            override fun onAuthResult(result: AuthenticationResult) {
                when (result) {
                    is AuthenticationResult.Success -> {
                        viewModel.onEvent(AppEvent.OnAuthResult(true))
                    }
                    is AuthenticationResult.Error -> {
                        viewModel.onEvent(AppEvent.OnAuthResult(false))
                    }
                    else -> {}
                }
            }

            override fun onAuthAttemptFailed() {
                viewModel.onEvent(AppEvent.OnAuthResult(false))
            }
        }
    )
    val biometricUnlockString =  stringResource(R.string.biometric_prompt_title)
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest {
            when (it) {
                is AppEffect.ShowToast -> {
                    context.showToast(it.message)
                }
            }
        }
    }


    val launchBiometric: () -> Unit = {
        biometricLauncher.launch(
            AuthenticationRequest.biometricRequest(
                title = biometricUnlockString,
                authFallbacks = arrayOf(AuthenticationRequest.Biometric.Fallback.DeviceCredential)
            ) { }
        )
    }

    val shouldShowBiometricLock = state.showBiometricUnlock && !state.isUnlocked

    LaunchedEffect(shouldShowBiometricLock) {
        if (shouldShowBiometricLock) {
            launchBiometric()
        }
    }

    val isDarkMode = when (state.theme) {
        ThemeOption.DARK -> true
        ThemeOption.LIGHT -> false
        ThemeOption.SYSTEM_DEFAULT -> isSystemInDarkTheme()
    }

    HabitDiaryTheme(
        darkTheme = isDarkMode,
        dynamicColor = state.dynamicColorsEnabled,
        isSystemFont = state.useSystemFont
    ) {
        Surface {
            Box(modifier = Modifier.fillMaxSize()) {
                if (!shouldShowBiometricLock) {
                    if(state.showChangeLog){
                        ChangeLogBottomSheet {
                            viewModel.onEvent(AppEvent.OnCloseChangeLog)
                        }
                    }
                    RootNavigation(
                        onBoardingCompleted = state.isOnBoardingCompleted,
                        deepLinkNavKey = deepLinkStack
                    )
                }

                AnimatedVisibility(
                    visible = shouldShowBiometricLock,
                    enter = fadeIn() + scaleIn(initialScale = 0.98f),
                    exit = fadeOut() + scaleOut(targetScale = 0.98f)
                ) {
                    BiometricLockScreen(
                        onRetry = launchBiometric
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun BiometricLockScreen(
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.app_lock),
            contentDescription = stringResource(R.string.app_logo_description),
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            stringResource(R.string.habit_diary_is_locked),
            style = MaterialTheme.typography.titleLargeEmphasized.copy(fontWeight = FontWeight.SemiBold)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            shapes = ButtonDefaults.shapes(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.unlock_app))
        }
    }
}
