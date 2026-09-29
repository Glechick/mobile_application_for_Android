package com.example.android_application.data.repository

import com.example.android_application.data.model.GameScenario
import com.example.android_application.data.model.GameStage

interface ScenarioRepository {
    fun getScenarios(): List<GameScenario>
    fun getStageById(stageId: Int): GameStage?
}