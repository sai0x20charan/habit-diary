package com.charan.habitdiary.utils

import android.content.Context
import android.widget.Toast
import com.charan.habitdiary.data.local.entity.DailyLogEntity
import com.charan.habitdiary.presentation.common.model.ToastMessage
import com.kizitonwose.calendar.core.minusDays
import com.kizitonwose.calendar.core.plusDays
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

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

