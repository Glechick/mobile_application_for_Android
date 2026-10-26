package com.example.android_application.ui.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_application.data.event.ScanEvent
import com.example.android_application.data.event.ScanEventBus
import com.example.android_application.data.validation.ScanValidator
import com.example.android_application.data.validation.ValidationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ScannerUiState(
    val scannedCode: String? = null,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val scanEventBus: ScanEventBus,
    private val scanValidator: ScanValidator
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScannerUiState())
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()

    private var currentStageId: Int = -1
    private var stageStartTime: Long = System.currentTimeMillis()
    private var lastScanned: String? = null
    private var nextStageId: Int? = null

    init {
        viewModelScope.launch {
            scanValidator.results.collect { result ->
                when (result) {
                    is ValidationResult.Success -> {
                        nextStageId = result.nextStageId
                        _uiState.update {
                            it.copy(isSuccess = true, errorMessage = null)
                        }
                    }
                    is ValidationResult.Failure -> {
                        _uiState.update {
                            it.copy(isSuccess = false, errorMessage = result.reason)
                        }
                    }
                }
            }
        }
    }

    fun initStage(stageId: Int) {
        currentStageId = stageId
        stageStartTime = System.currentTimeMillis()
    }

    fun onCodeScanned(code: String) {
        if (code == lastScanned) return
        lastScanned = code
        _uiState.update { it.copy(scannedCode = code, isSuccess = false, errorMessage = null) }
        viewModelScope.launch {
            scanEventBus.publish(
                ScanEvent.CodeDetected(
                    stageId = currentStageId,
                    rawCode = code,
                    startedAt = stageStartTime
                )
            )
        }
    }

    fun getNextStageId(): Int? = nextStageId

    fun reset() {
        lastScanned = null
        nextStageId = null
        _uiState.value = ScannerUiState()
    }
}