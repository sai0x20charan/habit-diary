package com.charan.habitdiary.core.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.charan.habitdiary.data.repository.HabitRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import com.charan.habitdiary.data.repository.DataStoreRepository
import javax.inject.Inject

@AndroidEntryPoint
class BootBroadcastReceiver : BroadcastReceiver() {
    @Inject lateinit var habitRepository: HabitRepository
    @Inject lateinit var notificationScheduler: NotificationScheduler
    @Inject lateinit var dataStoreRepository: DataStoreRepository
    override fun onReceive(context: Context?, intent: Intent?) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch{
            if(intent?.action == Intent.ACTION_BOOT_COMPLETED){
                try {
                    habitRepository.getAllHabits()
                        .onSuccess { habits ->
                            for (habit in habits) {
                                notificationScheduler.scheduleReminder(
                                    habitId = habit.id,
                                    time = habit.habitReminder,
                                    frequency = habit.habitFrequency,
                                    isReminderEnabled = habit.isReminderEnabled
                                )
                            }
                        }
                        .onFailure {
                            Log.e("BootBroadcastReceiver", "Failed to load habits on boot", it)
                        }

                    val dailyLogEnabled = dataStoreRepository.getDailyLogReminderEnabled.first()
                    val dailyLogTime = dataStoreRepository.getDailyLogReminderTime.first()
                    if (dailyLogEnabled) {
                        notificationScheduler.scheduleDailyLogReminder(dailyLogTime, true)
                    }
                } finally {
                    pendingResult.finish()
                }
            } else {
                pendingResult.finish()
            }

        }



    }
}