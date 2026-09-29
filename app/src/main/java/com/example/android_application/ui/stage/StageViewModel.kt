package com.example.android_application.ui.stage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_application.data.model.GameStage
import com.example.android_application.data.repository.ScenarioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StageUiState(
    val stage: GameStage? = null,
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

    fun loadStage(stageId: Int) {
        viewModelScope.launch {
            try {
                val stage = repository.getStageById(stageId)
                _uiState.value = StageUiState(stage = stage, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = StageUiState(
                    isLoading = false,
                    error = e.message ?: "Не удалось загрузить этап"
                )
            }
        }
    }

    fun revealHint(index: Int) {
        val current = _uiState.value
        _uiState.value = current.copy(
            revealedHints = current.revealedHints + index
        )
    }
}