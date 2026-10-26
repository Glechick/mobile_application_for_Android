package com.example.android_application.data.validation

import com.example.android_application.data.event.ScanEvent
import com.example.android_application.data.event.ScanEventBus
import com.example.android_application.data.local.realm.HashUtils
import com.example.android_application.data.repository.PlayerRepository
import com.example.android_application.data.repository.ScenarioRepository
import com.example.android_application.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScanValidator @Inject constructor(
    private val eventBus: ScanEventBus,
    private val scenarioRepository: ScenarioRepository,
    private val playerRepository: PlayerRepository,
    @ApplicationScope private val scope: CoroutineScope
) {

    private val _results = MutableSharedFlow<ValidationResult>(extraBufferCapacity = 16)
    val results: SharedFlow<ValidationResult> = _results.asSharedFlow()

    init {
        scope.launch {
            eventBus.events.collect { event ->
                when (event) {
                    is ScanEvent.CodeDetected -> handleCodeDetected(event)
                }
            }
        }
    }

    private suspend fun handleCodeDetected(event: ScanEvent.CodeDetected) {
        val stage = scenarioRepository.getStageById(event.stageId)
        if (stage == null) {
            _results.emit(ValidationResult.Failure("Этап не найден"))
            return
        }

        val scannedHash = HashUtils.sha256(event.rawCode)
        if (scannedHash != stage.answerHash) {
            _results.emit(ValidationResult.Failure("Код не совпадает с ожидаемым"))
            return
        }

        // верификация пройдена — пишем прогресс в Room
        playerRepository.recordStageCompletion(
            scenarioId = stage.scenarioId,
            stageId = stage.id,
            stageNumber = stage.stageNumber,
            scoreEarned = stage.score,
            startedAt = event.startedAt,
            finishedAt = System.currentTimeMillis()
        )

        val nextStage = scenarioRepository.getNextStage(
            stage.scenarioId,
            stage.stageNumber
        )

        _results.emit(ValidationResult.Success(stage.id, nextStage?.id))
    }
}