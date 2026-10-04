package com.example.android_application.ui.scanner

import androidx.lifecycle.ViewModel
import com.example.android_application.data.progress.InMemoryProgress
import com.example.android_application.data.repository.ScenarioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class ScannerUiState(
    val scannedCode: String? = null,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val repository: ScenarioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScannerUiState())
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()

    private var currentStageId: Int = -1
    private var expectedCode: String? = null
    private var nextStageId: Int? = null
    private var lastScanned: String? = null

    fun initStage(stageId: Int) {
        currentStageId = stageId
        val currentStage = repository.getStageById(stageId)
        expectedCode = currentStage?.qrCode
        nextStageId = currentStage?.let {
            repository.getNextStage(it.scenarioId, it.stageNumber)?.id
        }
    }

    fun onCodeScanned(code: String) {
        if (code == lastScanned) return
        lastScanned = code

        val success = expectedCode != null && code == expectedCode
        if (success) {
            InMemoryProgress.markStageCompleted(currentStageId)
            if (nextStageId == null) {
                repository.getStageById(currentStageId)?.let {
                    InMemoryProgress.markScenarioCompleted(it.scenarioId)
                }
            }
        }

        _uiState.value = ScannerUiState(
            scannedCode = code,
            isSuccess = success,
            errorMessage = if (success) null else "Ожидалось: '$expectedCode'"
        )
    }

    fun getNextStageId(): Int? = nextStageId

    fun reset() {
        lastScanned = null
        _uiState.value = ScannerUiState()
    }
}