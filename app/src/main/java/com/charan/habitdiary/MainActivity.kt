package com.charan.habitdiary

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.charan.habitdiary.data.model.enums.ThemeOption
import com.charan.habitdiary.data.repository.DataStoreRepository
import com.charan.habitdiary.presentation.common.model.ToastMessage
import com.charan.habitdiary.presentation.navigation.Destinations
import com.charan.habitdiary.presentation.navigation.RootNavigation
import com.charan.habitdiary.ui.theme.HabitDiaryTheme
import com.charan.habitdiary.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    @Inject lateinit var dataStore : DataStoreRepository
    @Inject lateinit var biometricManager : BiometricManager
    private val keepScreen = mutableStateOf(true)

    private var isUnlocked = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        var deepLinkStack = intent?.data?.let {
            DeepLinkHandler.resolve(it)
        }
        intent.getSharedMedia().apply {
            if (this.isNotEmpty()) {
                deepLinkStack = listOf(
                    Destinations.BottomBarNav,
                    Destinations.AddDailyLog(
                        id = null,
                        date = null,
                        openCaptureImageOnLaunch = false,
                        openCaptureVideoOnLaunch = false,
                        mediaList = this.map { it }
                    )
                )

            }
        }

        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition {
            keepScreen.value
        }
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)

        )

        val controller = WindowInsetsControllerCompat(window, window.decorView)
        setContent {
            val initialTheme = if (isSystemInDarkTheme()) ThemeOption.DARK else ThemeOption.LIGHT
            val themeData =
                dataStore.getTheme.collectAsStateWithLifecycle(initialValue = initialTheme)
            val dynamicColorsEnabled =
                dataStore.getDynamicColorsState.collectAsStateWithLifecycle(initialValue = true)
            val isSystemFont =
                dataStore.getSystemFontState.collectAsStateWithLifecycle(initialValue = true)
            val isBiometricLockEnabled = rememberSaveable { mutableStateOf(false) }
            val onBoardingCompleted = remember {
                mutableStateOf(true)
            }
            LaunchedEffect(Unit) {
                onBoardingCompleted.value = dataStore.getOnBoardingCompleted.first()
                isBiometricLockEnabled.value = dataStore.getBiometricLockEnabled.first()
            }
            LaunchedEffect(isBiometricLockEnabled.value) {
                if (isBiometricLockEnabled.value) {
                    isUnlocked.value = false
                    val available = ensureBiometricAvailable()
                    if (available) {
                        showBiometricPrompt()
                    } else {
                        this@MainActivity.showToast(ToastMessage.Res(R.string.biometric_unavailable))
                        dataStore.setBiometricLockEnabled(false)
                        isUnlocked.value = true
                    }
                } else {
                    isUnlocked.value = true
                }
            }

            val isDarkMode = when (themeData.value) {
                ThemeOption.DARK -> true
                ThemeOption.LIGHT -> false
                ThemeOption.SYSTEM_DEFAULT -> isSystemInDarkTheme()
            }
            controller.isAppearanceLightStatusBars = !isDarkMode
            LaunchedEffect(Unit) {
                keepScreen.value = false
            }
            HabitDiaryTheme(
                darkTheme = isDarkMode,
                dynamicColor = dynamicColorsEnabled.value,
                isSystemFont = isSystemFont.value
            ) {
                Surface {
                    if (isUnlocked.value.not()) {
                        BiometricLockScreen(
                            onRetry = { showBiometricPrompt() },
                        )
                    } else {
                        RootNavigation(
                            onBoardingCompleted = onBoardingCompleted.value,
                            deepLinkNavKey = deepLinkStack
                        )


                    }
                }

            }
        }
    }

    private fun showBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(this)
        val prompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                isUnlocked.value = true
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                isUnlocked.value = false
            }

            override fun onAuthenticationFailed() {
                isUnlocked.value = false
            }
        })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.biometric_prompt_title))
            .setConfirmationRequired(false)
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.BIOMETRIC_WEAK
            )
            .setNegativeButtonText(getString(R.string.biometric_cancel))
            .build()

        prompt.authenticate(promptInfo)
    }

    private fun ensureBiometricAvailable(): Boolean {
        val canAuthenticate = biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.BIOMETRIC_WEAK
        )
        return canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun BiometricLockScreen(
    onRetry: () -> Unit,
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
            contentDescription = "App Logo",
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))

        Text("Habit Diary Locked",
            style = MaterialTheme.typography.titleLargeEmphasized.copy(fontWeight = FontWeight.SemiBold),
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

fun Intent.getSharedMedia(): List<String> {
    val result = mutableListOf<String>()

    when (action) {

        Intent.ACTION_SEND -> {

            clipData?.let { clip ->
                for (i in 0 until clip.itemCount) {
                    clip.getItemAt(i).uri?.let {
                        result.add(it.toString())
                    }
                }
            }
        }

        Intent.ACTION_SEND_MULTIPLE -> {
            clipData?.let { clip ->
                for (i in 0 until clip.itemCount) {
                    clip.getItemAt(i).uri?.let {
                        result.add(it.toString())
                    }
                }
            }
        }
    }

    return result
}

object DeepLinkHandler {
    val BASE_URL = "habitdiary://app/"
    val ADDHABIT_URI = "add-habit"
    val DAILYLOG_URI = "add-daily-log"

    val HABIT_STATS_URI = "habit-stats"
    val CAPTURE_IMAGE_QUERY = "openImageCaptureOnOpen"
    val CAPTURE_VIDEO_QUERY = "openVideoCaptureOnOpen"

    fun resolve(uri: Uri): List<NavKey>? {
        val pathSegments = uri.pathSegments

        return when {
            pathSegments.firstOrNull() == ADDHABIT_URI -> {
                val habitId = uri.getQueryParameter("id")?.toLongOrNull()
                listOf(Destinations.BottomBarNav, Destinations.AddHabit(habitId))
            }

            pathSegments.firstOrNull() == DAILYLOG_URI -> {
                val logId = uri.getQueryParameter("logId")?.toLongOrNull()
                val sharedMedias = uri.getQueryParameter("sharedMedias")?.split(",") ?: emptyList()
                val triggerImageCapture =
                    uri.getQueryParameter("openImageCaptureOnOpen")?.toBoolean() ?: false
                val triggerVideoCapture =
                    uri.getQueryParameter("openVideoCaptureOnOpen")?.toBoolean() ?: false
                listOf(
                    Destinations.BottomBarNav,
                    Destinations.AddDailyLog(
                        logId,
                        null,
                        triggerImageCapture,
                        triggerVideoCapture,
                        mediaList = sharedMedias)
                )
            }

            pathSegments.firstOrNull() == HABIT_STATS_URI -> {
                val habitId = uri.getQueryParameter("id")?.toLongOrNull() ?: return null
                listOf(Destinations.BottomBarNav, Destinations.HabitStatsScreeNav(habitId))
            }

            else -> null
        }
    }
}
