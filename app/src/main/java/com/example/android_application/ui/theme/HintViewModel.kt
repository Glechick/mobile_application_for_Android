package com.example.android_application.ui.hints

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_application.data.llm.LlmRepository
import com.example.android_application.data.local.room.HintCache
import com.example.android_application.data.local.room.HintDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HintViewModel @Inject constructor(
    private val llmRepository: LlmRepository,
    private val hintDao: HintDao
) : ViewModel() {

    private val _state = MutableStateFlow<LlmUiState>(LlmUiState.Idle)
    val state: StateFlow<LlmUiState> = _state.asStateFlow()

    fun generateHints(riddle: String) {
        viewModelScope.launch {
            val cached = hintDao.getByRiddle(riddle)
            if (cached != null) {
                _state.value = LlmUiState.Success(cached.hintsJson.split("||"))
                return@launch
            }

            _state.value = LlmUiState.Generating
            try {
                val rawResult = llmRepository.generateHints(riddle)

                val hints = rawResult.lines()
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                    .map {
                        it.removePrefix("1.").removePrefix("2.").removePrefix("3.")
                            .removePrefix("-").removePrefix("*").trim()
                    }
                    .filter { it.isNotBlank() }
                    .take(3)

                hintDao.insert(
                    HintCache(
                        riddle = riddle,
                        hintsJson = hints.joinToString("||"),
                        createdAt = System.currentTimeMillis()
                    )
                )

                _state.value = LlmUiState.Success(hints)
            } catch (e: Exception) {
                _state.value = LlmUiState.Error(e.message ?: "Ошибка генерации. Проверьте память устройства.")
            }
        }
    }
}