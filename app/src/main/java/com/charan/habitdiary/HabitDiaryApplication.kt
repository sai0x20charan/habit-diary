package com.charan.habitdiary

import android.app.Application
import androidx.appfunctions.service.AppFunctionConfiguration
import com.charan.habitdiary.appfunctions.AppFunctions
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class HabitDiaryApplication : Application(), AppFunctionConfiguration.Provider {
    @Inject lateinit var appFunction : AppFunctions
    override val appFunctionConfiguration: AppFunctionConfiguration
        get() =
            AppFunctionConfiguration.Builder()
                .addEnclosingClassFactory(AppFunctions::class.java) { appFunction }
                .build()
}