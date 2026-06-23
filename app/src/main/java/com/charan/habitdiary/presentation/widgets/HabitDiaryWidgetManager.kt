package com.charan.habitdiary.presentation.widgets

import android.content.Context
import androidx.glance.appwidget.updateAll
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class HabitDiaryWidgetManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    suspend fun refreshAddDailyLogWidget() {
        AddDailyLogWidget().updateAll(context)
    }

    suspend fun refreshAllWidgets() = refreshAddDailyLogWidget()
}
