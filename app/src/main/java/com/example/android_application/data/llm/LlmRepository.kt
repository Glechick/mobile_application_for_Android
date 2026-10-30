package com.example.android_application.data.llm

interface LlmRepository {
    suspend fun generateHints(riddle: String): String
    suspend fun ensureModelLoaded()
}