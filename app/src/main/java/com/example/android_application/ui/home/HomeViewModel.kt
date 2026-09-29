package com.example.android_application.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_application.data.model.GameScenario
import com.example.android_application.data.repository.ScenarioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val scenarios: List<GameScenario> = emptyList(),
    val totalScore: Int = 0,
    val completedCount: Int = 0,
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ScenarioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadScenarios()
    }

    private fun loadScenarios() {
        viewModelScope.launch {
            val scenarios = repository.getScenarios()
            _uiState.value = HomeUiState(
                scenarios = scenarios,
                totalScore = scenarios.sumOf { it.earnedScore },
                completedCount = scenarios.count { it.isCompleted },
                isLoading = false
            )
        }
    }
}