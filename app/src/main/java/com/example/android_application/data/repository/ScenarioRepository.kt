package com.example.android_application.data.repository

import com.example.android_application.data.model.GameScenario
import com.example.android_application.data.model.GameStage

interface ScenarioRepository {
    fun getScenarios(): List<GameScenario>
    suspend fun getFirstStageOfScenario(scenarioId: Int): GameStage?
    suspend fun getStageById(stageId: Int): GameStage?
    suspend fun getNextStage(scenarioId: Int, currentStageNumber: Int): GameStage?
    suspend fun getStagesOfScenario(scenarioId: Int): List<GameStage>
}