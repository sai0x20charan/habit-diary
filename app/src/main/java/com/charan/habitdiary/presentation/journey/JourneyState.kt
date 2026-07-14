package com.charan.habitdiary.presentation.journey

import com.charan.habitdiary.presentation.common.model.MediaItemUIModel

data class JourneyState(
    val flashbackMedia : List<FlashbackMedia> = emptyList(),
    val carouselMediaPaths : List<String> = emptyList(),
    val diaryStats : DiaryStats = DiaryStats(),
    val habitsStats: HabitsStats = HabitsStats()
)

data class FlashbackMedia(
    val titleRes : Int = 0,
    val mediaItems : List<MediaItemUIModel> = emptyList()
)

data class DiaryStats(
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val totalLogs: Int = 0,
    val totalMedia : Int = 0
)

data class HabitsStats(
    val totalHabits: Int = 0,
    val totalCompletions: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0
)