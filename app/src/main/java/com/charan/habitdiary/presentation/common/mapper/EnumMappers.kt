package com.charan.habitdiary.presentation.common.mapper

import com.charan.habitdiary.R
import com.charan.habitdiary.data.model.enums.DailyLogSortType
import com.charan.habitdiary.data.model.enums.HabitSortType
import com.charan.habitdiary.data.model.enums.ThemeOption

fun DailyLogSortType.toResId(): Int {
    return when (this) {
        DailyLogSortType.NEWEST_FIRST -> R.string.newest_first
        DailyLogSortType.OLDEST_FIRST -> R.string.oldest_first
    }
}

fun DailyLogSortType.Companion.fromResId(resId: Int): DailyLogSortType {
    return DailyLogSortType.entries.firstOrNull { it.toResId() == resId }
        ?: DailyLogSortType.NEWEST_FIRST
}

fun HabitSortType.toResId(): Int {
    return when (this) {
        HabitSortType.ALL_HABITS -> R.string.all_habits
        HabitSortType.TODAY_HABITS -> R.string.today_habits
    }
}

fun HabitSortType.Companion.fromResId(resId: Int): HabitSortType {
    return HabitSortType.entries.firstOrNull { it.toResId() == resId }
        ?: HabitSortType.ALL_HABITS
}

fun ThemeOption.toResId(): Int {
    return when (this) {
        ThemeOption.SYSTEM_DEFAULT -> R.string.system
        ThemeOption.LIGHT -> R.string.light
        ThemeOption.DARK -> R.string.dark
    }
}
