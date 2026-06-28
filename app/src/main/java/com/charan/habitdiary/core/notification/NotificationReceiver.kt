package com.charan.habitdiary.core.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.charan.habitdiary.data.local.entity.DailyLogEntity
import com.charan.habitdiary.data.repository.DiaryRepository
import com.charan.habitdiary.data.repository.HabitRepository
import com.charan.habitdiary.core.utils.DateUtil
import com.charan.habitdiary.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationReceiver : BroadcastReceiver() {
    @Inject lateinit var notificationHelper: NotificationHelper
    @Inject lateinit var habitRepository: HabitRepository
    @Inject lateinit var diaryRepository: DiaryRepository

    @Inject lateinit var notificationScheduler: NotificationScheduler
    override fun onReceive(context: Context?, intent: Intent?) {
        val pendingResult = goAsync()
        val appContext = context?.applicationContext
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            try {
                when(intent?.action){
                    IntentActions.SHOW_NOTIFICATION.name -> {
                        val habitId = intent?.getLongExtra("habitId", -1) ?: -1
                        if (habitId != -1L && appContext != null) {
                            val habitResult = habitRepository.getHabitWithId(habitId)
                            habitResult.onFailure {
                                Log.e("NotificationReceiver", "Failed to get habit", it)
                                return@launch
                            }
                            val habit = habitResult.getOrNull() ?: return@launch
                            
                            val habitLogResult = diaryRepository.getLoggedHabitFromIdForRange(habitId)
                            habitLogResult.onFailure {
                                Log.e("NotificationReceiver", "Failed to get habit log", it)
                                return@launch
                            }
                            val habitLog = habitLogResult.getOrNull()
                            
                            if(habitLog == null){
                                notificationHelper.showNotification(
                                    title = appContext.getString(R.string.notification_reminder_title),
                                    message = appContext.getString(R.string.notification_habit_reminder_message, habit.habitName),
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
                            val habitResult = habitRepository.getHabitWithId(habitId)
                            habitResult.onFailure {
                                Log.e("NotificationReceiver", "Failed to get habit", it)
                                return@launch
                            }
                            val habit = habitResult.getOrNull() ?: return@launch
                            
                            val habitLogResult = diaryRepository.getLoggedHabitFromIdForRange(habitId)
                            habitLogResult.onFailure {
                                Log.e("NotificationReceiver", "Failed to get habit log", it)
                                return@launch
                            }
                            val habitLog = habitLogResult.getOrNull()
                            
                            if(habitLog == null){
                                diaryRepository.upsertDailyLog(
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