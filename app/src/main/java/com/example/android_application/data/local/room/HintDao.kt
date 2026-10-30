package com.example.android_application.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HintDao {
    @Query("SELECT * FROM hint_cache WHERE riddle = :riddle LIMIT 1")
    suspend fun getByRiddle(riddle: String): HintCache?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cache: HintCache)
}