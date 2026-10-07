package com.example.android_application.data.local.realm

import io.github.xilinjia.krdb.types.RealmObject
import io.github.xilinjia.krdb.types.annotations.PrimaryKey

open class GameStageEntity : RealmObject {
    @PrimaryKey
    var id: Int = 0
    var scenarioId: Int = 0
    var stageNumber: Int = 0
    var riddle: String = ""
    var hints: String = ""
    var transitionCondition: String = ""
    var answerHash: String = ""
    var score: Int = 0
}