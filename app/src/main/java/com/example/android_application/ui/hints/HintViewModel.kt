package com.example.android_application.ui.hints

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_application.domain.GenerateHintsUseCase
import com.example.android_application.util.DeviceCompatibilityChecker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HintViewModel @Inject constructor(
    private val generateHintsUseCase: GenerateHintsUseCase,
    private val checker: DeviceCompatibilityChecker
) : ViewModel() {

    private val _state = MutableStateFlow<LlmUiState>(LlmUiState.Idle)
    val state: StateFlow<LlmUiState> = _state.asStateFlow()

    fun generateHints(riddle: String, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            val compat = checker.check()
            if (!compat.isCompatible) {
                _state.value = LlmUiState.Error(compat.reason ?: "Устройство несовместимо")
                return@launch
            }
            _state.value = LlmUiState.Generating
            try {
                val hints = generateHintsUseCase(riddle, forceRefresh)
                _state.value = LlmUiState.Success(hints)
            } catch (e: Exception) {
                _state.value = LlmUiState.Error(e.message ?: "Ошибка генерации")
            }
        }
    }
}