package com.example.android_application.data.repository

import com.example.android_application.data.local.room.CompletionHistory
import com.example.android_application.data.local.room.PlayerDao
import com.example.android_application.data.local.room.PlayerProfile
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton

class PlayerRepositoryImpl @Inject constructor(
    private val playerDao: PlayerDao
) : PlayerRepository {

    override fun getProfile(): Flow<PlayerProfile?> = playerDao.getProfile()

    override suspend fun updateProfile(profile: PlayerProfile) {
        playerDao.saveProfile(profile)
    }

    override fun getHistory(): Flow<List<CompletionHistory>> = playerDao.getHistory()

    override fun getCompletedStageIds(): Flow<List<Int>> =
        playerDao.getCompletedStageIds()

    override fun getCompletedScenarioIds(): Flow<List<Int>> =
        playerDao.getCompletedScenarioIds()

    override suspend fun recordStageCompletion(
        scenarioId: Int,
        stageId: Int,
        stageNumber: Int,
        scoreEarned: Int,
        startedAt: Long,
        finishedAt: Long
    ) {
        val existing = playerDao.getHistoryForStage(stageId)
        if (existing != null) return

        playerDao.insertHistory(
            CompletionHistory(
                scenarioId = scenarioId,
                stageId = stageId,
                stageNumber = stageNumber,
                scoreEarned = scoreEarned,
                startedAt = startedAt,
                finishedAt = finishedAt
            )
        )

        val currentTotal = playerDao.getTotalScore() ?: 0
        val profile = playerDao.getProfileOnce() ?: PlayerProfile()
        playerDao.saveProfile(profile.copy(totalScore = currentTotal))
    }

    override suspend fun getTimeSpentMinutes(scenarioId: Int): Int =
        (playerDao.getTimeSpentMinutesForScenario(scenarioId) ?: 0L).toInt()

    override suspend fun getScoreForScenario(scenarioId: Int): Int =
        playerDao.getScoreForScenario(scenarioId) ?: 0

    override suspend fun clearAll() {
        playerDao.clearHistory()
        playerDao.saveProfile(PlayerProfile())
    }
}