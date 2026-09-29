package com.example.android_application.data.repository

import com.example.android_application.data.mock.MockData
import com.example.android_application.data.model.GameScenario
import com.example.android_application.data.model.GameStage

class ScenarioRepositoryImpl(private val mockData: MockData) : ScenarioRepository {
    override fun getScenarios(): List<GameScenario> = mockData.scenarios

    override fun getStageById(stageId: Int): GameStage? =
        mockData.stages.firstOrNull { it.id == stageId }
}