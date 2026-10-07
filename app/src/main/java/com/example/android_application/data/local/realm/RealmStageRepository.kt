package com.example.android_application.data.local.realm

import io.github.xilinjia.krdb.Realm
import io.github.xilinjia.krdb.ext.query
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealmStageRepository @Inject constructor(
    private val realm: Realm
) {

    suspend fun getStagesOfScenarioSync(scenarioId: Int): List<GameStageEntity> =
        realm.query<GameStageEntity>("scenarioId == $0", scenarioId)
            .find()
            .sortedBy { it.stageNumber }

    suspend fun getStageById(stageId: Int): GameStageEntity? =
        realm.query<GameStageEntity>("id == $0", stageId).first().find()

    suspend fun getFirstStageOfScenario(scenarioId: Int): GameStageEntity? =
        realm.query<GameStageEntity>("scenarioId == $0", scenarioId)
            .find()
            .minByOrNull { it.stageNumber }

    suspend fun getNextStage(
        scenarioId: Int,
        currentStageNumber: Int
    ): GameStageEntity? =
        realm.query<GameStageEntity>("scenarioId == $0", scenarioId)
            .find()
            .filter { it.stageNumber > currentStageNumber }
            .minByOrNull { it.stageNumber }

    suspend fun isEmpty(): Boolean =
        realm.query<GameStageEntity>().count().find() == 0L

    suspend fun insertAll(stages: List<GameStageEntity>) {
        realm.write {
            stages.forEach { copyToRealm(it) }
        }
    }
}