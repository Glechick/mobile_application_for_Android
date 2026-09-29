package com.example.android_application.ui.scanner

import androidx.lifecycle.ViewModel
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

    private var expectedCode: String? = null
    private var lastScanned: String? = null

    fun initStage(stageId: Int) {
        expectedCode = repository.getStageById(stageId)?.qrCode
    }

    fun onCodeScanned(code: String) {
        if (code == lastScanned) return   // игнорируем повторные срабатывания
        lastScanned = code

        val success = expectedCode != null && code == expectedCode
        _uiState.value = ScannerUiState(
            scannedCode = code,
            isSuccess = success,
            errorMessage = if (success) null else "Код не совпадает с ожидаемым"
        )
    }

    fun reset() {
        lastScanned = null
        _uiState.value = ScannerUiState()
    }
}