package com.example.android_application.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {

    @Query("SELECT * FROM player_profile WHERE id = 1")
    fun getProfile(): Flow<PlayerProfile?>

    @Query("SELECT * FROM player_profile WHERE id = 1")
    suspend fun getProfileOnce(): PlayerProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProfile(profile: PlayerProfile)

    @Query("SELECT * FROM completion_history ORDER BY startedAt DESC")
    fun getHistory(): Flow<List<CompletionHistory>>

    @Query("SELECT * FROM completion_history WHERE stageId = :stageId LIMIT 1")
    suspend fun getHistoryForStage(stageId: Int): CompletionHistory?

    @Query("SELECT DISTINCT stageId FROM completion_history")
    fun getCompletedStageIds(): Flow<List<Int>>

    @Query("SELECT DISTINCT scenarioId FROM completion_history WHERE finishedAt IS NOT NULL")
    fun getCompletedScenarioIds(): Flow<List<Int>>

    @Insert
    suspend fun insertHistory(entry: CompletionHistory)

    @Query("SELECT SUM(scoreEarned) FROM completion_history")
    suspend fun getTotalScore(): Int?

    @Query("""
        SELECT (MAX(finishedAt) - MIN(startedAt)) / 60000
        FROM completion_history
        WHERE scenarioId = :scenarioId AND finishedAt IS NOT NULL
    """)
    suspend fun getTimeSpentMinutesForScenario(scenarioId: Int): Long?

    @Query("""
        SELECT SUM(scoreEarned)
        FROM completion_history
        WHERE scenarioId = :scenarioId
    """)
    suspend fun getScoreForScenario(scenarioId: Int): Int?

    @Query("DELETE FROM completion_history")
    suspend fun clearHistory()
}