package com.example.android_application.domain

import com.example.android_application.data.llm.LlmRepository
import com.example.android_application.data.local.room.HintCache
import com.example.android_application.data.local.room.HintDao
import javax.inject.Inject

class GenerateHintsUseCase @Inject constructor(
    private val llmRepository: LlmRepository,
    private val hintDao: HintDao
) {
    suspend operator fun invoke(riddle: String, forceRefresh: Boolean = false): List<String> {
        if (!forceRefresh) {
            val cached = hintDao.getByRiddle(riddle)
            if (cached != null) {
                return cached.hintsJson.split("||")
            }
        }

        val rawResult = llmRepository.generateHints(riddle)

        val hints = rawResult.lines()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .map { it.removePrefix("1.").removePrefix("2.").removePrefix("3.").removePrefix("-").trim() }
            .take(3)

        hintDao.insert(
            HintCache(
                riddle = riddle,
                hintsJson = hints.joinToString("||"),
                createdAt = System.currentTimeMillis()
            )
        )
        return hints
    }
}