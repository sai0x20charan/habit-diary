package com.charan.habitdiary.core.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.charan.habitdiary.R
import androidx.core.net.toUri
import com.charan.habitdiary.DeepLinkHandler
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationHelper @Inject constructor(@ApplicationContext private val context: Context) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    init {
        createHabitReminderNotificationChannel()
    }

    companion object {
        private const val HABIT_REMINDER_CHANNEL_ID = "habit_reminder_channel"
        private const val HABIT_REMINDER_CHANNEL_NAME = "Habit Reminders"
    }

    fun createHabitReminderNotificationChannel() {
        val channel = NotificationChannel(
            HABIT_REMINDER_CHANNEL_ID,
            HABIT_REMINDER_CHANNEL_NAME,
            IMPORTANCE_HIGH
        ).apply {
            description = context.getString(R.string.notification_channel_description)
        }
        notificationManager.createNotificationChannel(channel)
    }

    fun showNotification(
        title: String,
        message: String,
        habitId : Long
    ) {
        val markAsDoneIntent = Intent(context, NotificationReceiver::class.java).apply {
            action = IntentActions.MARK_AS_DONE.name
            putExtra("habitId", habitId)
        }
        val markAsDoneActionIntent = PendingIntent.getBroadcast(
            context,
            habitId.toInt(),
            markAsDoneIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val openHabitStatScreen = Intent(
            Intent.ACTION_VIEW,
            "${DeepLinkHandler.BASE_URL}${DeepLinkHandler.HABIT_STATS_URI}?id=$habitId".toUri()
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("habitId", habitId)
        }

        val openHabitStatsIntent = PendingIntent.getActivity(
            context,
            habitId.hashCode(),
            openHabitStatScreen,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )



        val builder =
            Notification.Builder(context, HABIT_REMINDER_CHANNEL_ID)

        val notification = builder
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.notification_icon)
            .setAutoCancel(true)
            .setContentIntent(openHabitStatsIntent)
            .addAction(
                Notification.Action.Builder(
                    null,
                    context.getString(R.string.mark_as_done),
                    markAsDoneActionIntent
                ).build()
            )
            .build()

        notificationManager.notify(habitId.toInt(), notification)
    }

    fun cancelNotification(habitId : Long){
        notificationManager.cancel(habitId.toInt())
    }
}
