package com.example.android_application.data.mock

import com.example.android_application.data.model.GameScenario
import com.example.android_application.data.model.GameStage

class MockData {
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

    val stages = listOf(
        GameStage(
            id = 1,
            scenarioId = 1,
            stageNumber = 1,
            riddle = "Я стою в тени деревьев, где отдыхают люди. Меня легко найти, но сложно заметить.",
            hints = listOf(
                "Посмотрите на скамейки",
                "Рядом с фонтаном",
                "Ищите табличку с QR-кодом"
            ),
            transitionCondition = "Найдите QR-код",
            qrCode = "QUEST_PARK_001"
        ),
        GameStage(
            id = 2,
            scenarioId = 1,
            stageNumber = 2,
            riddle = "Я — старый свидетель истории, но молчу уже сто лет.",
            hints = listOf("Обратите внимание на памятники"),
            transitionCondition = "Найдите QR-код",
            qrCode = "QUEST_PARK_002"
        )
    )
}