package com.example.android_application.ui.hints

sealed interface LlmUiState {
    object Idle : LlmUiState
    object LoadingModel : LlmUiState
    object Generating : LlmUiState
    data class Success(val hints: List<String>) : LlmUiState
    data class Error(val message: String) : LlmUiState
}