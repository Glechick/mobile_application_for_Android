package com.example.android_application.data.model

import android.media.MediaDescription

data class GameScenario(
    val id: Int,
    val title: String,
    val description: String,
    val maxScore: Int,
    val estimatedMinutes: Int,
    val isCompleted: Boolean = false,
    val earnedScore: Int = 0,
    val timeSpentMinutes: Int = 0
)
