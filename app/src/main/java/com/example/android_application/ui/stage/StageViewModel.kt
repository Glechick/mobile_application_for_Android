package com.example.android_application.ui.stage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_application.data.model.GameStage
import com.example.android_application.data.repository.PlayerRepository
import com.example.android_application.data.repository.ScenarioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StageUiState(
    val stage: GameStage? = null,
    val stageNumber: Int = 0,
    val totalStages: Int = 0,
    val isStageCompleted: Boolean = false,
    val isScenarioCompleted: Boolean = false,
    val isLoading: Boolean = true,
    val revealedHints: Set<Int> = emptySet(),
    val error: String? = null
)

@HiltViewModel
class StageViewModel @Inject constructor(
    private val scenarioRepository: ScenarioRepository,
    private val playerRepository: PlayerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StageUiState())
    val uiState: StateFlow<StageUiState> = _uiState.asStateFlow()

    fun loadFirstStage(scenarioId: Int) {
        viewModelScope.launch {
            val allStages = scenarioRepository.getStagesOfScenario(scenarioId)
            if (allStages.isEmpty()) {
                _uiState.value = StageUiState(
                    isLoading = false,
                    error = "Для этого сценария нет этапов"
                )
                return@launch
            }

            val completedIds = playerRepository.getCompletedStageIds().first().toSet()
            val firstNotCompleted = allStages.firstOrNull { it.id !in completedIds }
            val allDone = firstNotCompleted == null
            val stage = firstNotCompleted ?: allStages.last()

            _uiState.value = StageUiState(
                stage = stage,
                stageNumber = stage.stageNumber,
                totalStages = allStages.size,
                isStageCompleted = stage.id in completedIds,
                isScenarioCompleted = allDone,
                isLoading = false
            )
        }
    }

    fun loadStageById(stageId: Int) {
        viewModelScope.launch {
            val stage = scenarioRepository.getStageById(stageId)
            if (stage == null) {
                _uiState.value = StageUiState(
                    isLoading = false,
                    error = "Этап не найден"
                )
                return@launch
            }

            val allStages = scenarioRepository.getStagesOfScenario(stage.scenarioId)
            val completedIds = playerRepository.getCompletedStageIds().first().toSet()
            val allDone = allStages.isNotEmpty() && allStages.all { it.id in completedIds }

            _uiState.value = StageUiState(
                stage = stage,
                stageNumber = stage.stageNumber,
                totalStages = allStages.size,
                isStageCompleted = stage.id in completedIds,
                isScenarioCompleted = allDone,
                isLoading = false
            )
        }
    }

    fun revealHint(index: Int) {
        val current = _uiState.value
        _uiState.value = current.copy(
            revealedHints = current.revealedHints + index
        )
    }
}