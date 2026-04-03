package com.charan.habitdiary.widgets

import android.content.Context
import androidx.glance.appwidget.updateAll

class HabitDiaryWidgetManager(
    private val context: Context
) {

    suspend fun refreshAddDailyLogWidget() {
        AddDailyLogWidget().updateAll(context)
    }

    suspend fun refreshAllWidgets() = refreshAddDailyLogWidget()
}
