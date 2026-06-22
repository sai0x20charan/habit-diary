package com.charan.habitdiary.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.charan.habitdiary.data.local.entity.DailyLogEntity
import com.charan.habitdiary.data.repository.HabitRepository
import com.charan.habitdiary.utils.DateUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationReceiver : BroadcastReceiver() {
    @Inject lateinit var notificationHelper: NotificationHelper
    @Inject lateinit var habitRepository: HabitRepository

    @Inject lateinit var notificationScheduler: NotificationScheduler
    override fun onReceive(context: Context?, intent: Intent?) {
        val pendingResult = goAsync()
        val appContext = context?.applicationContext

        CoroutineScope(Dispatchers.IO).launch {
            try {
                when(intent?.action){
                    IntentActions.SHOW_NOTIFICATION.name -> {
                        val habitId = intent?.getLongExtra("habitId", -1) ?: -1
                        if (habitId != -1L && appContext != null) {
                            val habit = habitRepository.getHabitWithId(habitId).getOrNull() ?: return@launch
                            val habitLog = habitRepository.getLoggedHabitFromIdForRange(habitId).getOrNull()
                            if(habitLog == null){
                                notificationHelper.showNotification(
                                    title = "Habit Reminder",
                                    message = "It's time for your habit: ${habit.habitName}",
                                    habitId = habit.id
                                )
                            }
                            notificationScheduler.scheduleReminder(
                                habitId = habit.id,
                                time = habit.habitReminder,
                                frequency = habit.habitFrequency,
                                isReminderEnabled = habit.isReminderEnabled
                            )
                        }
                    }

                    IntentActions.MARK_AS_DONE.name -> {
                        val habitId = intent?.getLongExtra("habitId", -1) ?: -1
                        if (habitId != -1L) {
                            val habit = habitRepository.getHabitWithId(habitId).getOrNull() ?: return@launch
                            val habitLog = habitRepository.getLoggedHabitFromIdForRange(habitId).getOrNull()
                            if(habitLog == null){
                                habitRepository.upsetDailyLog(
                                    DailyLogEntity(
                                        logNote = "",
                                        imagePath = "",
                                        createdAt = DateUtil.getCurrentDateTime(),
                                        habitId = habit.id
                                    )
                                )
                            }
                        }
                        notificationHelper.cancelNotification(habitId)
                    }

                }

            } finally {
                pendingResult.finish()
            }
        }
    }


}