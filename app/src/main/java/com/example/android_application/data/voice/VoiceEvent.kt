package com.example.android_application.data.voice

sealed interface VoiceEvent {
    object ReadyForSpeech : VoiceEvent
    object SpeechEnded : VoiceEvent
    data class PartialResult(val text: String) : VoiceEvent
    data class FinalResult(val text: String) : VoiceEvent
    data class Error(val code: Int, val message: String) : VoiceEvent
}