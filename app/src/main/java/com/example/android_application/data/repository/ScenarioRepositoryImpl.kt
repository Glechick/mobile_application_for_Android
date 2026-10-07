package com.example.android_application.data.repository

import com.example.android_application.data.local.realm.RealmStageRepository
import com.example.android_application.data.mock.MockData
import com.example.android_application.data.model.GameScenario
import com.example.android_application.data.model.GameStage
import javax.inject.Inject

class ScenarioRepositoryImpl @Inject constructor(
    private val mockData: MockData,
    private val realmStageRepository: RealmStageRepository
) : ScenarioRepository {

    override fun getScenarios(): List<GameScenario> = mockData.scenarios

    override suspend fun getFirstStageOfScenario(scenarioId: Int): GameStage? =
        realmStageRepository.getFirstStageOfScenario(scenarioId)?.toGameStage()

    override suspend fun getStageById(stageId: Int): GameStage? =
        realmStageRepository.getStageById(stageId)?.toGameStage()

    override suspend fun getNextStage(
        scenarioId: Int,
        currentStageNumber: Int
    ): GameStage? =
        realmStageRepository.getNextStage(scenarioId, currentStageNumber)?.toGameStage()

    override suspend fun getStagesOfScenario(scenarioId: Int): List<GameStage> =
        realmStageRepository.getStagesOfScenarioSync(scenarioId).map { it.toGameStage() }
}

private fun com.example.android_application.data.local.realm.GameStageEntity.toGameStage() =
    GameStage(
        id = id,
        scenarioId = scenarioId,
        stageNumber = stageNumber,
        riddle = riddle,
        hints = if (hints.isEmpty()) emptyList() else hints.split("||"),
        transitionCondition = transitionCondition,
        answerHash = answerHash,
        score = score
    )