package com.example.android_application.data.progress

object InMemoryProgress {
    private val completedStageIds = mutableSetOf<Int>()

    private val completedScenarioIds = mutableSetOf<Int>()

    fun isStageCompleted(stageId: Int): Boolean =
        stageId in completedStageIds

    fun markStageCompleted(stageId: Int) {
        completedStageIds.add(stageId)
    }

    fun isScenarioCompleted(scenarioId: Int): Boolean =
        scenarioId in completedScenarioIds

    fun markScenarioCompleted(scenarioId: Int) {
        completedScenarioIds.add(scenarioId)
    }

    fun reset() {
        completedStageIds.clear()
        completedScenarioIds.clear()
    }
}