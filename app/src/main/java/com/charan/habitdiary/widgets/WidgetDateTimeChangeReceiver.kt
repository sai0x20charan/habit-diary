package com.charan.habitdiary.widgets

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class WidgetDateTimeChangeReceiver : BroadcastReceiver() {
    @Inject
    lateinit var widgetManager: HabitDiaryWidgetManager

    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return
        val action = intent?.action ?: return
        if (action !in SUPPORTED_ACTIONS) return
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.Default).launch {
            try {
                widgetManager.refreshAddDailyLogWidget()
            } finally {
                pendingResult.finish()
            }
        }
    }

    private companion object {
        val SUPPORTED_ACTIONS = setOf(
            Intent.ACTION_DATE_CHANGED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
        )
    }
}
