package com.example.android_application.ui.voice

sealed interface VoiceUiState {
    object Idle : VoiceUiState
    object Listening : VoiceUiState
    object Processing : VoiceUiState
    data class Success(
        val recognizedText: String,
        val verdict: String,
        val isCorrect: Boolean,
        val nextStageId: Int?
    ) : VoiceUiState
    data class Error(val message: String) : VoiceUiState
}