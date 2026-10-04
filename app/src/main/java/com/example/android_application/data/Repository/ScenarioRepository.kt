package com.example.android_application.data.repository

import com.example.android_application.data.model.GameScenario
import com.example.android_application.data.model.GameStage

interface ScenarioRepository {
    fun getScenarios(): List<GameScenario>
    fun getFirstStageOfScenario(scenarioId: Int): GameStage?
    fun getStageById(stageId: Int): GameStage?
    fun getNextStage(scenarioId: Int, currentStageNumber: Int): GameStage?
    fun getStagesOfScenario(scenarioId: Int): List<GameStage>
}