package com.example.android_application.di

import com.example.android_application.data.mock.MockData
import com.example.android_application.data.repository.ScenarioRepository
import com.example.android_application.data.repository.ScenarioRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMockData(): MockData = MockData()

    @Provides
    @Singleton
    fun provideScenarioRepository(mockData: MockData): ScenarioRepository =
        ScenarioRepositoryImpl(mockData)
}