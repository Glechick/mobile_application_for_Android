package com.example.android_application.data.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoiceRepositoryImpl @Inject constructor(
    private val context: Context
) : VoiceRepository, RecognitionListener, TextToSpeech.OnInitListener {

    private val _events = MutableSharedFlow<VoiceEvent>(
        extraBufferCapacity = 16,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val events: SharedFlow<VoiceEvent> = _events.asSharedFlow()

    private var speechRecognizer: SpeechRecognizer? = null
    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private val mainHandler = Handler(Looper.getMainLooper())

    init {
        // Инициализация TTS
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("ru", "RU"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                _events.tryEmit(VoiceEvent.Error(-1, "Русский язык не поддерживается TTS"))
            } else {
                isTtsReady = true
            }
        } else {
            _events.tryEmit(VoiceEvent.Error(-1, "Ошибка инициализации TTS"))
        }
    }

    override fun startListening() {
        mainHandler.post {
            if (speechRecognizer == null) {
                if (!SpeechRecognizer.isRecognitionAvailable(context)) {
                    _events.tryEmit(VoiceEvent.Error(-1, "Распознавание речи недоступно на устройстве"))
                    return@post
                }
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                    setRecognitionListener(this@VoiceRepositoryImpl)
                }
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ru-RU")
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            }
            speechRecognizer?.startListening(intent)
        }
    }

    override fun stopListening() {
        mainHandler.post {
            speechRecognizer?.stopListening()
        }
    }

    override fun speak(text: String) {
        if (isTtsReady) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utteranceId")
        }
    }

    override fun stopSpeaking() {
        tts?.stop()
    }

    override fun destroy() {
        mainHandler.post {
            speechRecognizer?.destroy()
            speechRecognizer = null
        }
        tts?.stop()
        tts?.shutdown()
        tts = null
    }

    override fun onReadyForSpeech(params: Bundle?) {
        _events.tryEmit(VoiceEvent.ReadyForSpeech)
    }

    override fun onEndOfSpeech() {
        _events.tryEmit(VoiceEvent.SpeechEnded)
    }

    override fun onResults(results: Bundle?) {
        val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
        if (text != null) {
            _events.tryEmit(VoiceEvent.FinalResult(text))
        }
    }

    override fun onPartialResults(partialResults: Bundle?) {
        val text = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
        if (text != null) {
            _events.tryEmit(VoiceEvent.PartialResult(text))
        }
    }

    override fun onError(error: Int) {
        val message = when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "Ошибка записи аудио"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Нет разрешения на микрофон"
            SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Ошибка сети"
            SpeechRecognizer.ERROR_NO_MATCH -> "Речь не распознана"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Микрофон занят другим приложением"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Время ожидания речи истекло"
            else -> "Неизвестная ошибка распознавания: $error"
        }
        _events.tryEmit(VoiceEvent.Error(error, message))
    }

    override fun onBeginningOfSpeech() {}
    override fun onRmsChanged(rmsdB: Float) {}
    override fun onBufferReceived(buffer: ByteArray?) {}
    override fun onEvent(eventType: Int, params: Bundle?) {}
}