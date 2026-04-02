package com.charan.habitdiary.widgets

import android.content.Context
import androidx.glance.appwidget.updateAll
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HabitDiaryWidgetManager @Inject constructor(
    private val context: Context
) {

    suspend fun refreshAddDailyLogWidget() {
        AddDailyLogWidget().updateAll(context)
    }

    suspend fun refreshAllWidgets() = refreshAddDailyLogWidget()
}
