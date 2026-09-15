package com.example.android_application.ui.home

import androidx.lifecycle.ViewModel
import com.example.android_application.data.mock.MockData
import com.example.android_application.data.model.GameScenario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class HomeUiState(
    val scenarios: List<GameScenario> = emptyList(),
    val totalScore: Int = 0,
    val completedCount: Int = 0,
    val isLoading: Boolean = true
)


class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadScenarios()
    }

    private fun loadScenarios() {
        val scenarios = MockData.scenarios
        _uiState.value = HomeUiState(
            scenarios = scenarios,
            totalScore = scenarios.sumOf { it.earnedScore },
            completedCount = scenarios.count { it.isCompleted },
            isLoading = false
        )
    }
}