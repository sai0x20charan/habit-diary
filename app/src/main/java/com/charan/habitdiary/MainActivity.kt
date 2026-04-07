package com.charan.habitdiary

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf

import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.FragmentActivity

import androidx.navigation3.runtime.NavKey
import com.charan.habitdiary.presentation.root.navigation.Destinations
import com.charan.habitdiary.presentation.root.AppRoot
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    private val keepScreen = mutableStateOf(true)

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
        WindowInsetsControllerCompat(window, window.decorView)
        setContent {
            LaunchedEffect(Unit) {
                keepScreen.value = false
            }

            AppRoot(
                deepLinkStack = deepLinkStack,

            )
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
