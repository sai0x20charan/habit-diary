package com.charan.habitdiary

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.charan.habitdiary.data.model.enums.ThemeOption
import com.charan.habitdiary.data.repository.DataStoreRepository
import com.charan.habitdiary.presentation.navigation.Destinations
import com.charan.habitdiary.presentation.navigation.RootNavigation
import com.charan.habitdiary.ui.theme.HabitDiaryTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var dataStore : DataStoreRepository
    private val keepScreen = mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        val deepLinkStack = intent?.data?.let {
            DeepLinkHandler.resolve(it)
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
            val initialTheme = if(isSystemInDarkTheme()) ThemeOption.DARK else ThemeOption.LIGHT
            val themeData = dataStore.getTheme.collectAsStateWithLifecycle(initialValue = initialTheme)
            val dynamicColorsEnabled = dataStore.getDynamicColorsState.collectAsStateWithLifecycle(initialValue = true)
            val isSystemFont = dataStore.getSystemFontState.collectAsStateWithLifecycle(initialValue = true)
            val onBoardingCompleted = remember {
                mutableStateOf(true)
            }
            LaunchedEffect(Unit) {
                onBoardingCompleted.value = dataStore.getOnBoardingCompleted.first()
            }
            val isDarkMode = when(themeData.value){
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
                    RootNavigation(
                        onBoardingCompleted = onBoardingCompleted.value,
                        deepLinkNavKey = deepLinkStack
                    )
                }

            }
        }
    }
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
                        triggerVideoCapture)
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
