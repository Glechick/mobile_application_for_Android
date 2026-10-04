package com.example.android_application.data.repository

import com.example.android_application.data.mock.MockData
import com.example.android_application.data.model.GameScenario
import com.example.android_application.data.model.GameStage

class ScenarioRepositoryImpl(private val mockData: MockData) : ScenarioRepository {

    override fun getScenarios(): List<GameScenario> = mockData.scenarios

    override fun getFirstStageOfScenario(scenarioId: Int): GameStage? =
        mockData.stages
            .filter { it.scenarioId == scenarioId }
            .minByOrNull { it.stageNumber }

    override fun getStageById(stageId: Int): GameStage? =
        mockData.stages.firstOrNull { it.id == stageId }

    override fun getNextStage(scenarioId: Int, currentStageNumber: Int): GameStage? =
        mockData.stages
            .filter { it.scenarioId == scenarioId && it.stageNumber > currentStageNumber }
            .minByOrNull { it.stageNumber }

    override fun getStagesOfScenario(scenarioId: Int): List<GameStage> =
        mockData.stages
            .filter { it.scenarioId == scenarioId }
            .sortedBy { it.stageNumber }
}