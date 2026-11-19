package com.example.android_application.di

import android.content.Context
import com.example.android_application.data.voice.VoiceRepository
import com.example.android_application.data.voice.VoiceRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object VoiceModule {
    @Provides
    @Singleton
    fun provideVoiceRepository(@ApplicationContext context: Context): VoiceRepository =
        VoiceRepositoryImpl(context)
}