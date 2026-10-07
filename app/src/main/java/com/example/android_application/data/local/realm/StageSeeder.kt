package com.example.android_application.data.local.realm

import com.example.android_application.data.mock.MockData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StageSeeder @Inject constructor(
    private val realmStageRepository: RealmStageRepository,
    private val mockData: MockData
) {
    suspend fun seedIfEmpty() {
        if (!realmStageRepository.isEmpty()) return

        val entities = mockData.stages.map { stage ->
            GameStageEntity().apply {
                id = stage.id
                scenarioId = stage.scenarioId
                stageNumber = stage.stageNumber
                riddle = stage.riddle
                hints = stage.hints.joinToString("||")
                transitionCondition = stage.transitionCondition
                answerHash = HashUtils.sha256(stage.qrCode)
                score = stage.score
            }
        }
        realmStageRepository.insertAll(entities)
    }
}