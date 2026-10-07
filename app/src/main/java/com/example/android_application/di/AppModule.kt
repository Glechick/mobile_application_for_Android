package com.example.android_application.di

import android.content.Context
import androidx.room.Room
import com.example.android_application.data.local.room.PlayerDao
import com.example.android_application.data.local.room.QuestDatabase
import com.example.android_application.data.mock.MockData
import com.example.android_application.data.repository.PlayerRepository
import com.example.android_application.data.repository.PlayerRepositoryImpl
import com.example.android_application.data.repository.ScenarioRepository
import com.example.android_application.data.repository.ScenarioRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
    fun provideQuestDatabase(
        @ApplicationContext context: Context
    ): QuestDatabase = Room.databaseBuilder(
        context,
        QuestDatabase::class.java,
        "quest_database"
    )
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun providePlayerDao(db: QuestDatabase): PlayerDao = db.playerDao()

    @Provides
    @Singleton
    fun provideScenarioRepository(
        mockData: MockData,
        realmStageRepository: com.example.android_application.data.local.realm.RealmStageRepository
    ): ScenarioRepository = ScenarioRepositoryImpl(mockData, realmStageRepository)

    @Provides
    @Singleton
    fun providePlayerRepository(playerDao: PlayerDao): PlayerRepository =
        PlayerRepositoryImpl(playerDao)
}