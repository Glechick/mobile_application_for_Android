package com.example.android_application.data.model

data class GameStage(
    val id: Int,
    val scenarioId: Int,
    val stageNumber: Int,
    val riddle: String,
    val hints: List<String>,
    val transitionCondition: String,
    val qrCode: String = "",
    val answerHash: String = "",
    val score: Int = 20
)