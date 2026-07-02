package com.charan.habitdiary.core.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationScheduler @Inject constructor(
    @ApplicationContext private val context : Context
) {
    private val alarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    @OptIn(ExperimentalTime::class)
    fun scheduleReminder(
        habitId: Long,
        time: LocalTime?,
        frequency: List<DayOfWeek>,
        isReminderEnabled: Boolean = false,
    ) {
        if (!isReminderEnabled) {
            cancelReminder(habitId)
            return
        }
        if(time==null){
            return
        }

        val hours = time.hour
        val minutes = time.minute

        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        var scheduledDate = now.date
        var scheduledTime = LocalDateTime(
            year = scheduledDate.year,
            month = scheduledDate.month.number,
            day = scheduledDate.day,
            hour = hours,
            minute = minutes,
            second = 0,
            nanosecond = 0
        )
        if (scheduledTime <= now) {
            scheduledDate = scheduledDate.plus(1, DateTimeUnit.DAY)
            scheduledTime = LocalDateTime(
                year = scheduledDate.year,
                month = scheduledDate.month.number,
                day = scheduledDate.day,
                hour = hours,
                minute = minutes,
                second = 0,
                nanosecond = 0
            )
        }
        while (true) {
            val weekdayIndex = scheduledTime.dayOfWeek
            if (frequency.contains(weekdayIndex)) {
                break
            }
            scheduledDate = scheduledDate.plus(1, DateTimeUnit.DAY)
            scheduledTime = LocalDateTime(
                year = scheduledDate.year,
                month = scheduledDate.month.number,
                day = scheduledDate.day,
                hour = hours,
                minute = minutes,
                second = 0,
                nanosecond = 0
            )
        }

        val epochMillis = scheduledTime
            .toInstant(TimeZone.currentSystemDefault())
            .toEpochMilliseconds()

        println("scheduleReminder at weekday ${scheduledTime.dayOfWeek} time $epochMillis")

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("habitId", habitId)
            action = IntentActions.SHOW_NOTIFICATION.name
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            habitId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            epochMillis,
            pendingIntent
        )
    }


    fun cancelReminder(
        habitId : Long
    ) {

        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            habitId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)

    }

    fun scheduleDailyLogReminder(
        time: LocalTime?,
        isReminderEnabled: Boolean = false,
    ) {
        val reminderId = NotificationHelper.DAILY_LOG_REMINDER_NOTIFICATION_ID.toLong()
        if (!isReminderEnabled) {
            cancelDailyLogReminder()
            return
        }
        if(time == null){
            return
        }

        val hours = time.hour
        val minutes = time.minute

        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        var scheduledDate = now.date
        var scheduledTime = LocalDateTime(
            year = scheduledDate.year,
            month = scheduledDate.month.number,
            day = scheduledDate.day,
            hour = hours,
            minute = minutes,
            second = 0,
            nanosecond = 0
        )
        if (scheduledTime <= now) {
            scheduledDate = scheduledDate.plus(1, DateTimeUnit.DAY)
            scheduledTime = LocalDateTime(
                year = scheduledDate.year,
                month = scheduledDate.month.number,
                day = scheduledDate.day,
                hour = hours,
                minute = minutes,
                second = 0,
                nanosecond = 0
            )
        }

        val epochMillis = scheduledTime
            .toInstant(TimeZone.currentSystemDefault())
            .toEpochMilliseconds()

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = IntentActions.SHOW_DAILY_LOG_REMINDER.name
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            epochMillis,
            pendingIntent
        )
    }

    fun cancelDailyLogReminder() {
        val reminderId = NotificationHelper.DAILY_LOG_REMINDER_NOTIFICATION_ID.toLong()
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = IntentActions.SHOW_DAILY_LOG_REMINDER.name
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}