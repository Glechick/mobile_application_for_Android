package com.example.android_application.data.mock

import com.example.android_application.data.model.GameScenario

object MockData {
    val scenarios = listOf(
        GameScenario(
            id = 1,
            title = "Тайна старого парка",
            description = "Найдите 5 контрольных меток в парке Горького",
            maxScore = 100,
            estimatedMinutes = 45,
            isCompleted = true,
            earnedScore = 85,
            timeSpentMinutes = 38
        ),
        GameScenario(
            id = 2,
            title = "Квест по историческому центру",
            description = "Прогулка по старинным улицам с загадками",
            maxScore = 150,
            estimatedMinutes = 90
        ),
        GameScenario(
            id = 3,
            title = "Ночной дозор",
            description = "Испытание для смельчаков после заката",
            maxScore = 200,
            estimatedMinutes = 120
        )
    )
}