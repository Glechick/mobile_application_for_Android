package com.example.android_application.ui.stage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_application.data.model.GameStage
import com.example.android_application.data.progress.InMemoryProgress
import com.example.android_application.data.repository.ScenarioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StageUiState(
    val stage: GameStage? = null,
    val stageNumber: Int = 0,
    val totalStages: Int = 0,
    val isStageCompleted: Boolean = false,
    val isLoading: Boolean = true,
    val revealedHints: Set<Int> = emptySet(),
    val error: String? = null
)

@HiltViewModel
class StageViewModel @Inject constructor(
    private val repository: ScenarioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StageUiState())
    val uiState: StateFlow<StageUiState> = _uiState.asStateFlow()

    /**
     * startId — либо scenarioId (если пришли с главного),
     * либо stageId (если пришли из сканера).
     */
    fun loadStage(startId: Int) {
        viewModelScope.launch {
            val stage: GameStage? = repository.getStageById(startId)
                ?: repository.getFirstStageOfScenario(startId)

            if (stage == null) {
                _uiState.value = StageUiState(
                    isLoading = false,
                    error = "Для этого сценария нет этапов"
                )
                return@launch
            }

            val allStages = repository.getStagesOfScenario(stage.scenarioId)
            _uiState.value = StageUiState(
                stage = stage,
                stageNumber = stage.stageNumber,
                totalStages = allStages.size,
                isStageCompleted = InMemoryProgress.isStageCompleted(stage.id),
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