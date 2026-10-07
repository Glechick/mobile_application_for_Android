package com.example.android_application.di

import android.content.Context
import com.example.android_application.data.local.realm.GameStageEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.xilinjia.krdb.Realm
import io.github.xilinjia.krdb.RealmConfiguration
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RealmModule {

    @Provides
    @Singleton
    fun provideRealm(@ApplicationContext context: Context): Realm {
        val config = RealmConfiguration.Builder(
            schema = setOf(GameStageEntity::class)
        )
            .name("quest_stages.realm")
            .deleteRealmIfMigrationNeeded()
            .build()
        return Realm.open(config)
    }
}