package com.example.android_application.data.model

data class QuestProgress(
    val scenarioId: Int,
    val startedAt: Long,
    val finishedAt: Long?,
    val earnedScore: Int
)
