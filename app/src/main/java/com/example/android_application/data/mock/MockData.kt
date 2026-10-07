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
            estimatedMinutes = 45
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
        // === Сценарий 1 «Тайна старого парка» ===
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
            qrCode = "QUEST_PARK_001",
            score = 50
        ),
        GameStage(
            id = 2,
            scenarioId = 1,
            stageNumber = 2,
            riddle = "Я — старый свидетель истории, но молчу уже сто лет.",
            hints = listOf("Обратите внимание на памятники"),
            transitionCondition = "Найдите QR-код",
            qrCode = "QUEST_PARK_002",
            score = 50
        ),

        // === Сценарий 2 «Квест по историческому центру» ===
        GameStage(
            id = 3,
            scenarioId = 2,
            stageNumber = 1,
            riddle = "Я храню историю в своих стенах, но не могу рассказать её словами.",
            hints = listOf(
                "Здание на главной площади",
                "Ищите QR-код у входа"
            ),
            transitionCondition = "Найдите QR-код у входа в музей",
            qrCode = "QUEST_CENTER_001",
            score = 50
        ),
        GameStage(
            id = 4,
            scenarioId = 2,
            stageNumber = 2,
            riddle = "Я возвышаюсь над городом, меня видно с любого конца улицы.",
            hints = listOf(
                "Смотровая площадка",
                "Ищите QR-код у подножия"
            ),
            transitionCondition = "Найдите QR-код у башни",
            qrCode = "QUEST_CENTER_002",
            score = 50
        ),
        GameStage(
            id = 5,
            scenarioId = 2,
            stageNumber = 3,
            riddle = "Я помню голоса прошлого, но сегодня молчу.",
            hints = listOf("Старинный особняк"),
            transitionCondition = "Найдите QR-код у особняка",
            qrCode = "QUEST_CENTER_003",
            score = 50
        ),

        // === Сценарий 3 «Ночной дозор» ===
        GameStage(
            id = 6,
            scenarioId = 3,
            stageNumber = 1,
            riddle = "Я появляюсь, когда солнце уходит. Меня боятся, но и ждут.",
            hints = listOf(
                "Посмотрите на небо",
                "Ищите QR-код у фонаря"
            ),
            transitionCondition = "Найдите QR-код у старого фонаря",
            qrCode = "QUEST_NIGHT_001",
            score = 100
        ),
        GameStage(
            id = 7,
            scenarioId = 3,
            stageNumber = 2,
            riddle = "Я — тихий свидетель ночи, и только луна видит мои следы.",
            hints = listOf("Загляните в переулки"),
            transitionCondition = "Найдите QR-код в переулке",
            qrCode = "QUEST_NIGHT_002",
            score = 100
        )
    )
}