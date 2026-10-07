package com.example.android_application.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_application.data.model.GameScenario
import com.example.android_application.data.repository.PlayerRepository
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
    private val scenarioRepository: ScenarioRepository,
    private val playerRepository: PlayerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeProgress()
    }

    private fun observeProgress() {
        viewModelScope.launch {
            playerRepository.getCompletedStageIds().collect { completedStageIds ->
                rebuildState(completedStageIds.toSet())
            }
        }
    }

    private fun rebuildState(completedStageIds: Set<Int>) {
        viewModelScope.launch {
            val scenarios = scenarioRepository.getScenarios().map { scenario ->
                val stages = scenarioRepository.getStagesOfScenario(scenario.id)
                val earnedScore = stages
                    .filter { it.id in completedStageIds }
                    .sumOf { it.score }
                val allDone = stages.isNotEmpty() && stages.all { it.id in completedStageIds }
                val timeSpent = if (allDone) {
                    playerRepository.getTimeSpentMinutes(scenario.id)
                } else 0

                scenario.copy(
                    isCompleted = allDone,
                    earnedScore = earnedScore,
                    timeSpentMinutes = timeSpent
                )
            }
            _uiState.value = HomeUiState(
                scenarios = scenarios,
                totalScore = scenarios.sumOf { it.earnedScore },
                completedCount = scenarios.count { it.isCompleted },
                isLoading = false
            )
        }
    }
}