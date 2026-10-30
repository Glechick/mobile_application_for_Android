package com.example.android_application.di

import android.content.ContentResolver
import android.content.Context
import com.example.android_application.data.llm.LlmRepository
import com.example.android_application.data.llm.LlmRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LlmModule {

    @Provides
    @Singleton
    fun provideLlmRepository(
        @ApplicationContext context: Context
    ): LlmRepository = LlmRepositoryImpl(
        contentResolver = context.contentResolver,
        context = context
    )
}