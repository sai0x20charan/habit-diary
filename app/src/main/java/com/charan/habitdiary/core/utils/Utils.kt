package com.charan.habitdiary.core.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.biometric.BiometricManager
import com.charan.habitdiary.data.local.entity.DailyLogEntity
import com.charan.habitdiary.presentation.common.model.ToastMessage
import com.kizitonwose.calendar.core.minusDays
import com.kizitonwose.calendar.core.plusDays
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import androidx.core.net.toUri
import com.charan.habitdiary.BuildConfig
import com.charan.habitdiary.R
import java.util.Locale
import kotlin.coroutines.cancellation.CancellationException

suspend inline fun <T> suspendRunCatching(crossinline block: suspend () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (c: CancellationException) {
        throw c
    } catch (e: Throwable) {
        Result.failure(e)
    }
}

fun String.isVideo(): Boolean {
    return endsWith(".mp4", true) ||
            endsWith(".mov", true) ||
            endsWith(".mkv", true) ||
            endsWith(".webm", true)
}

fun Context.showToast(toastMessage: ToastMessage){
    when(toastMessage){
        is ToastMessage.Res -> {
            Toast.makeText(
                this,
                toastMessage.resId,
                Toast.LENGTH_SHORT
            ).show()
        }
        is ToastMessage.Text -> {
            Toast.makeText(
                this,
                toastMessage.text,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

fun List<DailyLogEntity>.getHabitStreak(
    frequency: List<DayOfWeek>
): Int {
    if (isEmpty()) return 0

    val completedDates = this
        .map { it.createdAt.date }
        .toSet()

    var streak = 0
    var date = DateUtil.getCurrentDate()

    while (true) {
        val isScheduledDay = date.dayOfWeek in frequency
        val isDone = date in completedDates

        when {
            isDone -> {
                streak++
                date = date.minusDays(1)
            }

            isScheduledDay && !isDone -> {
                break
            }

            else -> {
                date = date.minusDays(1)
            }
        }
    }

    return streak
}


fun List<DailyLogEntity>.getBestHabitStreak(
    frequency: List<DayOfWeek>
): Int {
    if (this.isEmpty()) return 0

    val completedDates = this
        .map { it.createdAt.date }
        .sorted()

    var bestStreak = 0
    var currentStreak = 0
    var lastDate: LocalDate? = null

    for (date in completedDates) {
        val shouldContinue =
            if (lastDate == null) {
                true
            } else {
                var nextDate = lastDate.plusDays(1)
                var isBroken = false

                while (nextDate < date) {
                    if (nextDate.dayOfWeek in frequency) {
                        isBroken = true
                        break
                    }
                    nextDate = nextDate.plusDays(1)
                }

                !isBroken
            }

        currentStreak = if (shouldContinue) currentStreak + 1 else 1
        bestStreak = maxOf(bestStreak, currentStreak)
        lastDate = date
    }

    return bestStreak
}

fun isSDK29OrAbove() = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q

fun Context.launchFeedbackEmail() {
    try {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:".toUri()
            putExtra(Intent.EXTRA_EMAIL, arrayOf(DEVELOPER_EMAIL))
            putExtra(
                Intent.EXTRA_SUBJECT,
                getString(R.string.feedback_email_subject)
            )
            putExtra(Intent.EXTRA_TEXT, generateFeedbackEmailBody())
        }
        startActivity(intent)
    } catch (e: Exception) {
        showToast(
            ToastMessage.Res(R.string.no_email_client)
        )
    }
}
private fun Context.generateFeedbackEmailBody(): String {
    return buildString {
        append("\n\n")
        appendLine("---")
        appendLine(getString(R.string.feedback_device_app_info))
        appendLine(getString(R.string.feedback_app_version, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE))
        appendLine(getString(R.string.feedback_android_version, android.os.Build.VERSION.RELEASE, android.os.Build.VERSION.SDK_INT))
        appendLine(getString(R.string.feedback_device, android.os.Build.MANUFACTURER, android.os.Build.MODEL))
        appendLine("---")
    }
}

fun Context.launchUrl(url : String){
    try {
        this.startActivity(
            Intent(Intent.ACTION_VIEW).apply {
                data = url.toUri()
            }
        )
    } catch (e: Exception) {
        showToast(
            ToastMessage.Res(R.string.no_browser_found)
        )
        return
    }

}

fun Long.toFormatTimeMs(): String {
    if (this <= 0) return "0:00"

    val totalSeconds = this / 1000

    val seconds = (totalSeconds % 60).toInt()
    val minutes = ((totalSeconds / 60) % 60).toInt()
    val hours = (totalSeconds / 3600).toInt()

    return if (hours > 0) {
        String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
    }
}
fun BiometricManager.isBiometricAvailable(): Boolean {
    return canAuthenticate(
        BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.BIOMETRIC_WEAK or
                BiometricManager.Authenticators.DEVICE_CREDENTIAL
    ) == BiometricManager.BIOMETRIC_SUCCESS
}

fun getAppVersionWithVersionCode() =
    "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"

fun getAppVersion() = BuildConfig.VERSION_NAME

fun List<DailyLogEntity>.getDiaryStreak(): Int {
    if (isEmpty()) return 0

    val completedDates = this
        .map { it.createdAt.date }
        .toSet()

    var streak = 0
    var date = DateUtil.getCurrentDate()

    if (date !in completedDates) {
        date = date.minusDays(1)
        if (date !in completedDates) {
            return 0
        }
    }

    while (date in completedDates) {
        streak++
        date = date.minusDays(1)
    }

    return streak
}

fun List<DailyLogEntity>.getBestDiaryStreak(): Int {
    if (isEmpty()) return 0

    val completedDates = this
        .map { it.createdAt.date }
        .toSet()
        .sorted()

    var bestStreak = 0
    var currentStreak = 0
    var lastDate: LocalDate? = null

    for (date in completedDates) {
        val shouldContinue = if (lastDate == null) {
            true
        } else {
            date == lastDate.plusDays(1)
        }

        currentStreak = if (shouldContinue) currentStreak + 1 else 1
        bestStreak = maxOf(bestStreak, currentStreak)
        lastDate = date
    }

    return bestStreak
}
