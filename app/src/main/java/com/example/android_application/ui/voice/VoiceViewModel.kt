package com.example.android_application.ui.voice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_application.data.repository.PlayerRepository
import com.example.android_application.data.repository.ScenarioRepository
import com.example.android_application.data.voice.VoiceEvent
import com.example.android_application.data.voice.VoiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VoiceViewModel @Inject constructor(
    private val voiceRepository: VoiceRepository,
    private val playerRepository: PlayerRepository,
    private val scenarioRepository: ScenarioRepository
) : ViewModel() {

    private val _state = MutableStateFlow<VoiceUiState>(VoiceUiState.Idle)
    val state: StateFlow<VoiceUiState> = _state.asStateFlow()

    private var currentStageId: Int = -1
    private var expectedAnswer: String = ""

    init {
        viewModelScope.launch {
            voiceRepository.events.collect { event ->
                when (event) {
                    is VoiceEvent.ReadyForSpeech -> _state.value = VoiceUiState.Listening
                    is VoiceEvent.SpeechEnded -> _state.value = VoiceUiState.Processing
                    is VoiceEvent.FinalResult -> processAnswer(event.text)
                    is VoiceEvent.Error -> {
                        _state.value = VoiceUiState.Error(event.message)
                        voiceRepository.speak(event.message)
                    }
                    else -> {}
                }
            }
        }
    }

    fun startListening(stageId: Int, correctAnswer: String) {
        this.currentStageId = stageId
        this.expectedAnswer = correctAnswer.lowercase()
        voiceRepository.startListening()
    }

    fun stopListening() {
        voiceRepository.stopListening()
        _state.value = VoiceUiState.Idle
    }

    private fun processAnswer(spokenText: String) {
        val lowerText = spokenText.lowercase()
        val isCorrect = lowerText.contains(expectedAnswer)

        val verdict = if (isCorrect) {
            "Верно! Вы отгадали загадку."
        } else {
            "Не совсем. Попробуйте взять подсказку."
        }

        var nextStage: Int? = null

        if (isCorrect) {
            viewModelScope.launch {
                val stage = scenarioRepository.getStageById(currentStageId)
                if (stage != null) {
                    playerRepository.recordStageCompletion(
                        scenarioId = stage.scenarioId,
                        stageId = stage.id,
                        stageNumber = stage.stageNumber,
                        scoreEarned = stage.score,
                        startedAt = System.currentTimeMillis(),
                        finishedAt = System.currentTimeMillis()
                    )

                    val next = scenarioRepository.getNextStage(stage.scenarioId, stage.stageNumber)
                    nextStage = next?.id
                }
                _state.value = VoiceUiState.Success(spokenText, verdict, isCorrect, nextStage)
                voiceRepository.speak(verdict)
            }
        } else {
            _state.value = VoiceUiState.Success(spokenText, verdict, isCorrect, null)
            voiceRepository.speak(verdict)
        }
    }

    fun reset() {
        _state.value = VoiceUiState.Idle
        voiceRepository.stopSpeaking()
    }

    override fun onCleared() {
        super.onCleared()
        voiceRepository.destroy()
    }
}