package com.example.android_application.ui.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_application.data.local.realm.HashUtils
import com.example.android_application.data.repository.PlayerRepository
import com.example.android_application.data.repository.ScenarioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ScannerUiState(
    val scannedCode: String? = null,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val scenarioRepository: ScenarioRepository,
    private val playerRepository: PlayerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScannerUiState())
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()

    private var currentStageId: Int = -1
    private var expectedHash: String? = null
    private var nextStageId: Int? = null
    private var lastScanned: String? = null
    private var stageStartTime: Long = System.currentTimeMillis()

    fun initStage(stageId: Int) {
        currentStageId = stageId
        stageStartTime = System.currentTimeMillis()
        viewModelScope.launch {
            val currentStage = scenarioRepository.getStageById(stageId)
            expectedHash = currentStage?.answerHash
            nextStageId = currentStage?.let {
                scenarioRepository.getNextStage(it.scenarioId, it.stageNumber)?.id
            }
        }
    }

    fun onCodeScanned(code: String) {
        if (code == lastScanned) return
        lastScanned = code

        val success = expectedHash != null && HashUtils.sha256(code) == expectedHash
        if (success) {
            viewModelScope.launch {
                val currentStage = scenarioRepository.getStageById(currentStageId)
                if (currentStage != null) {
                    playerRepository.recordStageCompletion(
                        scenarioId = currentStage.scenarioId,
                        stageId = currentStage.id,
                        stageNumber = currentStage.stageNumber,
                        scoreEarned = currentStage.score,
                        startedAt = stageStartTime,
                        finishedAt = System.currentTimeMillis()
                    )
                }
            }
        }

        _uiState.value = ScannerUiState(
            scannedCode = code,
            isSuccess = success,
            errorMessage = if (success) null else "Код не совпадает с ожидаемым"
        )
    }

    fun getNextStageId(): Int? = nextStageId

    fun reset() {
        lastScanned = null
        _uiState.value = ScannerUiState()
    }
}