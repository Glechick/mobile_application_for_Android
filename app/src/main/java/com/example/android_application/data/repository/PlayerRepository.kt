package com.example.android_application.data.repository

import com.example.android_application.data.local.room.CompletionHistory
import com.example.android_application.data.local.room.PlayerProfile
import kotlinx.coroutines.flow.Flow

interface PlayerRepository {
    fun getProfile(): Flow<PlayerProfile?>
    suspend fun updateProfile(profile: PlayerProfile)
    fun getHistory(): Flow<List<CompletionHistory>>
    fun getCompletedStageIds(): Flow<List<Int>>
    fun getCompletedScenarioIds(): Flow<List<Int>>
    suspend fun recordStageCompletion(
        scenarioId: Int,
        stageId: Int,
        stageNumber: Int,
        scoreEarned: Int,
        startedAt: Long,
        finishedAt: Long
    )
    suspend fun getTimeSpentMinutes(scenarioId: Int): Int
    suspend fun getScoreForScenario(scenarioId: Int): Int
    suspend fun clearAll()
}