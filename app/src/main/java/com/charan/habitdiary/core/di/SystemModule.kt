package com.charan.habitdiary.core.di

import android.content.Context
import androidx.biometric.BiometricManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SystemModule {

    @Provides
    @Singleton
    fun provideBiometricManager(
        @ApplicationContext context: Context
    ) : BiometricManager = BiometricManager.from(context)
}
