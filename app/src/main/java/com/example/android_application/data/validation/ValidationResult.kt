package com.example.android_application.data.validation

sealed interface ValidationResult {
    data class Success(
        val stageId: Int,
        val nextStageId: Int?
    ) : ValidationResult

    data class Failure(
        val reason: String
    ) : ValidationResult
}