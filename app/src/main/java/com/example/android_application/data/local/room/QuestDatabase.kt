package com.example.android_application.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PlayerProfile::class, CompletionHistory::class, HintCache::class],
    version = 2,
    exportSchema = false
)
abstract class QuestDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun hintDao(): HintDao
}