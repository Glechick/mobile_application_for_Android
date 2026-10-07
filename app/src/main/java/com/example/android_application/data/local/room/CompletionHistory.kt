package com.example.android_application.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "completion_history")
data class CompletionHistory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val scenarioId: Int,
    val stageId: Int,
    val stageNumber: Int,
    val scoreEarned: Int,
    val startedAt: Long,
    val finishedAt: Long?
)